import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.gradle.internal.tasks.FinalizeBundleTask
import java.util.Locale
import kotlin.jvm.java

plugins {
    alias(libs.plugins.multi.module.android.application.compose) // 컴포즈 관련 빌드 옵션 Plugin 추가
    alias(libs.plugins.multi.module.ui) // UI 관련 Plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 Plugin 추가
}

android {
    namespace = "co.aos.myjetpack"

    defaultConfig {
        applicationId = "co.aos.myjetpack"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // android 21 미만에서 drawable/vector 사용 시 문제가 되기 때문에, 호환성 추가
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // 패키지 충돌 방지
    // AL2.0 : Apache License 2.0 문서 파일들(자동으로 생성)
    // LGPL2.1 : GNU Lesser General Public License 2.1(자동으로 생성)
    androidResources {
        noCompress += listOf("AL2.0", "LGPL2.1")
    }

    // apk,aab 이름 명칭 설정
    applicationVariants.all {
        outputs.all {
            // APK 이름 명칭
            if (this is ApkVariantOutputImpl) {
                outputFileName = "${rootProject.name}-${name}-v${versionName}(${versionCode}).apk"
            }

            // AAB 이름 명칭
            val bundleTaskName = "sign${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}Bundle"
            tasks.named(bundleTaskName, FinalizeBundleTask::class.java) {
                val file = finalBundleFile.asFile.get()
                finalBundleFile.set(
                    File(file.parent, "${rootProject.name}-${name}-v${versionName}(${versionCode}).aab")
                )
            }
        }
    }
}

kotlin {
    // 코틀린 2.2.0 => 어노테이션 추가 시 param 과 프로퍼티 둘다 적용
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // test 관련 부분은 해당 모듈에 정의!
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //LifeCycle
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.test)

    // 빌드 런타임 오류 방지
    implementation(libs.androidx.work.runtime.ktx)

    // 확장 아이콘
    implementation(libs.androidx.compose.material.icons)

    // 다양한 디스플레이 대응을 위함
    implementation(libs.androidx.compose.material3.window)

    // 모듈 추가
    implementation(project(":core:base"))
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":core:network"))
    implementation(project(":core:local"))
    implementation(project(":feature:webview-feature"))
    implementation(project(":feature:ocr"))
    implementation(project(":common-ui:webview"))
    implementation(project(":common-ui:common"))
    implementation(project(":common-ui:permission"))
    implementation(project(":feature:webview-renewal-feature"))
    implementation(project(":feature:network-error-feature"))
    implementation(project(":feature:setting-feature"))
    implementation(project(":feature:user-feature"))
    implementation(project(":feature:home-feature"))
    implementation(project(":feature:guide-feature"))
    implementation(project(":feature:splash-feature"))
    implementation(project(":feature:barcode-feature"))
}