
package info.freelibrary;

import java.util.Arrays;

import java.util.List;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.it.Verifier;

import org.junit.Test;

public class SolrJettyMavenIntegrationTest {

    // Maven settings.xml file to be used for the test projects
    private static final String SETTINGS = "/settings.xml";

    // Solr-Jetty-Maven version (so we don't have to manually keep in sync)
    private static final String TESTED_VERSION = "tested.project.version";

    private static final String GENERATED_CORE = "testCore";

    private static final String GENERATED_SCHEMA = "target/classes/solr/" +
            GENERATED_CORE + "/conf";

    /**
     * Tests that the CreateSolrCoreMojo creates the desired Solr core.
     * 
     * @throws Exception If the test fails as a result of an exception
     */
    @Test
    public void testCreateSolrCoreMojo() throws Exception {
        Verifier verifier = getVerifier("mojo-tests-project");
        Properties properties = verifier.getSystemProperties();
        List<String> goals =
                Arrays.asList(new String[] {"resources:resources",
                        "info.freelibrary:solr-jetty-maven:create-core"});

        properties.put("solr.core.name", GENERATED_CORE);

        verifier.displayStreamBuffers();
        verifier.executeGoals(goals);
        verifier.resetStreams();
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(GENERATED_SCHEMA);
    }
    
    /**
     * Tests that the DeleteSolrCoreMojo deletes the desired Solr core.
     * 
     * @throws Exception If the test fails as a result of an exception
     */
    @Test
    public void testDeleteSolrCoreMojo() throws Exception {
        Verifier verifier = getVerifier("mojo-tests-project");
        Properties properties = verifier.getSystemProperties();

        properties.put("solr.core.name", GENERATED_CORE);

        verifier.displayStreamBuffers();
        verifier.executeGoal("info.freelibrary:solr-jetty-maven:delete-core");
        verifier.resetStreams();
        verifier.verifyErrorFreeLog();
        verifier.assertFileNotPresent(GENERATED_SCHEMA);
    }

    /**
     * Returns a <code>Verifier</code> that has been configured to use the test
     * repository along with the test project that was passed in as a variable.
     * <p/>
     * 
     * @param aTestName The test project to run
     * @return A configured <code>Verifier</code>
     * @throws IOException If there is a problem with configuration.
     * @throws VerificationException If there is a problem with verification.
     */
    protected Verifier getVerifier(String aTestName) throws IOException,
            VerificationException {
        Class<? extends SolrJettyMavenIntegrationTest> cls = getClass();
        String name = aTestName.startsWith("/") ? aTestName : "/" + aTestName;
        File config = ResourceExtractor.simpleExtractResources(cls, SETTINGS);
        File test = ResourceExtractor.simpleExtractResources(cls, name);
        String solrVersion = System.getProperty(TESTED_VERSION);
        String absoluteTestPath = test.getAbsolutePath();
        String settings = config.getAbsolutePath();

        // Construct a verifier that will run our integration tests
        Verifier verifier = new Verifier(absoluteTestPath, settings, true);
        Properties verProperties = verifier.getVerifierProperties();
        Properties sysProperties = verifier.getSystemProperties();

        // We need to pass along the version number of our parent project
        sysProperties.setProperty(TESTED_VERSION, solrVersion);

        // use.mavenRepoLocal instructs forked tests to use the local repo
        verProperties.setProperty("use.mavenRepoLocal", "true");

        return verifier;
    }
}
