plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
}

android {
    namespace = "co.aos.webview"
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.swiperefreshlayout)

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}