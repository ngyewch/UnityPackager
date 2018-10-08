var subDirs = rootDir.listFiles { file ->
    file.isDirectory && (file.name != "buildSrc")
            && (File(file, "build.gradle").isFile || File(file, "build.gradle.kts").isFile)
}

subDirs.forEach {
    include(it.name)
}
