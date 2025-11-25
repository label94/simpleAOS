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
-keepnames class * extends android.app.Application
-keepnames class * extends androidx.lifecycle.ViewModel

# coil3 코드 관련 난독화 방지
-keep class coil3.** { *; }
-dontwarn coil3.PlatformContext

# 특정 패키지 코드 난독화 방지
-keep class co.aos.network.** { *; }
-keep class co.aos.myutils.** { *; }
-keep class co.aos.base.** { *; }

# Google ML Kit Barcode Scanning
-keep public class com.google.mlkit.vision.barcode.** { *; }
-keep public class com.google.mlkit.vision.barcode.common.** { *; }
-keep public class com.google.mlkit.vision.common.** { *; }

# Firestore가 내부적으로 사용하는 grpc-okhttp 관련 규칙
# 오래된 com.squareup.okhttp 클래스를 유지 (CipherSuite 오류 해결)
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# grpc-okhttp의 내부 클래스도 유지
-keep class io.grpc.okhttp.internal.** { *; }

# Guava 라이브러리 관련 규칙 추가
# Guava 라이브러리가 사용하는 일부 내부 클래스들을 유지합니다.
-keep class com.google.common.base.** { *; }
-dontwarn com.google.common.base.**
-dontwarn java.lang.reflect.AnnotatedType
-dontwarn com.google.firebase.ktx.Firebase

#==============================================================
# ▼▼▼ Firebase & Google Play Services (최종 수정본) ▼▼▼
#==============================================================

# 1. Firebase와 Google Play Services의 핵심 공개 클래스 및 멤버를 유지합니다.
#    이 규칙 하나로 대부분의 인증, Firestore, gRPC 관련 클래스 유지 문제가 해결됩니다.
-keep public class com.google.firebase.** {
    public *;
}
-keep public class com.google.android.gms.** {
    public *;
}

# 2. Firestore 데이터 모델(POJO) 클래스의 내용이 제거되거나 이름이 바뀌는 것을 방지합니다.
#    'your.package.models.**'는 반드시 실제 프로젝트의 데이터 모델 패키지 경로로 수정해주세요.
#    예시: -keep class co.aos.data.model.** { *; }
-keep class co.aos.firebase.** { *; }
-keep class com.google.firebase.ktx.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.firestore.** { *; }

# 3. Firestore가 내부적으로 참조하는 오래된 OkHttp 클래스를 유지합니다. (CipherSuite 오류 방지)
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
#==============================================================
# ▲▲▲ Firebase & Google Play Services (최종 수정본) ▲▲▲
#==============================================================








