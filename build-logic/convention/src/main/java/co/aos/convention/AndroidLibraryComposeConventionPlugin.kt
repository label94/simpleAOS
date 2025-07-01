package co.aos.convention

import co.aos.convention.convention.configureAndroidCompose
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * 화면이 없는 UI 모듈에서 사용할 Library 용 Convention Plugin
 * */
class AndroidLibraryComposeConventionPlugin: Plugin<Project> {
    /** build logic 정의 */
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                // 기존에 만들어 놓은 library 용 plugin 추가
                apply("multi.module.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            // 컴포즈 관련 빌드 옵션 적용
            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}