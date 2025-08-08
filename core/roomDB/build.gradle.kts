plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
    alias(libs.plugins.multi.module.android.room) // room 관련 plugin 추가
}

android {
    namespace = "co.aos.roomdb"
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

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}