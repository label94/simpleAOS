package co.aos.convention

import co.aos.convention.convention.ExtensionType
import co.aos.convention.convention.configureBuildTypes
import co.aos.convention.convention.configureKotlinAndroid
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Library 내에서 사용할 Convention Plugin
 * */
class AndroidLibraryConventionPlugin: Plugin<Project> {

    /** build logic 정의 */
    override fun apply(target: Project) {
        target.run {
            // Library 전용 plugin 적용
            pluginManager.run {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                configureBuildTypes(
                    commonExtension = this,
                    extensionType = ExtensionType.LIBRARY
                )

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
        }
    }
}