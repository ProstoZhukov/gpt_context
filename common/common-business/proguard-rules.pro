# ---------- Custom settings ----------

-dontnote ru.tensor.sbis.common.**
-dontnote ru.tensor.sbis.core.**
-dontnote ru.tensor.sbis.declaration.**
-dontnote ru.tensor.sbis.design.**
-dontnote ru.tensor.sbis.design2.**

# ---------- General settings ----------

-keep, includedescriptorclasses public class **.di.** { public protected *; }

-keep public class * extends java.lang.Exception

-dontwarn java.lang.invoke.*
-dontwarn android.support.**
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

# ========== AdvancedWebView ==========
-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**
# ========== ========== ========== ==========

# ========== com.google.android.gms ==========
-keep class * extends java.util.ListResourceBundle {
protected java.lang.Object[][] getContents() ;
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;
}

-keep class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontnote com.google.android.gms.**

# ========== ========== ========== ==========

# ========== ru.tensor.sbis.controller ==========
-keep, includedescriptorclasses class ru.tensor.sbis.**.generated.** { *; }
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

# ========== Jackson 2.x ==========
# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
# ========== ========== ========== ==========

# ========== com.facebook.Fresco ==========
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontnote com.facebook.imagepipeline.nativecode.WebpTranscoderImpl
-dontnote com.facebook.webpsupport.WebpBitmapFactoryImpl
-dontnote com.facebook.imagepipeline.animated.factory.AnimatedFactoryImplSupport
-dontnote com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
# ========== ========== ========== ==========

# ========== de.greenrobot.dao ==========
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
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

# ========== org.chromium ==========
-keep, includedescriptorclasses class org.chromium.** { *; }
# ========== ========== ========== ==========

# ========== com.soundcloud.android.crop ==========
-keep class com.soundcloud.android.crop.** { *; }
# ========== ========== ========== ==========

# ========== org.jsoup.parser ==========
-keep, includedescriptorclasses class org.jsoup.parser.** { *; }
# ========== ========== ========== ==========

# ========== com.koushikdutta.async ==========
-keep, includedescriptorclasses class com.koushikdutta.async.** { *; }
# ========== ========== ========== ==========

# ========== com.crashlytics ==========
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# ========== ========== ========== ==========

# ========== com.couchbase ==========
-keep class com.couchbase.litecore.**{ *; }
-keep class com.couchbase.lite.**{ *; }
# ========== ========== ========== ==========

# ========== org.bouncycastle ==========
-dontnote org.bouncycastle.jce.provider.BouncyCastleProvider
# ========== ========== ========== ==========

# ========== org.robolectric ==========
-keep class org.robolectric.** { *; }
-dontwarn org.robolectric.**
-dontnote org.robolectric.**
# ========== ========== ========== ==========

# ========== com.google.android.gms.tagmanager ==========
-keep, includedescriptorclasses class com.google.android.gms.tagmanager.** { *; }
-dontnote  com.google.android.gms.tagmanager.TagManagerService
# ========== ========== ========== ==========

# ========== com.firebase ==========
-keep class com.firebase.** { *; }
-keep class com.google.firebase.** { *; }
# ========== ========== ========== ==========

# ========== com.google.gson ==========
-dontnote sun.misc.**
# ========== ========== ========== ==========

# ========== com.google.protobuf ==========
-dontnote com.google.protobuf.**
-dontnote libcore.io.Memory
# ========== ========== ========== ==========

# ========== com.google.dagger ==========
-dontwarn com.google.errorprone.annotations.**
# ========== ========== ========== ==========

# ========== org.webrtc ==========
-dontwarn org.webrtc.**
-dontwarn android.net.Network
-keep class org.webrtc.**  { *; }
# ========== ========== ========== ==========
