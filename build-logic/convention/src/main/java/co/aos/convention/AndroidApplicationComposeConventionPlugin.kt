package co.aos.convention

import co.aos.convention.convention.configureAndroidCompose
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * Compose Application Convention
 *
 * - 컴포즈 사용을 위한 Plugin 정의
 * */
class AndroidApplicationComposeConventionPlugin: Plugin<Project> {

    /** build logic 추가 */
    override fun apply(target: Project) {
        target.run {
            // 기본 Android Application Plugin 적용
            pluginManager.run {
                apply("multi.module.android.application")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            // compose 관련 설정 추가
            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}