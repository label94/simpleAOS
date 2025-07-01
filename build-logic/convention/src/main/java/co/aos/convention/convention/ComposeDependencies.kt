package co.aos.convention.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * UI 관련 의존성 정의
 * */
fun DependencyHandlerScope.addUILayerDependencies(project: Project) {
    // 컴포즈 관련 의존성 추가
    add("implementation", project.libs.findBundle("compose").get())
    add("debugImplementation", project.libs.findBundle("compose.debug").get())
    add("androidTestImplementation", project.libs.findLibrary("androidx-ui-test-junit4").get())
}