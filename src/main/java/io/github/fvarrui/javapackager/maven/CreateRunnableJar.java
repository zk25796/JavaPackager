package io.github.fvarrui.javapackager.maven;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.File;
import java.util.function.Function;

import org.apache.maven.plugin.MojoExecutionException;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import io.github.fvarrui.javapackager.packagers.Packager;
import io.github.fvarrui.javapackager.utils.Logger;

/**
 * Creates a runnable jar file from sources
 */
public class CreateRunnableJar implements Function<Packager, File> {
	
	@Override
	public File apply(Packager packager) {
		Logger.infoIndent("Creating runnable JAR...");
		
		String classifier = "runnable";
		String name = packager.getName();
		String version = packager.getVersion();
		String mainClass = packager.getMainClass();
		File outputDirectory = packager.getOutputDirectory();
		ExecutionEnvironment env = MavenContext.getEnv();

		File jarFile = new File(outputDirectory, name + "-" + version + "-" + classifier + ".jar");

		try {
			
			executeMojo(
					plugin(
							groupId("org.apache.maven.plugins"),
							artifactId("maven-jar-plugin"), 
							version("3.1.1")
					),
					goal("jar"),
					configuration(
							element("classifier", classifier),
							element("archive", 
									element("manifest", 
											element("addClasspath", "true"),
											element("classpathPrefix", "libs/"),
											element("mainClass", mainClass)
									)
							),
							element("outputDirectory", jarFile.getParentFile().getAbsolutePath()),
							element("finalName", name + "-" + version)
					),
					env);

			Logger.infoUnindent("Runnable jar created in " + jarFile.getAbsolutePath() + "!");

		} catch (MojoExecutionException e) {

			Logger.error("Runnable jar creation failed! " + e.getMessage());
			throw new RuntimeException(e);
			
		}
		
		return jarFile;
	}
	
}
