package co.aos.convention.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Compose 관련 빌드 로직 정의
 *
 * - Compose 관련 설정 및 의존성 추가
 * */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.run {
        // 컴포즈 옵션 추가
        buildFeatures {
            compose = true
        }

        // 컴포즈 관련 의존성 추가
        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add("debugImplementation", libs.findLibrary("androidx.ui.tooling.preview").get())
            add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
        }
    }
}