package com.github.ngyewch.unity.packager.app;

import com.github.ngyewch.unity.packager.UnityPackagePacker;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Packer {

    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption("p", "project", true, "[REQUIRED] Unity project path.");
        options.addOption("o", "output", true, "[REQUIRED] Output file.");
        options.addOption("i", "includes", true, "Include patterns (comma-separated).");
        options.addOption("x", "excludes", true, "Exclude patterns (comma-separated).");
        options.addOption("h", "help", false, "Show this help message.");

        final CommandLineParser commandLineParser = new DefaultParser();
        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);
            if (commandLine.hasOption('h')) {
                showHelp(options);
                System.exit(0);
            }
            if (!commandLine.hasOption('p') || !commandLine.hasOption('o')) {
                showHelp(options);
                System.exit(1);
            }
            final File projectDirectory = new File(commandLine.getOptionValue('p'));
            if (!projectDirectory.isDirectory()) {
                showHelp(options);
                System.exit(1);
            }
            final File outputFile = new File(commandLine.getOptionValue('o'));
            final List<String> includes = toList(commandLine.getOptionValue("i"));
            final List<String> excludes = toList(commandLine.getOptionValue("x"));

            new UnityPackagePacker.Builder()
                    .withProjectDirectory(projectDirectory)
                    .withOutputFile(outputFile)
                    .withIncludes(includes)
                    .withExcludes(excludes)
                    .pack();
        } catch (ParseException e) {
            showHelp(options);
            System.exit(1);
        }
    }

    private static List<String> toList(String s) {
        if (s == null) {
            return null;
        }
        return Arrays.asList(StringUtils.split(s, ","));
    }

    private static void showHelp(Options options) {
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("packer", options);
    }
}
