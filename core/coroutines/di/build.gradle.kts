plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.google.ksp)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(projects.core.coroutines.executors)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.google.dagger.hilt.core)
    ksp(libs.google.dagger.hilt.compiler)
}