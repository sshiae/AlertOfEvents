plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.alertofevents"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.alertofevents"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding=true
    }
}

dependencies {
    val hiltVersion = "2.49"
    val lifecycle = "2.6.2"
    val activity = "1.8.1"
    val fragment = "1.6.2"
    val appcompat = "1.6.1"
    val material = "1.10.0"
    val constraintlayout = "2.1.4"
    val picasso = "2.71828"
    val junit = "4.13.2"
    val ktx = "1.12.0"
    val recyclerView = "1.3.2"
    val appcompatAlpha = "1.7.0-alpha03"
    val materialBeta = "1.12.0-alpha01"
    val roomVersion = "2.6.1"
    val materialDesign = "1.12.0-alpha01"
    val splashScreen = "1.0.1"
    val calendar = "2.4.0"
    val navVersion = "2.7.5"
    val spinner = "1.4.0"
    val datastore = "1.1.0-alpha07"
    val junitExt = "1.1.5"
    val espresso = "3.5.1"

    implementation("androidx.core:core-ktx:${ktx}")
    implementation("androidx.appcompat:appcompat:${appcompat}")
    implementation("androidx.appcompat:appcompat:${appcompatAlpha}")
    implementation("com.google.android.material:material:${material}")
    implementation("com.google.android.material:material:${materialBeta}")
    implementation("androidx.constraintlayout:constraintlayout:${constraintlayout}")

    // Fragment/ViewModel
    implementation("androidx.fragment:fragment-ktx:${fragment}")
    implementation("androidx.activity:activity-ktx:${activity}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycle}")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:${recyclerView}")

    // Hilt
    implementation("com.google.dagger:hilt-android:${hiltVersion}")
    kapt("com.google.dagger:hilt-compiler:${hiltVersion}")

    // Room
    implementation("androidx.room:room-runtime:${roomVersion}")
    ksp("androidx.room:room-compiler:${roomVersion}")
    implementation("androidx.room:room-ktx:${roomVersion}")

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:${datastore}")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${navVersion}")
    implementation("androidx.navigation:navigation-ui-ktx:${navVersion}")

    // Picasso
    implementation("com.squareup.picasso:picasso:${picasso}")

    // Material Design
    implementation("com.google.android.material:material:${materialDesign}")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:${splashScreen}")

    // Views
    implementation("com.kizitonwose.calendar:view:${calendar}")
    implementation("com.github.ybq:Android-SpinKit:${spinner}")
    implementation("io.github.ihermandev:format-watcher:1.0.1")

    // JUnit
    testImplementation("junit:junit:${junit}")
    androidTestImplementation("androidx.test.ext:junit:${junitExt}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${espresso}")
}