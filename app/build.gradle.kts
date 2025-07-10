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
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

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

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":core:network"))
    implementation(project(":feature:webview-feature"))
}