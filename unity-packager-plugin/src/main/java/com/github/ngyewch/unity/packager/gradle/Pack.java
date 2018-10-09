package com.github.ngyewch.unity.packager.gradle;

import com.github.ngyewch.unity.packager.UnityPackagePacker;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Pack extends DefaultTask {

    private File projectDirectory;
    private File outputFile;
    private List<String> includes;
    private List<String> excludes;
    private boolean includeMeta;

    @InputDirectory
    public File getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(File projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    @OutputFile
    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    @Input
    @Optional
    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    @Input
    @Optional
    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    @Input
    @Optional
    public boolean isIncludeMeta() {
        return includeMeta;
    }

    public void setIncludeMeta(boolean includeMeta) {
        this.includeMeta = includeMeta;
    }

    @TaskAction
    public void pack() throws IOException {
        new UnityPackagePacker.Builder()
                .withProjectDirectory(projectDirectory)
                .withOutputFile(outputFile)
                .withIncludes(includes)
                .withExcludes(excludes)
                .withIncludeMeta(includeMeta)
                .pack();
    }
}
