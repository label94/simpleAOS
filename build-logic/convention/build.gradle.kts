plugins {
    `kotlin-dsl`
}

// namespace 지정
group = "co.aos.convention.buildlogic"

// 사용하려는 JDK 버전을 지정 => Gradle이 사용하는 JDK를 명시적으로 설정하고, 프로젝트에 필요한 JDK를 다운받아 사용가능!!
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    jvmToolchain(17)
}

dependencies {
    // build-logic 모듈에서 생성하는 모든 플러그인 컴파일 중에만 관련이 있고 런타임 중에는 아무것도 하지 않기 때문에
    // compileOnly를 사용!
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

// custom convention 등록
// 입력하는 id는 libs.version.toml 파일에 작성한 id와 반드시 동일해야 함!
gradlePlugin {
    plugins {
        // 기본 android application 관련 plugin 등록
        register("androidApplication") {
            id = "multi.module.android.application"
            implementationClass = "co.aos.convention.AndroidApplicationConventionPlugin"
        }

        // 기본 + compose 관련 plugin 등록
        register("androidApplicationCompose") {
            id = "multi.module.android.application.compose"
            implementationClass = "co.aos.convention.AndroidApplicationComposeConventionPlugin"
        }

        // 기본 Library 관련 plugin 등록
        register("androidLibrary") {
            id = "multi.module.android.library"
            implementationClass = "co.aos.convention.AndroidLibraryConventionPlugin"
        }

        // 기본 + compose 관련 plugin 등록
        register("androidLibraryCompose") {
            id = "multi.module.android.library.compose"
            implementationClass = "co.aos.convention.AndroidLibraryComposeConventionPlugin"
        }

        // UI 관련 plugin 등록
        register("androidUI") {
            id = "multi.module.ui"
            implementationClass = "co.aos.convention.AndroidUIConventionPlugin"
        }
    }
}




