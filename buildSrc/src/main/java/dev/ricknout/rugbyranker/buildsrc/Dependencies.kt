package dev.ricknout.rugbyranker.buildsrc

object Versions {
    const val compileSdk = 34
    const val minSdk = 24
    const val targetSdk = 34
    const val androidGradlePlugin = "8.2.0-alpha09"
    const val ktlint = "0.48.2"
    const val coil = "2.4.0"
    const val insetter = "0.6.1"
    object Kotlin {
        const val kotlin = "1.8.22"
        const val coroutines = "1.7.1"
    }
    object AndroidX {
        const val activity = "1.8.0-alpha06"
        const val fragment = "1.7.0-alpha01"
        const val appCompat = "1.7.0-alpha02"
        const val constraintLayout = "2.2.0-alpha10"
        const val recyclerView = "1.3.1-rc01"
        const val drawerLayout = "1.2.0"
        const val swipeRefreshLayout = "1.2.0-alpha01"
        const val viewPager2 = "1.1.0-beta02"
        const val emoji2 = "1.4.0-beta05"
        const val browser = "1.6.0-beta01"
        const val lifecycle = "2.6.1"
        const val room = "2.6.0-alpha02"
        const val paging = "3.2.0-rc01"
        const val navigation = "2.7.0-beta01"
        const val work = "2.9.0-alpha01"
        const val hilt = "1.0.0"
        const val dataStore = "1.1.0-alpha04"
        object Core {
            const val core = "1.12.0-alpha05"
            const val splashscreen = "1.0.1"
        }
        object Test {
            const val core = "1.6.0-alpha01"
            const val jUnit = "1.2.0-alpha01"
            const val runner = "1.6.0-alpha02"
        }
    }
    object Google {
        const val material = "1.10.0-alpha04"
        const val hilt = "2.46.1"
        object OssLicenses {
            const val ossLicenses = "17.0.1"
            const val gradlePlugin = "0.10.6"
        }
    }
    object Square {
        const val okHttp = "5.0.0-alpha.11"
        const val retrofit = "2.9.0"
    }
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
    const val ktlint = "com.pinterest:ktlint:${Versions.ktlint}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val insetter = "dev.chrisbanes.insetter:insetter:${Versions.insetter}"
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin.kotlin}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.kotlin}"
        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}"
        }
    }
    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
        const val drawerLayout = "androidx.drawerlayout:drawerlayout:${Versions.AndroidX.drawerLayout}"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"
        const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.AndroidX.viewPager2}"
        const val emoji2 = "androidx.emoji2:emoji2:${Versions.AndroidX.emoji2}"
        const val browser = "androidx.browser:browser:${Versions.AndroidX.browser}"
        object Core {
            const val ktx = "androidx.core:core-ktx:${Versions.AndroidX.Core.core}"
            const val splashscreen = "androidx.core:core-splashscreen:${Versions.AndroidX.Core.splashscreen}"
        }
        object Activity {
            const val ktx = "androidx.activity:activity-ktx:${Versions.AndroidX.activity}"
        }
        object Fragment {
            const val ktx = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"
        }
        object Lifecycle {
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.lifecycle}"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.lifecycle}"
        }
        object Room {
            const val runtime = "androidx.room:room-runtime:${Versions.AndroidX.room}"
            const val compiler = "androidx.room:room-compiler:${Versions.AndroidX.room}"
            const val ktx = "androidx.room:room-ktx:${Versions.AndroidX.room}"
        }
        object Paging {
            const val runtime = "androidx.paging:paging-runtime:${Versions.AndroidX.paging}"
        }
        object Navigation {
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.navigation}"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigation}"
            const val safeArgsGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.AndroidX.navigation}"
        }
        object Work {
            const val runtimeKtx = "androidx.work:work-runtime-ktx:${Versions.AndroidX.work}"
        }
        object Hilt {
            const val compiler = "androidx.hilt:hilt-compiler:${Versions.AndroidX.hilt}"
            const val work = "androidx.hilt:hilt-work:${Versions.AndroidX.hilt}"
        }
        object DataStore {
            const val preferences = "androidx.datastore:datastore-preferences:${Versions.AndroidX.dataStore}"
        }
        object Test {
            const val runner = "androidx.test:runner:${Versions.AndroidX.Test.runner}"
            const val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            object Core {
                const val ktx = "androidx.test:core-ktx:${Versions.AndroidX.Test.core}"
            }
            object JUnit {
                const val ktx = "androidx.test.ext:junit-ktx:${Versions.AndroidX.Test.jUnit}"
            }
        }
    }
    object Google {
        const val material = "com.google.android.material:material:${Versions.Google.material}"
        object Hilt {
            const val compiler = "com.google.dagger:hilt-compiler:${Versions.Google.hilt}"
            const val android = "com.google.dagger:hilt-android:${Versions.Google.hilt}"
            const val androidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.Google.hilt}"
        }
        object OssLicenses {
            const val ossLicenses = "com.google.android.gms:play-services-oss-licenses:${Versions.Google.OssLicenses.ossLicenses}"
            const val gradlePlugin = "com.google.android.gms:oss-licenses-plugin:${Versions.Google.OssLicenses.gradlePlugin}"
        }
    }
    object Square {
        const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.Square.okHttp}"
        object Retrofit {
            const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.Square.retrofit}"
            const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.Square.retrofit}"
        }
    }
}
