package com.github.ngyewch.unity.packager;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class UnityPackageUnpacker implements Closeable {

    private final File tempDirectory;
    private final List<Entry> entries = new ArrayList<>();

    public UnityPackageUnpacker(File inputFile) throws IOException {
        super();

        tempDirectory = new File("unitypackage-", "");
        tempDirectory.delete();

        try (final TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(
                new GZIPInputStream(new FileInputStream(inputFile)))) {
            while (true) {
                final TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();
                if (entry == null) {
                    break;
                }
                if (entry.isFile()) {
                    final File file = new File(tempDirectory, entry.getName());
                    file.getParentFile().mkdirs();
                    try (final OutputStream outputStream = new FileOutputStream(file)) {
                        IOUtils.copy(tarArchiveInputStream, outputStream);
                    }
                }
            }
        }

        final File[] dirs = tempDirectory.listFiles();
        if (dirs != null) {
            for (final File dir : dirs) {
                if (dir.isDirectory()) {
                    final File pathnameFile = new File(dir, "pathname");
                    final File metaFile = new File(dir, "asset.meta");
                    final File file = new File(dir, "asset");
                    if (pathnameFile.isFile() && metaFile.isFile()) {
                        final String pathname = FileUtils.readFileToString(pathnameFile, "UTF-8");
                        if (file.isFile()) {
                            entries.add(new Entry(pathname, file, metaFile));
                        } else {
                            entries.add(new Entry(pathname, null, metaFile));
                        }
                    }
                }
            }
        }
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void close() throws IOException {
        FileUtils.deleteDirectory(tempDirectory);
    }

    public static void unpack(File inputFile, File outputDirectory)
            throws IOException {
        try (final UnityPackageUnpacker unpacker = new UnityPackageUnpacker(inputFile)) {
            for (final Entry entry : unpacker.getEntries()) {
                final File file = new File(outputDirectory, entry.getPathname());
                final File metaFile = new File(outputDirectory, entry.getPathname() + ".meta");
                metaFile.getParentFile().mkdirs();
                FileUtils.copyFile(entry.getMetaFile(), metaFile);
                if (entry.getFile() != null) {
                    file.getParentFile().mkdirs();
                    FileUtils.copyFile(entry.getFile(), file);
                } else {
                    file.mkdirs();
                }
            }
        }
    }

    private static class Entry {

        private final String pathname;
        private final File file;
        private final File metaFile;

        public Entry(String pathname, File file, File metaFile) {
            super();

            this.pathname = pathname;
            this.file = file;
            this.metaFile = metaFile;
        }

        public String getPathname() {
            return pathname;
        }

        public File getFile() {
            return file;
        }

        public File getMetaFile() {
            return metaFile;
        }
    }
}
