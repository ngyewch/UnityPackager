package com.github.ngyewch.unity.packager.gradle;

import com.github.ngyewch.unity.packager.UnityPackagePacker;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UnityPackerTask extends DefaultTask {

    private File projectDirectory;
    private File outputFile;
    private String includes;
    private String excludes;

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
    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    @Input
    @Optional
    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    @TaskAction
    public void pack() {
        try {
            new UnityPackagePacker.Builder()
                    .withProjectDirectory(projectDirectory)
                    .withOutputFile(outputFile)
                    .withIncludes(toList(includes))
                    .withExcludes(toList(excludes))
                    .pack();
        } catch (IOException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    private static List<String> toList(String s) {
        if (s == null) {
            return null;
        }
        return Arrays.asList(StringUtils.split(s, ","));
    }
}
