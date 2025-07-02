plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "co.aos.network"
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // kotlinx-serialize 설정
    implementation(libs.kotlinx.serialization.json)

    // 네트워크 관련 설정
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.kotlinx.serialization.json)
    compileOnly(libs.okhttp.logging)
    compileOnly(libs.okhttp.profiler)
    debugImplementation(libs.okhttp.logging)
    debugImplementation(libs.okhttp.profiler)
    implementation(libs.com.google.gson)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.com.squareup.rxAdapter2)

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}