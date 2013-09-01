
package info.freelibrary.maven;

import java.io.File;

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
        String targetDir = myProject.getBuild().getOutputDirectory() + "/solr";
        File coreDir = new File(targetDir, myCore);
        File baseDir = myProject.getBasedir();
        File sourceDir = new File(baseDir, "src/main/resources/solr/" + myCore);

        if (myCore.equals("collection1")) {
            throw new MojoExecutionException(
                    "Sorry, but you can't delete the default core");
        }

        try {
            FileUtils.deleteDirectory(coreDir);
            FileUtils.deleteDirectory(sourceDir);
        } catch (IOException details) {
            throw new MojoExecutionException(
                    "Unable to delete the requested core directory: " +
                            coreDir.getAbsolutePath(), details);
        }
    }

}
