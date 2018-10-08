package com.github.ngyewch.unity.packager;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class UnityPackagePacker implements Closeable {

    private final TarArchiveOutputStream tarArchiveOutputStream;
    private final Yaml yaml;

    private UnityPackagePacker(File outputFile)
            throws IOException {
        super();

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);

        tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(outputFile)));
    }

    public void add(String fileName, File file, File metaFile)
            throws IOException {
        final Map<String, Object> meta = readMeta(metaFile);
        final String guid = (String) meta.get(UnityPackageHelper.META_PROPERTY_NAME_GUID);

        if (fileName.endsWith("/")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }

        {
            final byte[] data = fileName.getBytes("UTF-8");
            final TarArchiveEntry entry = new TarArchiveEntry(String.format("./%s/pathname", guid));
            entry.setSize(data.length);
            tarArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.write(data, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        }

        if (metaFile.isFile()) {
            final TarArchiveEntry entry = new TarArchiveEntry(metaFile, String.format("./%s/asset.meta", guid));
            tarArchiveOutputStream.putArchiveEntry(entry);
            FileUtils.copyFile(metaFile, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } else {
            final byte[] data = yaml.dump(meta).getBytes("UTF-8");
            final TarArchiveEntry entry = new TarArchiveEntry(String.format("./%s/asset.meta", guid));
            entry.setSize(data.length);
            tarArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.write(data, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        }

        if (file.isFile()) {
            final TarArchiveEntry entry = new TarArchiveEntry(file, String.format("./%s/asset", guid));
            tarArchiveOutputStream.putArchiveEntry(entry);
            FileUtils.copyFile(file, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        }
    }

    private Map<String, Object> readMeta(File metaFile)
            throws IOException {
        if (metaFile.isFile()) {
            try (final InputStream inputStream = new FileInputStream(metaFile)) {
                return (Map<String, Object>) yaml.load(inputStream);
            }
        } else {
            final Map<String, Object> meta = new LinkedHashMap<>();
            meta.put(UnityPackageHelper.META_PROPERTY_NAME_FILE_FORMAT_VERSION,
                    UnityPackageHelper.META_FILE_FORMAT_VERSION);
            meta.put(UnityPackageHelper.META_PROPERTY_NAME_GUID, UnityPackageHelper.generateGuid());
            return meta;
        }
    }

    @Override
    public void close() throws IOException {
        tarArchiveOutputStream.close();
    }

    public static void pack(File inputDirectory, File outputFile)
            throws IOException {
        try (final UnityPackagePacker packer = new UnityPackagePacker(outputFile)) {
            for (final File file : FileUtils.listFilesAndDirs(inputDirectory, TrueFileFilter.TRUE,
                    TrueFileFilter.TRUE)) {
                final String relativePath = inputDirectory.toURI().relativize(file.toURI()).toString();
                if (relativePath.startsWith("Assets/") && !relativePath.endsWith(".meta")) {
                    final File metaFile = new File(inputDirectory, relativePath + ".meta");
                    packer.add(relativePath, file, metaFile);
                }
            }
        }
    }
}
