import pres.ketikai.hyper.gradle.util.hyperBukkit

buildscript {
    dependencies {
        classpath("pres.ketikai.hyper:hyper-gradle-util:1.0.8")
    }
}

apply {
    plugin("hyper-gradle-util")
}

plugins {
    id("java")
    id("java-library")
}

group = "pres.shanque"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(fileTree("${rootDir}/libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "utf-8"
}

tasks.jar {
    dependsOn(tasks.hyperBukkit)
    destinationDirectory.set(file("C:\\Users\\58257\\Desktop\\testServer\\plugins"))
}

tasks.hyperBukkit {
    pluginYaml {
        name = project.name
        main = "pres.shanque.quecook.Quecook"
        description = "插件描述"
        version = project.version.toString()
        apiVersion = "1.12"
        authors.add("shanque")
//        depend.add("DragonCore")
        softDepend.add("MythicMobs")

        libraries {
            enabled = false
        }
    }
}
