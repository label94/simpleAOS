package co.aos.convention.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

/**
 * 다른 convention 에서도 사용하기 위해 유틸로 생성
 *
 * - compilerSdk, minSdk, jvm tool chain 정의
 * */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()

        defaultConfig.minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()

        compileOptions {
            isCoreLibraryDesugaringEnabled = true

            // toolchain 적용하면 해당 부분 없어도 되지만, 호환성을 위해 명시
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        // kotlin ToolChain 설정
        configureKotlinJvm()

        // 어떤 API가 deprecated 되었는지 확인
        tasks.withType<JavaCompile>().configureEach {
            options.compilerArgs.add("-Xlint:deprecation")
        }

        // dependencies
        dependencies {
            // Android 26 버전이 필요한 API를 21 버전에서도 사용할 수 있게 하기 위해 적용.
            "coreLibraryDesugaring"(libs.findLibrary("desugar.jdk.libs").get())
        }
    }
}

/**
 * JVM 관련 공통 함수
 * */
internal fun Project.configureKotlinJvm() {
    project.plugins.withId("org.jetbrains.kotlin.android") {
        project.extensions.configure<KotlinProjectExtension> {
            jvmToolchain(21)
        }
    }
}