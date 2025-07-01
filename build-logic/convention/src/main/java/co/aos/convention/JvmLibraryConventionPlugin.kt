package co.aos.convention

import co.aos.convention.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 공통 JVM Convention Plugin
 *
 * - Android 에 의존성이 없고 순수 Java, Kotlin 으로 생성된 모듈을 위한 Plugin
 * */
class JvmLibraryConventionPlugin: Plugin<Project> {
    /** build logic 정의 */
    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            // jvm 설정
            configureKotlinJvm()
        }
    }
}