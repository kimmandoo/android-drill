pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven(url = "https://github.com/rosjava/rosjava_mvn_repo/raw/master")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://github.com/rosjava/rosjava_mvn_repo/raw/master")
        google()
        mavenCentral()
    }
}

rootProject.name = "ROS"
include(":app")
