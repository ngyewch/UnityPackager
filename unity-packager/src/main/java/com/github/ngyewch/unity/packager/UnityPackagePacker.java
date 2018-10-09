package com.github.ngyewch.unity.packager;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class UnityPackagePacker implements Closeable {

    private final File projectDirectory;
    private final TarArchiveOutputStream tarArchiveOutputStream;
    private final List<String> includes;
    private final List<String> excludes;
    private final boolean includeMeta;
    private final Yaml yaml;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private UnityPackagePacker(File projectDirectory, File outputFile, List<String> includes, List<String> excludes,
                               boolean includeMeta)
            throws IOException {
        super();

        this.projectDirectory = projectDirectory;

        tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(outputFile)));

        this.includes = ((includes != null) && !includes.isEmpty()) ? includes
                : Collections.singletonList("Assets/**");
        this.excludes = excludes;
        this.includeMeta = includeMeta;

        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    private boolean isIncluded(String path) {
        if (includes != null) {
            boolean matchedInclude = false;
            for (final String include : includes) {
                if (pathMatcher.match(include, path)) {
                    matchedInclude = true;
                    break;
                }
            }
            if (!matchedInclude) {
                return false;
            }
        }
        if (excludes != null) {
            for (final String exclude : excludes) {
                if (pathMatcher.match(exclude, path)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void pack()
            throws IOException {
        for (final File file : FileUtils.listFilesAndDirs(projectDirectory, TrueFileFilter.TRUE,
                TrueFileFilter.TRUE)) {
            final String relativePath = projectDirectory.toURI().relativize(file.toURI()).toString();
            if (!relativePath.endsWith(".meta") && isIncluded(relativePath)) {
                if (includeMeta) {
                    final File metaFile = new File(projectDirectory, relativePath + ".meta");
                    add(relativePath, file, metaFile);
                } else {
                    add(relativePath, file, null);
                }
            }
        }
    }

    private void add(String fileName, File file, File metaFile)
            throws IOException {
        final Map<String, Object> meta = readMeta(metaFile);
        final String guid = (String) meta.get(UnityPackageHelper.META_PROPERTY_NAME_GUID);

        if (fileName.endsWith("/")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }

        {
            final byte[] data = fileName.getBytes(StandardCharsets.UTF_8);
            final TarArchiveEntry entry = new TarArchiveEntry(String.format("./%s/pathname", guid));
            entry.setSize(data.length);
            tarArchiveOutputStream.putArchiveEntry(entry);
            IOUtils.write(data, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        }

        if ((metaFile != null) && metaFile.isFile()) {
            final TarArchiveEntry entry = new TarArchiveEntry(metaFile, String.format("./%s/asset.meta", guid));
            tarArchiveOutputStream.putArchiveEntry(entry);
            FileUtils.copyFile(metaFile, tarArchiveOutputStream);
            tarArchiveOutputStream.closeArchiveEntry();
        } else {
            final byte[] data = yaml.dump(meta).getBytes(StandardCharsets.UTF_8);
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> readMeta(File metaFile)
            throws IOException {
        if ((metaFile != null) && metaFile.isFile()) {
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

    public static class Builder {

        private File projectDirectory;
        private File outputFile;
        private List<String> includes = null;
        private List<String> excludes = null;
        private boolean includeMeta = false;

        public Builder withProjectDirectory(File projectDirectory) {
            this.projectDirectory = projectDirectory;
            return this;
        }

        public Builder withOutputFile(File outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder withIncludes(List<String> includes) {
            this.includes = includes;
            return this;
        }

        public Builder withInclude(String include) {
            if (includes == null) {
                includes = new ArrayList<>();
            }
            includes.add(include);
            return this;
        }

        public Builder withExcludes(List<String> excludes) {
            this.excludes = excludes;
            return this;
        }

        public Builder withExclude(String exclude) {
            if (excludes == null) {
                excludes = new ArrayList<>();
            }
            excludes.add(exclude);
            return this;
        }

        public Builder withIncludeMeta(boolean includeMeta) {
            this.includeMeta = includeMeta;
            return this;
        }

        public void pack()
                throws IOException {
            if (projectDirectory == null) {
                throw new IllegalArgumentException("projectDirectory not specified");
            }
            if (outputFile == null) {
                throw new IllegalArgumentException("outputFile not specified");
            }
            try (final UnityPackagePacker packer = new UnityPackagePacker(projectDirectory, outputFile, includes,
                    excludes, includeMeta)) {
                packer.pack();
            }
        }
    }
}
