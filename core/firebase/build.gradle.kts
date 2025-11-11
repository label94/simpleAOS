plugins {
    alias(libs.plugins.multi.module.android.library)  // library plugin 추가
    alias(libs.plugins.multi.module.hilt) // hilt 관련 plugin 추가
}

android {
    namespace = "co.aos.firebase"
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

    // firebase
    implementation(platform(libs.firebase.bom)) {
        // Guava 충돌 방지(CameraX와 firebase 동시 사용 시 guava 라이브러리 충돌 방지 위함)
        exclude(group = "com.google.guava", module = "listenablefuture")
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.ai.ktx) // firebase ai logic

    // 모듈 추가
    implementation(project(":core:commonUtils:myutils"))
}