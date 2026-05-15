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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Pennywise"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":feature:dashboard")
include(":feature:transactions")
include(":feature:budgets")
include(":feature:analytics")
include(":feature:settings")
include(":feature:onboarding")
include(":feature:onboarding")
