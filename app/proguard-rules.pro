# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Marco\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#retrolambda
-dontwarn java.lang.invoke.*

-dontwarn com.squareup.okhttp.**
-keep class android.support.v8.renderscript.** { *; }

-keep class com.bortolan.iquadriv2.Interfaces.** {*;}

#retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

#Fabric
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


-dontwarn okio.**

#-keep class com.roughike.bottombar.**
-dontwarn com.roughike.bottombar.**

-keep public class org.jsoup.** {
public *;
}

-dontwarn com.google.firebase.messaging.**

-dontwarn okhttp3.**

-dontwarn com.afollestad.materialdialogs.DefaultAdapter
-dontwarn com.afollestad.materialdialogs.AlertDialogWrapper$Builder

-keepclassmembers class com.adcolony.sdk.ADCNative** {
    *;
 }

 -keep public class com.google.android.gms.ads.** {
 public *;
 }

 -keep public class com.google.ads.** {
 public *;
 }