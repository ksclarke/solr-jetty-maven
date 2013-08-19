
package info.freelibrary.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Iterator;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.apache.commons.io.FileUtils;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Creates a new core using the default collection1 core as a template.
 * <p/>
 * 
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
@Mojo(name = "create-core")
public class CreateSolrCoreMojo extends AbstractMojo {

    /**
     * The Maven project directory.
     */
    @Component
    private MavenProject myProject;

    /**
     * The name of the new core to create.
     */
    @Parameter(property = "solr.core.name", required = true)
    private String myCore;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String targetDir = myProject.getBuild().getOutputDirectory();
        File template = new File(targetDir, "collection1");
        File config = new File(targetDir, "solr.xml");
        File coreDir = new File(targetDir, myCore);

        if (myCore.equals("collection1")) {
            throw new MojoExecutionException(
                    "The default Solr collection can not be overwritten");
        }

        if (coreDir.exists()) {
            throw new MojoExecutionException(
                    "Can't create core because directory already exists");
        }

        if (!coreDir.mkdirs()) {
            throw new MojoExecutionException(
                    "Unable to create directory with the supplied core name");
        }

        // If solr.xml doesn't exist in solr.home, copy it over so we can use it
        if (!config.exists()) {
            Iterator<Resource> iterator = myProject.getResources().iterator();

            while (iterator.hasNext()) {
                Resource resource = iterator.next();
                File source = new File(resource.getDirectory(), "solr.xml");

                if (source.exists()) {
                    try {
                        FileUtils.copyFile(source, config);
                    } catch (IOException details) {
                        throw new MojoExecutionException(details.getMessage());
                    }
                } else {
                    throw new MojoFailureException("Missing solr.xml resource");
                }
            }
        }

        try {
            Document doc = new Builder().build(config);
            Element root = doc.getRootElement();
            Nodes nodes = root.query("//core[@name='" + myCore + "']");

            if (nodes.size() != 0) {
                coreDir.delete();
                throw new MojoExecutionException(myCore +
                        " already exists in the solr.xml file");
            } else {
                Elements elements = root.getChildElements("cores");
                Element cores = elements.get(0);
                Element core = new Element("core");

                core.addAttribute(new Attribute("name", myCore));
                core.addAttribute(new Attribute("instanceDir", myCore));
                cores.appendChild(core);
            }

            FileOutputStream xmlOut = new FileOutputStream(config);
            Serializer serializer = new Serializer(xmlOut);

            // Update the solr.xml configuration
            serializer.setIndent(4);
            serializer.write(doc);
            xmlOut.close();
        } catch (IOException | ParsingException details) {
            coreDir.delete();
            throw new MojoExecutionException("Error writing the solr.xml file",
                    details);
        }

        try {
            SolrCoreFilter filter = new SolrCoreFilter();
            FileUtils.copyDirectory(template, coreDir, filter);
        } catch (IOException details) {
            coreDir.delete();
            throw new MojoExecutionException(
                    "Can't copy the template directory", details);
        }
    }

    private class SolrCoreFilter implements FileFilter {

        @Override
        public boolean accept(File aFile) {
            if (!aFile.getName().equals("data")) {
                return true;
            }

            return false;
        }

    }
}
