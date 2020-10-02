import org.jetbrains.kotlin.config.KotlinCompilerVersion

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val kotlinVersion: String by project

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "$kotlinVersion"))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    dependencies {
        apply(plugin="kotlin")
    }
}