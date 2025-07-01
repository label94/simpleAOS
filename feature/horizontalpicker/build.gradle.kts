plugins {
    alias(libs.plugins.multi.module.android.library.compose) // 컴포즈 옵션 적용 된 plugin 추가
    alias(libs.plugins.multi.module.ui) // ui 옵션 적용 된 plugin 추가
}

android {
    namespace = "co.aos.horizontalpicker"
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //LifeCycle
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.test)

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":feature:common"))
}