package com.github.ngyewch.unity.packager.app;

import com.github.ngyewch.unity.packager.UnityPackageUnpacker;
import org.apache.commons.cli.*;

import java.io.File;

public class Unpacker {

    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption("i", "includes", true, "[REQUIRED] Unity package file.");
        options.addOption("o", "output", true, "[REQUIRED] Output directory.");
        options.addOption("h", "help", false, "Show this help message.");

        final CommandLineParser commandLineParser = new DefaultParser();
        try {
            final CommandLine commandLine = commandLineParser.parse(options, args);
            if (commandLine.hasOption('h')) {
                showHelp(options);
                System.exit(0);
            }
            if (!commandLine.hasOption('i') || !commandLine.hasOption('o')) {
                showHelp(options);
                System.exit(1);
            }
            final File unityPackageFile = new File(commandLine.getOptionValue('i'));
            if (!unityPackageFile.isFile()) {
                showHelp(options);
                System.exit(1);
            }
            final File outputDirectory = new File(commandLine.getOptionValue('o'));

            UnityPackageUnpacker.unpack(unityPackageFile, outputDirectory);
        } catch (ParseException e) {
            showHelp(options);
            System.exit(1);
        }
    }

    private static void showHelp(Options options) {
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("unpacker", options);
    }
}
