plugins {
    id("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin"); // 添加Hilt插件

    id ("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

android {
    namespace = "com.delta.mobileplatform"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.delta.mobileplatform"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.delta.mobileplatform"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

var room_version = "2.4.0"
var hilt_version = "2.44"
var ktor_version = "2.2.2"
var ble = "1.8"
var coroutine_version = "1.7.1"
var ktx_extension = "2.6.1"
var camerax_version = "1.2.2"
var kotlinx_serialization_version = "1.5.1"
var turbine_version = "1.0.0"


dependencies {
    implementation ("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")



    implementation ("com.google.dagger:hilt-android:$hilt_version")
    kapt ("com.google.dagger:hilt-compiler:$hilt_version")
    annotationProcessor ("com.google.dagger:hilt-android-compiler:$hilt_version")

    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$ktx_extension")

    implementation ("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")


    testImplementation ("app.cash.turbine:turbine:$turbine_version")

    implementation ("com.neovisionaries:nv-bluetooth:$ble")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_version")

    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")

    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")

    implementation ("io.ktor:ktor-client-cio:$ktor_version")
    implementation ("io.ktor:ktor-client-logging:$ktor_version")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation ("io.ktor:ktor-client-content-negotiation:$ktor_version")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("net.openid:appauth:0.11.1")

    implementation ("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation ("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation ("io.jsonwebtoken:jjwt-jackson:0.11.2")


    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
//    implementation ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

//    implementation ("com.android.databinding:compiler:3.1.4")
//    kapt ("com.android.databinding:compiler:3.1.4")


//    annotationProcessor ("com.google.dagger:hilt-android-compiler:$hilt_version")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")


    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    implementation (files("libs/ZSDK_ANDROID_API.jar"))
//    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0") // 可选，用于日志记录
//    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.4.0")
//    implementation ("androidx.lifecycle:lifecycle-livedata:2.4.0")











}