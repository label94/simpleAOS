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
include(":feature:horizontalpicker")
include(":feature:common")
include(":feature:appbar")
include(":core:webview")
include(":feature:permission")
include(":core:base")
