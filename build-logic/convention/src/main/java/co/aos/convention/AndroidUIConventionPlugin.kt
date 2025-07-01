package co.aos.convention

import co.aos.convention.convention.addUILayerDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * UI 계층(화면을 담당하는 UI Layer)에 적용되는 Convention Plugin
 * */
class AndroidUIConventionPlugin: Plugin<Project> {
    /** build logic 추가 */
    override fun apply(target: Project) {
        target.run {
//            pluginManager.run {
//                //apply("multi.module.android.library.compose")
//                apply("multi.module.android.application.compose")
//            }

            // 컴포즈 UI 의존성 적용
            dependencies {
                addUILayerDependencies(target)
            }
        }
    }
}