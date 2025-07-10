plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "co.aos.domain"
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

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}