plugins {
    alias(libs.plugins.multi.module.android.library.compose) // 컴포즈 옵션 적용 된 plugin 추가
    alias(libs.plugins.multi.module.ui) // ui 옵션 적용 된 plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
}

android {
    namespace = "co.aos.webview_renewnal_feature"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.swiperefreshlayout)

    // 모듈 추가
    implementation(project(":core:base"))
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":domain"))
    implementation(project(":common-ui:webview"))
    implementation(project(":common-ui:common"))
    implementation(project(":common-ui:permission"))
}