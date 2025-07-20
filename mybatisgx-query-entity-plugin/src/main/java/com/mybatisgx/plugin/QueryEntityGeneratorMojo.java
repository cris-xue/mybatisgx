package com.mybatisgx.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
        name = "generate-query-entities",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class QueryEntityGeneratorMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("MyBatisGX Query Entity Generator");
    }
}