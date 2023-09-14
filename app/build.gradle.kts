plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-android-extensions")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.asf.cryptoprices"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.asf.cryptoprices"
        minSdk = 19
        targetSdk = 33
        versionCode = 3
        versionName = "1.2.0"
        multiDexEnabled = true
        resConfigs(
            "ar", "de", "en", "es", "fr", "gu", "hi", "hu", "id", "it", "ja", "kn", "ko", "nl",
            "pl", "ro", "sv", "ta", "te", "tr", "zh"
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation(platform("com.google.firebase:firebase-bom:28.1.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    // Declare the dependencies for the Dynamic Links and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-dynamic-links-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    // Declare the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx")
    // Declare the dependencies for the Remote Config and Analytics libraries
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Also declare the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.android.support:multidex:1.0.3")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation("com.google.android.material:material:1.6.0")

    implementation("com.github.bumptech.glide:glide:4.11.0")
    // Retrofit dependency
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.5.0")
    // Lifecycle dependency
    implementation("android.arch.lifecycle:extensions:1.1.1")
    // Coroutines dependency
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0-beta02")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0-beta02")
    // TO satisfy fragment transitive dependency
    implementation ("androidx.fragment:fragment-ktx:1.5.2")
    implementation("androidx.room:room-runtime:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")
    // google play billing
    // implementation ("com.android.billingclient:billing-ktx:4.0.0")
    // lucky wheel
    implementation("com.github.mmoamenn:LuckyWheel_Android:0.3.0")
    // dependency for using facebook shimmer layout
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    // graph / chart library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // chrome custom tab
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    // hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}