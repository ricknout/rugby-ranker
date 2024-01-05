plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

// TODO: Consolidate Compose build features and dependencies after migration

android {
    namespace = "dev.ricknout.rugbyranker.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
}

dependencies {
    api(libs.kotlin.stdlib)
    api(libs.kotlin.coroutines.core)
    api(libs.kotlin.coroutines.android)
    api(libs.androidx.core.ktx)
    api(libs.androidx.core.splashscreen)
    api(libs.androidx.activity.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.recyclerview)
    api(libs.androidx.swiperefreshlayout)
    api(libs.androidx.emoji2)
    api(libs.androidx.browser)
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    api(libs.androidx.navigation.ui.ktx)
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.work.runtime.ktx)
    api(libs.material)
    api(libs.androidx.hilt.work)
    api(libs.androidx.datastore.preferences)
    api(libs.okhttp)
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.insetter)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
    testImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)
}
