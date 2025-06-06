plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.protobuf") version "0.9.3" // Or latest version

    kotlin("plugin.serialization") version "1.9.22" // Use the latest version
}

android {
    namespace = "com.keiichi.medguidelines"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.keiichi.medguidelines"
        minSdk = 29
        targetSdk = 35
        versionCode = 31
        versionName = "1.28"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.graphics.shapes.android)
    implementation(libs.androidx.room.compiler.processing.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose.v284)
    implementation(libs.androidx.datastore.preferences.v100)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.protobuf.javalite)
    implementation(libs.androidx.preference)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material.icons.extended)
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")
    val work_version = "2.10.1"
    implementation("androidx.work:work-runtime-ktx:$work_version")

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.4"
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

configurations.implementation {
    exclude(group = "com.intellij", module = "annotations")
    exclude(group = "com.google.auto.value", module = "auto-value")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
}

