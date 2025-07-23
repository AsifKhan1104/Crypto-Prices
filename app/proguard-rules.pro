# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep class com.startapp.** {
#     *;
#}

-keep class com.truenet.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
#-dontwarn com.startapp.**

-dontwarn org.jetbrains.annotations.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keep class com.crypto.prices.CryptoApplication { *; }

# Hilt/Dagger related rules
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-keep class javax.inject.** { *; }

# Keep generated Hilt classes
-keep class * implements dagger.hilt.android.components.* { *; }
-keep class * extends dagger.hilt.android.internal.managers.* { *; }
-keep class * extends dagger.hilt.internal.* { *; }

# Keep Hilt annotations
-keepattributes *Annotation*

# If you use EntryPoint and other generated classes
-keep class **_HiltComponents { *; }
-keep class **_HiltModules { *; }

# Keep injectable fields and constructors
-keepclassmembers,allowobfuscation class * {
    @javax.inject.Inject <init>(...);
}

-keepclassmembers class * {
    @javax.inject.Inject <fields>;
}

# Retrofit interface methods - keep all interface methods annotated with @retrofit2.http.*
-keep interface * {
    @retrofit2.http.* <methods>;
}

# Keep Retrofit annotations
-keepattributes RuntimeVisibleAnnotations

# For OkHttp (if you use it)
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Gson uses reflection, so keep model classes' fields
-keep class com.crypto.prices.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*