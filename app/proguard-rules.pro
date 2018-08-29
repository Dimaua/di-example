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
-keep class com.valeo.loyalty.android.model.DataModel**
-keep class com.valeo.loyalty.android.ui.event.MenuRequestedEvent**
-keepclassmembernames @com.valeo.loyalty.android.model.DataModel public class * { *; }
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-dontwarn com.google.errorprone.annotations.**
-dontwarn java.lang.invoke.*
-dontwarn *$$Lambda$
-dontwarn java8.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
-dontwarn okhttp3.**

-dontnote com.facebook.stetho.**
-dontnote com.google.android.gms.**
-dontnote okhttp3.**
-dontnote java8.**