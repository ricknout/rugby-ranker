plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.oss.licenses)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "dev.ricknout.rugbyranker"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ricknout.rugbyranker"
        minSdk = 24
        targetSdk = 34
        versionCode = 2112
        versionName = "2.1.12"
        setProperty("archivesBaseName", "$applicationId-v$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders += mapOf(
                    "appName" to "@string/app_name",
                    "appIcon" to "@mipmap/ic_launcher"
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            manifestPlaceholders += mapOf(
                    "appName" to "@string/app_name_debug",
                    "appIcon" to "@mipmap/ic_launcher_debug"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

room {
    schemaDirectory("$projectDir/schemas/")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ranking"))
    implementation(project(":prediction"))
    implementation(project(":match"))
    implementation(project(":live"))
    implementation(project(":news"))
    implementation(project(":info"))
    implementation(project(":theme"))
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.dagger.hilt.android)
    ksp(libs.androidx.room.compiler)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    testImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)
}
