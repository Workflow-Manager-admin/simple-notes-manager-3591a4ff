# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Moshi
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# OkHttp3
-dontwarn okhttp3.**

# Dotenv-Kotlin
-dontwarn io.github.cdimascio.dotenv.**

# Data classes for Moshi
-keepclassmembers class ** {
    @com.squareup.moshi.Json *;
}
