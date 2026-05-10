package io.github.neczpal.restdsl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MyMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "inputFile", required = true)
    private File inputFile;

    @Parameter(property = "generatorName", required = true)
    private String generatorName;

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/rest-dsl")
    private File outputDirectory;

    @Parameter(property = "packageName")
    private String packageName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!inputFile.exists()) {
            throw new MojoExecutionException("Input file does not exist: " + inputFile.getAbsolutePath());
        }

        try {
            getLog().info("Reading input file: " + inputFile.getAbsolutePath());
            getLog().info("Generating " + generatorName + " sources...");
            
            RestDslGenerator.generate(
                    inputFile.getAbsolutePath(), 
                    generatorName, 
                    packageName, 
                    outputDirectory.getAbsolutePath()
            );

            getLog().info("Successfully generated " + generatorName + " spec to " + outputDirectory.getAbsolutePath());

            // Add generated sources to the Maven project so they are compiled
            if ("spring".equalsIgnoreCase(generatorName) || "restclient".equalsIgnoreCase(generatorName)) {
                 if (project != null) {
                     project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
                     getLog().info("Added " + outputDirectory.getAbsolutePath() + " to compile source roots");
                 }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error processing file: " + e.getMessage(), e);
        }
    }
}
