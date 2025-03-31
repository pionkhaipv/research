pluginManagement {
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
        jcenter()
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://artifact.bytedance.com/repository/pangle/") }
        maven { setUrl("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea") }
        gradlePluginPortal()
    }
}


include(":app")
rootProject.name = "PionBase"
include(":LibIAP")
include(":LibAds")
