import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.manzill.example.camxvk"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.manzill.example.camxvk"
        minSdk = 28
        targetSdk = 36
        versionCode = (project.findProperty("VERSION_CODE") as? String)?.toIntOrNull()
        versionName = (project.findProperty("VERSION_NAME") as? String)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isJniDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(JavaVersion.VERSION_21.toString())
    }
}

dependencies {
    implementation(project(":core:camera"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.foundation)

    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.compose.material3.adaptive)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime.rxjava2)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}
