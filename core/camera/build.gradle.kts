@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val appVersionName = (project.findProperty("VERSION_NAME") as? String)
val (major, minor, patch) = appVersionName?.split(".")?.map { it.toIntOrNull() } ?: listOf(null, null, null)

android {
    namespace = "com.manzill.example.camxvk.core.camera"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

        externalNativeBuild {
            cmake {
                cFlags += "-std=c23 -Wall"
                arguments += mutableListOf<String>().apply {
                    add("-DAPP_NAME=\"${rootProject.name}\"")

                    major?.let { add("-DAPP_VERSION_MAJOR=$it") }
                    minor?.let { add("-DAPP_VERSION_MINOR=$it") }
                    patch?.let { add("-DAPP_VERSION_PATCH=$it") }
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = libs.versions.cmake.get()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(JavaVersion.VERSION_21.toString())
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.camera.lifecycle)
}
