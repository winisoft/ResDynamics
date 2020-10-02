package com.winisoft.plugin.resdynamics

import com.android.build.gradle.*
import com.android.build.gradle.api.AndroidBasePlugin
import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.structureplugin.AndroidBuildConfig
import com.android.builder.model.SourceProvider
import com.android.sdklib.AndroidVersion
import com.android.sdklib.AndroidVersionHelper
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.util.Configurable
import java.io.File

class ResDynamicsPlugin : Plugin<Project> {

    private lateinit var config: ResDynamicsExt

    private var pluginConfigured = false


    override fun apply(project: Project) {

        config = project.extensions.create("resDynamics", ResDynamicsExt::class.java)
        project.plugins.whenPluginAdded {
            project.afterEvaluate {
                configure(project)
            }
        }
    }

    private fun configure(project: Project) {
        if (pluginConfigured)
            return

        project.plugins.forEach { plugin ->

            val variants: DomainObjectSet<out BaseVariant>? = when (plugin) {
                is AppPlugin -> project.extensions.getByType(AppExtension::class.java).applicationVariants
                is LibraryPlugin -> project.extensions.getByType(LibraryExtension::class.java).libraryVariants
                else -> null
            }

            if (variants?.isNotEmpty() == true) {
                pluginConfigured = true
                configureAndroid<BaseVariant>(project, variants as DomainObjectSet<out BaseVariant>?)
            }

        }
    }

    private fun <T : BaseVariant> configureAndroid(
        project: Project,
        variants: DomainObjectSet<out BaseVariant>?
    )
    {
        variants?.forEach { variant ->

            val files: ConfigurableFileCollection = variant.sourceSets.flatMap { source ->
                searchFilesInSourceSet(source)
            }.let {
                project.files(it)
            }

            variant.sourceSets[variant.sourceSets.lastIndex].let {
                if (it is AndroidSourceSet)
                    it.res.srcDir(getOutputDirForVariant(project, variant))
                else
                    throw IllegalStateException("Attempted to set res overrides for a non-Android source set!")
            }

            val outputDirectory = getOutputDirForVariant(project, variant)

            // override original sourceset with higher priority new one
            val sourceProvider = variant.sourceSets[variant.sourceSets.size - 1]
            if (sourceProvider is AndroidSourceSet) {
                sourceProvider.res.srcDir(outputDirectory)
            } else {
                throw IllegalStateException("sourceProvider is not an AndroidSourceSet")
            }

            val task = project.tasks.create(
                "resPlaceholdersFor${variant.name.capitalize()}",
                ResDynamicsTask::class.java
            ).apply {
                sources = files
                outputDir = outputDirectory
                placeholders = variant.run {
                    buildType.manifestPlaceholders + mergedFlavor.manifestPlaceholders
                }.toMutableMap().apply {
                    put("applicationId", variant.applicationId)
                }.toMap()
            }

            // register task to run before resource merging; add dummy folder as it's already in a sourceSet
            variant.registerGeneratedResFolders(project.files(File(outputDirectory, "_dummy")).builtBy(task))
        }
    }

    private fun searchFilesInSourceSet(sourceSet: SourceProvider): Iterable<File> =
        sourceSet.resDirectories.flatMap { resDir ->
            config.files.map { File(resDir, it) }
        }.filter {
            it.exists() && it.isDirectory.not()
        }

    private fun getOutputDirForVariant(project: Project, variant: BaseVariant): File =
        project.file("${project.buildDir}$GEN_DIR${variant.flavorName}/${variant.buildType.name}/")

    companion object {
        const val GEN_DIR = "/generated/res/resDynamics/"
    }
}