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

# --- R8 optimization: rules for reflection/ServiceLoader-based libraries ---

-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault

# kotlinx.serialization: keep generated serializers for our @Serializable models
-keepclasseswithmembers class com.keiichi.medguidelines.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class com.keiichi.medguidelines.data.**
-keepclassmembers class com.keiichi.medguidelines.data.<1>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room: keep the database and @Entity model classes (generated *_Impl code
# references them directly; consumer rules from androidx.room cover the rest).
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }

# Kotlin DataFrame is only used here for DataFrame.readCSV(...). Its readExcel/
# writeExcel paths are never called, but they pull in optional desktop-only
# dependencies (Apache POI, Arrow, Saxon, Batik, log4j, aalto-xml/StAX) that
# don't exist on Android at all. That code is unreachable at runtime, so it's
# safe to tell R8 not to fail the build over these missing classes.
-dontwarn java.awt.**
-dontwarn javax.lang.model.**
-dontwarn javax.xml.stream.**
-dontwarn net.sf.saxon.**
-dontwarn org.apache.arrow.**
-dontwarn org.apache.batik.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.osgi.**
-dontwarn aQute.bnd.**
-dontwarn edu.umd.cs.findbugs.**
-dontwarn org.codehaus.stax2.**
-dontwarn org.dhatim.fastexcel.reader.**
-dontwarn com.fasterxml.aalto.**