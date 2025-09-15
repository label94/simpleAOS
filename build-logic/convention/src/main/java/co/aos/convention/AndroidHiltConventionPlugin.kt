package co.aos.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Hilt 사용을 위한 Convention Plugin
 * */
class AndroidHiltConventionPlugin: Plugin<Project> {
    /** build logic 정의 */
    override fun apply(target: Project) {
        target.run {
            // hilt 및 ksp Plugin 적용
            pluginManager.run {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", libs.findLibrary("hilt.android").get())
                add("implementation", libs.findLibrary("androidx.hilt.lifecycle.viewmodel").get())
                add("implementation", libs.findLibrary("hilt.ext.work").get())
                add("ksp", libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}