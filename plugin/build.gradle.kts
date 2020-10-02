import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

buildscript {

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))
    }

}

apply(plugin="kotlin")

plugins {
    id("nu.studer.plugindev") version "1.0.3"
    id("com.jfrog.bintray") version "1.2"
    id("com.gradle.plugin-publish") version "0.10.0"
    id("java-gradle-plugin")
    id("groovy")
    id("kotlin")
    id("maven")
    id("maven-publish")
}

repositories {
    google()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    testImplementation("junit:junit:4.12")
    implementation(kotlin("stdlib", KotlinCompilerVersion.VERSION))
    implementation("com.android.tools.build:gradle:4.0.1")
}

version = "0.1"
group = "com.winisoft.resdynamics"


plugindev {
    pluginId = "com.winisoft.resdynamics.plugin"
    pluginName = "resdynamics"
    pluginImplementationClass = "com.winisoft.plugin.resdynamics.ResDynamicsPlugin"
    pluginDescription = "ResDynamics"
    pluginLicenses("MIT")
    pluginTags += mutableSetOf("gradle", "plugin", "android")
    authorId = "winisoft"
    authorName = "Steve Merollis"
    authorEmail = "stevemerollis@gmail.com"
    projectUrl = "https://github.com/winisoft"
    projectInceptionYear = "2020"
    done()
}

apply(from = "publish.gradle.kts")