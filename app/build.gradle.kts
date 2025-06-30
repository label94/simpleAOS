plugins {
    alias(libs.plugins.co.aos.multi.module.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "co.aos.myjetpack"

    defaultConfig {
        applicationId = "co.aos.myjetpack"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // android 21 미만에서 drawlabe/vector 사용 시 문제가 되기 때문에, 호환성 추가
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // 컴포즈 사용을 위한 옵션 설정
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //LifeCycle
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.test)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp((libs.hilt.compiler))

    // 빌드 런타임 오류 방지
    implementation(libs.androidx.work.runtime.ktx)

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":core:network"))
    implementation(project(":feature:horizontalpicker"))
    implementation(project(":feature:common"))
}