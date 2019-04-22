package com.github.ngyewch.unity.packager.gradle;

import com.github.ngyewch.unity.packager.UnityPackageUnpacker;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.File;
import java.io.IOException;

public class UnityUnpackerTask extends DefaultTask {

    private File inputFile;
    private File outputDirectory;

    @InputFile
    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @TaskAction
    public void unpack() {
        try {
            UnityPackageUnpacker.unpack(inputFile, outputDirectory);
        } catch (IOException e) {
            throw new TaskExecutionException(this, e);
        }
    }
}
