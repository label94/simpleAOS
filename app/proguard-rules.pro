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

# java interface 관련
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#Begin: Gson
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class com.igaworks.gson.stream.** { *; }
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.igaworks.adbrix.model.** { *; }
#End: Gson

-keepclassmembers class * {
  public static <fields>;
  public *;
}

# R8 난독화 오류 관련
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Retrofit
-dontwarn okhttp3.**
-keep class retrofit2.** { *; }
-keepattributes RuntimeVisibleAnnotations
-keep class io.reactivex.** { *; }
-dontwarn io.reactivex.**

# Gson 또는 Moshi 사용 시 추가
-keep class com.google.gson.** { *; }

# Retrofit 인터페이스 보호
-keep interface * {
    @retrofit2.http.* <methods>;
}
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# 코루틴 관련 클래스 보호
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

-keepclassmembers class * {
 *** writeReplace();
}

# Keep Dagger Hilt classes and annotations
-keep class dagger.hilt.** { *; }
-keep class androidx.hilt.** { *; }

# coil3 코드 관련 난독화 방지
-keep class coil3.** { *; }
-dontwarn coil3.PlatformContext

# 특정 패키지 코드 난독화 방지
-keep class co.aos.network.** { *; }

