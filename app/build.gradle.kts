plugins {
    alias(libs.plugins.android.application)
}


android {
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    namespace = "com.example.createreviewback"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.createreviewback"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.places)
    implementation(libs.material.calendar.view)
    implementation(libs.compact.calendar.view)
    implementation(libs.custom.calendar.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}