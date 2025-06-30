package co.aos.convention.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Build Types 을 공통으로 처리하기 위한 확장 함수 정의
 * */
internal  fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType // application 인지 library 인지 구분 하는 값
) {
    commonExtension.run {
        // build config 옵션 설정
        buildFeatures {
            buildConfig = true
        }

        // 타입 모듈 별 분기 처리
        when(extensionType) {
            ExtensionType.APPLICATION -> {
                // keystore 관련 정의 부분
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            // debug 활성화
                            isDebuggable = true

                            // configure debug build types
                            configureDebugBuildType()
                        }
                        release {
                            // debug 비활성화
                            isDebuggable = false

                            // configure release build types
                            configureReleaseBuildType(commonExtension)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                // keystore 관련 정의 부분
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            // debug 활성화
                            isDebuggable = true

                            // configure debug build types
                            configureDebugBuildType()
                        }
                        release {
                            // debug 비활성화
                            isDebuggable = false

                            // configure release build types
                            configureReleaseBuildType(commonExtension)
                        }
                    }
                }
            }
        }
    }
}

/** debug build type */
private fun BuildType.configureDebugBuildType() {
    // 리소스 압축하지 않음
    isMinifyEnabled = false

    // Build Config 정의(문자열 경우 이스케이프로 감싸야 한다.)
    buildConfigField("String", "BUILD_TYPE", "\"debug\"")
}

/** release build type */
private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    // 리소스 압축
    isMinifyEnabled = true

    // Build Config 정의(문자열 경우 이스케이프로 감싸야 한다.)
    buildConfigField("String", "BUILD_TYPE", "\"release\"")

    // proguard 연동
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}

