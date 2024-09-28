# ---------- Custom settings ----------

-keep class ru.tensor.sbis.folderspanel.adapter.FolderHolder { *; }
-keep class ru.tensor.sbis.common.document.DocumentWebView$LoadFileByLinkInterface { *; }

-dontnote ru.tensor.sbis.declaration.**
-dontnote ru.tensor.sbis.design.**

-dontnote ru.tensor.sbis.platform.Initializer

-dontwarn ru.tensor.sbis.folderspanel.BaseFolderPresenter

# ---------- General settings ----------

-keep, includedescriptorclasses public class **.di.** { public protected *; }

-dontwarn java.lang.invoke.*
-dontwarn androidx.**
-dontwarn **$$Lambda$*
-dontwarn android.databinding.**
-keep class **.databinding.** { *; }
-keep class **.BuildConfig { *; }

-dontnote java.lang.**
-dontnote android.**
-dontnote com.google.android.gms.common.**

-optimizationpasses 3

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, EnclosingMethod

# Preserve all annotations.

-keepattributes *Annotation*

# Needed for Parcelable classes & their creators to not get renamed, as they are found via reflection
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
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
-keep class org.chromium.** { *; }
# ========== ========== ========== ==========

# ========== me.leolin.shortcutbadger ==========
-dontnote me.leolin.shortcutbadger.**
# ========== ========== ========== ==========

# ========== org.webrtc ==========
-dontwarn org.webrtc.**
-dontwarn android.net.Network
-keep class org.webrtc.**  { *; }
# ========== ========== ========== ==========

# ========== com.caverock.androidsvg ==========
-dontwarn com.caverock.androidsvg.**
-keep class com.caverock.androidsvg.**
-keep interface com.caverock.androidsvg.**
# ========== ========== ========== ==========

# ========== For FragmentByTagFinder ==========
-keep class android.support.v4.app.FragmentManagerImpl { *; }
