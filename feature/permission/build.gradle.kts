plugins {
    alias(libs.plugins.multi.module.android.library.compose) // 컴포즈 옵션 적용 된 plugin 추가
}

android {
    namespace = "co.aos.permission"
}

dependencies {
    // androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // compose 관련
    implementation(libs.androidx.activity.compose)

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}