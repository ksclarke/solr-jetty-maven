package info.freelibrary.maven;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.apache.commons.io.FileUtils;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Deletes the Solr core associated with the supplied core name.
 * <p/>
 * 
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
@Mojo(name = "delete-core")
public class DeleteSolrCoreMojo extends AbstractMojo {

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
		File config = new File(targetDir, "solr.xml");
		File coreDir = new File(targetDir, myCore);

		if (myCore.equals("collection1")) {
			throw new MojoExecutionException(
					"Sorry, but you can't delete the default core");
		}

		try {
			FileUtils.deleteDirectory(coreDir);
		}
		catch (IOException details) {
			throw new MojoExecutionException(
					"Unable to delete the requested core directory: "
							+ coreDir.getAbsolutePath(), details);
		}

		if (config.exists()) {
			try {
				Document doc = new Builder().build(config);
				Element root = doc.getRootElement();
				Nodes nodes = root.query("//core[@name='" + myCore + "']");

				for (int index = 0; index < nodes.size(); index++) {
					nodes.get(index).detach();
				}

				FileOutputStream xmlOut = new FileOutputStream(config);
				Serializer serializer = new Serializer(xmlOut);

				// Update the solr.xml configuration
				serializer.setIndent(4);
				serializer.write(doc);
				xmlOut.close();
			}
			catch (ParsingException | IOException details) {
				throw new MojoExecutionException(
						"Can't write to the solr.xml file", details);
			}
		}
		else if (getLog().isWarnEnabled()) {
			getLog().warn("The solr.xml file isn't currently being used");
		}
	}

}
