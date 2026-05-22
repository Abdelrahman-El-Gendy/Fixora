pluginManagement {
    repositories {
        google()
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

rootProject.name = "Fixora"

// App module
include(":app")

// Core modules
include(":core:common")
include(":core:model")
include(":core:domain")
include(":core:network")
include(":core:database")
include(":core:data")
include(":core:designsystem")

// Feature modules
include(":feature:auth")
include(":feature:home")
include(":feature:provider")
include(":feature:booking")
include(":feature:profile")
