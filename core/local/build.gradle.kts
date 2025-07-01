plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "co.aos.local"
}

dependencies {
    // androidx 관련
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt 설정
    implementation(libs.hilt.android)
    implementation(libs.hilt.ext.work)
    ksp((libs.hilt.compiler))

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}