package com.winisoft.plugin.resdynamics

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File


open class ResDynamicsTask : DefaultTask() {

    @get:InputFiles
    lateinit var sources: FileCollection

    @get:Input
    lateinit var placeholders: Map<String, Any>

    @get:OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun execute(inputs: IncrementalTaskInputs) = sources.forEach { applyPlaceholders(it) }

    private fun applyPlaceholders(file: File) {

        file.readText(charset = Charsets.UTF_8)
            .apply {
                placeholders.forEach { (key, value) ->
                    replace(oldValue = "\${$key}", newValue = value.toString())
                }
            }.run {
                File(outputDir, file.parentFile.name).apply { mkdirs() }
                    .let {
                        File(it, file.name).apply { createNewFile() }
                    }.writeText(
                        text = this,
                        charset = Charsets.UTF_8
                    )
            }
    }
}