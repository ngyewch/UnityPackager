package com.github.ngyewch.unity.packager.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UnityPackagerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().create("unityPack", UnityPackerTask.class);
        project.getTasks().create("unityUnpack", UnityUnpackerTask.class);
    }
}
