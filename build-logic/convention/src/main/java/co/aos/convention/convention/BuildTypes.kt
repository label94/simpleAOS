package co.aos.convention.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

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
                    // signingConfig 정의
                    createSigningConfigs(this)

                    buildTypes {
                        debug {
                            // debug 활성화
                            isDebuggable = true

                            // configure debug build types
                            configureDebugBuildType()

                            // sign key 지정
                            signingConfig = signingConfigs.getByName("debug") // 디버그 키
                        }
                        release {
                            // debug 비활성화
                            isDebuggable = false

                            // configure release build types
                            configureReleaseBuildType(commonExtension)

                            // sigh key 지정
                            signingConfig = signingConfigs.getByName("release") // 릴리즈 키(업로드 키)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                // keystore 관련 정의 부분
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            // configure debug build types
                            configureDebugBuildType()
                        }
                        release {
                            // configure release build types
                            configureLibraryReleaseBuildType()
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

/** release build type for Library */
private fun BuildType.configureLibraryReleaseBuildType() {
    // 라이브러리 모듈은 리소스 압축 및 난독화를 적용하지 않는 것이 일반적입니다.
    // 최종 앱 모듈에서 한번에 처리합니다.
    isMinifyEnabled = false

    // Build Config 정의
    buildConfigField("String", "BUILD_TYPE", "\"release\"")
}


/** APK 생성 및 빌드를 위한 Sign Config 추가 */
private fun Project.createSigningConfigs(extension: ApplicationExtension) {
    val debugKeystorePropertiesFile = rootProject.file("./keystore/debug_key.properties")
    val debugKeystoreProperties = Properties().apply {
        load(debugKeystorePropertiesFile.inputStream())
    }

    // 앱 Sign key 정의
    extension.signingConfigs {
        getByName("debug") {
            storeFile = debugKeystoreProperties["storeFile"]?.let { file(it) }
            keyAlias = debugKeystoreProperties["keyAlias"].toString()
            keyPassword = debugKeystoreProperties["keyPassword"].toString()
            storePassword = debugKeystoreProperties["storePassword"].toString()
        }
        create("release") {
            storeFile = debugKeystoreProperties["storeFile"]?.let { file(it) }
            keyAlias = debugKeystoreProperties["keyAlias"].toString()
            keyPassword = debugKeystoreProperties["keyPassword"].toString()
            storePassword = debugKeystoreProperties["storePassword"].toString()
        }
    }
}

