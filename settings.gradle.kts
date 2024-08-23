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
        google()
        mavenCentral()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "S3-Reader"
include(":app")
include(":core:ui")
include(":core:coroutines:executors")
include(":core:coroutines:di")
include(":core:local")
include(":feature:s3reader:api")
include(":feature:s3reader:impl")
include(":feature:s3reader:di")
include(":feature:s3reader:work")
include(":feature:s3reader:ui:api")
include(":feature:s3reader:ui:explorer")
include(":feature:usersettings:api")
include(":feature:usersettings:impl")
include(":feature:usersettings:di")
include(":feature:usersettings:ui:api")
include(":feature:s3reader:ui:impl")
include(":feature:usersettings:ui:impl")
include(":core:nav")
include(":core:navdefault")
