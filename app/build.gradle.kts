//import androidx.glance.appwidget.compose

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    //alias(libs.plugins.kotlin.compose)
    //id("org.jetbrains.kotlin.plugin.compose") version "2.2.0-RC2"// apply false
    id("kotlin-parcelize")
    id("com.google.protobuf") version "0.9.3" // Or latest version
    kotlin("plugin.serialization") version "2.2.0-RC2" //"1.9.22" // Use the latest version
    //id("com.google.devtools.ksp")
    //id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.keiichi.medguidelines"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.keiichi.medguidelines"
        minSdk = 29
        targetSdk = 35
        versionCode = 39
        versionName = "1.35"

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
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.15"
//        //kotlinCompilerExtensionVersion = libs.versions.composeCompilerVersion.get() // Use the version from TOML
//
//    }
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
    implementation(libs.accompanist.navigation.animation)
    //val work_version = "2.10.1"
    implementation(libs.androidx.work.runtime.ktx)
    //implementation(libs.kotlinx.dataframe)
    //implementation("org.jetbrains.kotlinx:dataframe:1.0.0-Beta2")
    //implementation("org.jetbrains.kotlinx:dataframe-csv:1.0.0-Beta2")
    implementation("org.jetbrains.kotlinx:dataframe:0.15.0")
    //implementation("org.jetbrains.kotlinx:dataframe-excel:0.13.1")
    implementation("com.google.dagger:hilt-android:2.56.2")
    //ksp("com.google.dagger:hilt-android-compiler:2.56.2")

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
    exclude(group = "io.swagger", module = "swagger-parser-safe-url-resolver")
    exclude(group = "commons-logging", module = "commons-logging")
    exclude(group = "org.eclipse.collections", module = "eclipse-collections")
    exclude(group = "com.github.java-json-tools", module = "btf")
    exclude(group = "com.github.java-json-tools", module = "msg-simple")
    exclude(group = "com.github.java-json-tools", module = "jackson-coreutils")
    exclude(group = "com.github.java-json-tools", module = "json-patch")
    exclude(group = "com.github.java-json-tools", module = "uri-template")
    exclude(group = "com.github.java-json-tools", module = "son-schema-core")
    exclude(group = "com.github.java-json-tools", module = "json-schema-validator")
    //exclude(group = "org.jetbrains.kotlinx", module = "dataframe-core")
    exclude(group = "org.jetbrains.kotlinx", module = "dataframe-jdbc")
    exclude(group = "org.jetbrains.kotlinx", module = "dataframe-openapi")
    exclude(group = "org.apache.arrow", module = "arrow-memory-unsafe")
    exclude(group = "org.apache.arrow", module = "arrow-memory-core")
    exclude(group = "org.apache.arrow", module = "arrow-format")
    exclude(group = "org.apache.arrow", module = "arrow-vector")
    exclude(group = "org.jetbrains.kotlinx", module = "dataframe-csv")
    exclude(group = "com.google.devtools.ksp", module = "symbol-processing-aa-embeddable")

}

