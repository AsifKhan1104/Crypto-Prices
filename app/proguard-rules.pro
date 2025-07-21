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

-keep class com.crypto.prices.model.** { *; }

-dontwarn com.crypto.prices.Hilt_CryptoApplication
-dontwarn com.crypto.prices.view.activity.Hilt_MainActivity
-dontwarn com.crypto.prices.view.ui.explore.Hilt_CurrConverterActivity
-dontwarn com.crypto.prices.view.ui.explore.Hilt_NewsFragment
-dontwarn com.crypto.prices.view.ui.home.Hilt_HomeFragment
-dontwarn com.crypto.prices.view.ui.market.Hilt_MarketFragment
-dontwarn com.crypto.prices.view.ui.market.categories.Hilt_CategoriesCoinListActivity
-dontwarn com.crypto.prices.view.ui.market.crypto.Hilt_CryptoFragment
-dontwarn com.crypto.prices.view.ui.market.crypto.detail.Hilt_CryptoDetailActivity
-dontwarn com.crypto.prices.view.ui.market.crypto.detail.Hilt_CryptoDetailSearchActivity
-dontwarn com.crypto.prices.view.ui.market.derivatives.detail.Hilt_DerivativesDetailActivity
-dontwarn com.crypto.prices.view.ui.market.exchanges.detail.Hilt_ExchangesDetailActivity
-dontwarn com.crypto.prices.view.ui.market.exchanges.detail.Hilt_ExchangesDetailSearchActivity
-dontwarn com.crypto.prices.view.ui.market.nfts.detail.Hilt_NftDetailActivity
-dontwarn com.crypto.prices.view.ui.more.Hilt_MoreFragment
-dontwarn com.crypto.prices.view.ui.search.Hilt_SearchActivity