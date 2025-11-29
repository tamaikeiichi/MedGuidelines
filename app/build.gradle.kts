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
    //id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    //id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.keiichi.medguidelines"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.keiichi.medguidelines"
        minSdk = 29
        targetSdk = 35
        versionCode = 60
        versionName = "1.54"

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
            // This is a general good practice for many Android libraries
            excludes += "/META-INF/{AL2.0,LGPL2.1}"

            // These lines specifically target files that are often duplicated
            // by data-processing libraries like Arrow, POI, and DataFrame.
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/services/javax.xml.stream.XMLInputFactory"
            excludes += "META-INF/services/javax.xml.stream.XMLOutputFactory"
            excludes += "META-INF/services/javax.xml.stream.XMLEventFactory"
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
    //implementation(libs.androidx.room.compiler.processing.testing)
    implementation(libs.androidx.constraintlayout)

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
    implementation(libs.androidx.work.runtime.ktx)
    //implementation("org.jetbrains.kotlinx:dataframe:0.15.0")
    //implementation("com.google.dagger:hilt-android:2.56.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // Use the latest version
    implementation(libs.androidx.navigation.compose.v277) // Ensure this is up to date
// You need the main POI library and the one for modern .xlsx files
    // 1. Use the latest stable core DataFrame library for data manipulation
    implementation(libs.dataframe.v100beta3)
    // In your dependencies { ... } block
    //implementation("org.jetbrains.kotlinx:dataframe-excel:0.15.1") // Use the specific excel artifact

    // 2. Use fastexcel for memory-efficient reading. You only need the reader.
    implementation("org.dhatim:fastexcel-reader:0.19.0")

    // 3. EXPLICITLY ADD the missing dependency to resolve the build error.
    implementation("com.fasterxml:aalto-xml:1.3.4")

    //implementation("org.jetbrains.kotlinx:dataframe-excel:1.0.0-Beta3")
    //val room_version = "2.6.1"

//    implementation("androidx.room:room-runtime:$room_version")
//    ksp(libs.androidx.room.compiler) // Use ksp instead of kapt
//    implementation("androidx.room:room-ktx:$room_version") // For coroutine support
//    // ... other dependencies


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

