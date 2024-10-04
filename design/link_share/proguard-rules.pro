
# ---------- Custom settings ----------



# ---------- General settings ----------

-keep, includedescriptorclasses public class **.di.** { public protected *; }

-dontwarn java.lang.invoke.*
-dontwarn androidx.**
-dontwarn **$$Lambda$*
-dontwarn android.databinding.**
-keep class **.databinding.** { *; }
-keep class **.BuildConfig { *; }

-optimizationpasses 3

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, EnclosingMethod

# Preserve all annotations.

-keepattributes *Annotation*

# Needed for Parcelable classes & their creators to not get renamed, as they are found via reflection
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve all .class method names.

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ========== ru.tensor.sbis.controller ==========
-keep, includedescriptorclasses class ru.tensor.sbis.**.generated.** { *; }
# ========== ========== ========== ==========

# ========== AdvancedWebView ==========
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**
# ========== ========== ========== ==========

# ========== retrofit2 ==========
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
# ========== ========== ========== ==========

# ========== com.facebook.Fresco ==========
-keep class com.facebook.imagepipeline.gif.** { *; }
-keep class com.facebook.imagepipeline.webp.** { *; }

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**

-dontnote android.net.http.**
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-dontnote com.facebook.drawee.**
# ========== ========== ========== ==========

# ========== de.greenrobot.dao ==========
-keep class **$Properties
# ========== ========== ========== ==========

# ========== org.jsoup ==========
-keeppackagenames org.jsoup.nodes
# ========== ========== ========== ==========

# ========== org.ccil.cowan.tagsoup ==========
-keeppackagenames org.ccil.cowan.tagsoup
# ========== ========== ========== ==========

# ========== net.sqlcipher ==========
-keep class net.sqlcipher.** { *; }
# ========== ========== ========== ==========

# ========== com.mikepenz.iconics ==========
-dontnote com.mikepenz.iconics.**
# ========== ========== ========== ==========

# ========== android.view.MiuiWindowManager ==========
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class android.view.MiuiWindowManager$LayoutParams
-dontnote android.view.MiuiWindowManager$LayoutParams
# ========== ========== ========== ==========

# ========== org.webrtc ==========
-dontwarn org.webrtc.**
-dontwarn android.net.Network
-keep class org.webrtc.**  { *; }
# ========== ========== ========== ==========