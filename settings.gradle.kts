pluginManagement {
    // build-logic 추가
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyJetpack"
include(":app")
include(":core:commonUtils:myutils")
include(":core:network")
include(":core:local")
include(":core:base")
include(":common-ui:webview")
include(":common-ui:appbar")
include(":common-ui:common")
include(":common-ui:horizontalpicker")
include(":common-ui:permission")
include(":feature:webview-feature")
include(":data")
include(":domain")
include(":feature:ocr")
include(":feature:webview-renewal-feature")
include(":feature:network-error-feature")
include(":feature:setting-feature")
include(":core:roomDB")
include(":feature:user-feature")
include(":feature:home-feature")
include(":feature:splash-feature")
include(":feature:guide-feature")
