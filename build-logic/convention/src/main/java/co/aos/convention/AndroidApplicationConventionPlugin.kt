package co.aos.convention

import co.aos.convention.convention.configureKotlinAndroid
import co.aos.convention.convention.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * 멀티 모듈 관련 Application Convention 정의
 *
 * - 공통으로 사용 되는 설정을 여기서 정의
 * - 공통 기본 Plugin 및 sdk Version, VersionCode, VersionName 설정
 * - jvm toolChain 적용
 *
 * */
class AndroidApplicationConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        // 빌드 관련 코드 추가
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            // 버전 관련 부분만 정의
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }

                configureKotlinAndroid(this)
            }
        }
    }
}