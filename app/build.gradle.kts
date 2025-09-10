plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 34
    namespace = "de.xkript.blackcover"
    flavorDimensions += "version"
    
    defaultConfig {
        applicationId = "de.xkript.blackcover"
        minSdk = 21
        targetSdk = 34
        versionCode = 11
        versionName = "0.4.2"
        setProperty("archivesBaseName" , "BlackCover-v$versionCode($versionName)")
        //multiDexEnabled true
        vectorDrawables {
            useSupportLibrary = true
        }
        //        kapt {
        //            correctErrorTypes = true
        //        }
    }
    
    productFlavors {
        create("pro") {
            isDefault = true
            dimension = "version"
        }
        create("dev") {
            dimension = "version"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }
    }
    
    signingConfigs {
        create("release") {
            storeFile = file("D:/Project File/xKript/Black Cover/Android/Key/BlackCover.jks")
            storePassword = ""
            keyAlias = ""
            keyPassword = ""
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs["release"]
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    /*
        sourceSets {
            pro {
                setRoot("src/bazaar")
            }
            dev {
                setRoot("src/dev")
            }
        }
     */
    
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    
    // Basic
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    
    // Compose lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Retrofit
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // Dagger - Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)
    
    // Data Store
    implementation(libs.androidx.datastore.preferences)
    
    // Google billing
    implementation (libs.billing)

    // JUnit
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("app.cash.turbine:turbine:1.0.0")
}
