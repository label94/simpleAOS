plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "co.aos.data"
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

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
    implementation(project(":domain"))
    implementation(project(":core:network"))
    implementation(project(":core:roomDB"))
    implementation(project(":core:local"))
}