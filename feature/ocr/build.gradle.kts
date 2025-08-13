plugins {
    alias(libs.plugins.multi.module.android.library.compose) // 컴포즈 옵션 적용 된 plugin 추가
    alias(libs.plugins.multi.module.ui) // ui 옵션 적용 된 plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
}

android {
    namespace = "co.aos.ocr"
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

    // ocr
    implementation(libs.google.mlkit.text.recognition)
    implementation(libs.google.mlkit.text.recognition.korean)
    implementation(libs.google.mlkit.barcode.scanning)

    // cameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation(libs.androidx.camera.mlkit)

    // 확장 아이콘
    implementation(libs.androidx.compose.material.icons)

    // 모듈 추가
    implementation(project(":core:base"))
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":common-ui:common"))
    implementation(project(":common-ui:permission"))
}