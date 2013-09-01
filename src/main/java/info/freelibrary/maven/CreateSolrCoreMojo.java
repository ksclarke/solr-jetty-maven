
package info.freelibrary.maven;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

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
        String targetDir = myProject.getBuild().getOutputDirectory() + "/solr";
        File template = new File(targetDir, "collection1");
        File coreDir = new File(targetDir, myCore);
        File baseDir = myProject.getBasedir();
        File sourceDir = new File(baseDir, "src/main/resources/solr/" + myCore);

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

        try {
            // First copy it into our target directory (where it's run from)
            FileUtils.copyDirectory(template, coreDir, new SolrCoreFilter());
            
            // Then copy it into our source directory for version control
            FileUtils.copyDirectory(coreDir, sourceDir);
        } catch (IOException details) {
            coreDir.delete(); // clean up if we weren't successful
            throw new MojoExecutionException(
                    "Can't copy the template directory", details);
        }
    }

    /**
     * A filter that allows everything but the Solr data directory to be copied
     * to create a new Solr core.
     * 
     * @author Kevin S. Clarke <ksclarke@gmail.com>
     */
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
