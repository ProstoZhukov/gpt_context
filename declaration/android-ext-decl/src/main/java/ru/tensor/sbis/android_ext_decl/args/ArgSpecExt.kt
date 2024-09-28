package ru.tensor.sbis.android_ext_decl.args

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.android_ext_decl.getParcelableArrayListUniversally
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.android_ext_decl.validate
import java.io.Serializable
import kotlin.jvm.internal.Lambda
import kotlin.reflect.KProperty

/* Расширения для работы с ArgSpec. */

/** Положить [value] в [Bundle]. */
fun <T> Bundle.putKeySpec(spec: KeySpec<T>, value: T) = spec.put(this, value)

/** Прочитать данные из [Bundle]. */
fun <T> Bundle.getKeySpec(spec: KeySpec<T>): T = spec.get(this)

/** Положить [value] в [Intent]. */
fun <T> Intent.putKeySpec(spec: KeySpec<T>, value: T) = spec.put(this, value)

/** Прочитать данные из [Intent]. */
fun <T> Intent.getKeySpec(spec: KeySpec<T>): T = spec.get(this)

/** @SelfDocumented */
operator fun <T> KeySpec<T>.getValue(thisRef: Bundle, property: KProperty<*>): T = get(thisRef)

/** @SelfDocumented */
operator fun <T> KeySpec<T>.setValue(thisRef: Bundle, property: KProperty<*>, value: T) = put(thisRef, value)

/** @SelfDocumented */
operator fun <T> KeySpec<T>.getValue(thisRef: Intent, property: KProperty<*>): T = get(thisRef)

/** @SelfDocumented */
operator fun <T> KeySpec<T>.setValue(thisRef: Intent, property: KProperty<*>, value: T) = put(thisRef, value)

/** Декорирует [KeySpec] чтобы аргумент был non-null. */
fun <T> KeySpec<T>.nonNull(): KeySpec<T & Any> {
    return object : KeySpec<T & Any> {
        override val key: String? = this@nonNull.key

        override fun get(bundle: Bundle): T & Any {
            return requireNotNull(this@nonNull.get(bundle))
        }

        override fun put(bundle: Bundle, value: T & Any) {
            this@nonNull.put(bundle, value)
        }

        override fun put(intent: Intent, value: T & Any) {
            this@nonNull.put(intent, value)
        }

        override fun get(intent: Intent): T & Any {
            return requireNotNull(this@nonNull.get(intent))
        }
    }
}

/** Создать [Byte] аргумент. */
fun KeySpec.Companion.byte(argKey: String): KeySpec<Byte?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::getByte,
    bundleGetAction = Bundle::getByte,
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getByteExtra(key, 0) }
)

/** Создать [Short] аргумент. */
fun KeySpec.Companion.short(argKey: String): KeySpec<Short?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putShort,
    bundleGetAction = Bundle::getShort,
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getShortExtra(key, 0) }
)

/** Создать [Int] аргумент. */
fun KeySpec.Companion.int(argKey: String): KeySpec<Int?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putInt,
    bundleGetAction = Bundle::getInt,
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getIntExtra(key, 0) }
)

/** Создать [Long] аргумент. */
fun KeySpec.Companion.long(argKey: String): KeySpec<Long?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putLong,
    bundleGetAction = Bundle::getLong,
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getLongExtra(key, 0) }
)

/** Создать [Float] аргумент. */
fun KeySpec.Companion.float(argKey: String): KeySpec<Float?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putFloat,
    bundleGetAction = Bundle::getFloat,
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getFloatExtra(key, 0f) }
)

/** Создать [Double] аргумент. */
fun KeySpec.Companion.double(argKey: String): KeySpec<Double?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putDouble,
    bundleGetAction = Bundle::getDouble,
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getDoubleExtra(key, 0.0) }
)

/** Создать [String] аргумент. */
fun KeySpec.Companion.string(argKey: String): KeySpec<String?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putString,
    bundleGetAction = Bundle::getString,
    intentPutAction = Intent::putExtra,
    intentGetAction = Intent::getStringExtra
)

/** Создать [Boolean] аргумент. */
fun KeySpec.Companion.boolean(argKey: String): KeySpec<Boolean?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = { key, value -> putBoolean(key, value) },
    bundleGetAction = { key -> getBoolean(key, false) },
    intentPutAction = Intent::putExtra,
    intentGetAction = { key -> getBooleanExtra(key, false) }
)

/**
 * Создать [Serializable] аргумент.
 * Сериализуемый объект дополнительно валидируется на дебаг сборке при помощи [validate].
 */
inline fun <reified T : Serializable> KeySpec.Companion.serializable(argKey: String): KeySpec<T?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = { key, value ->
        if (BuildConfig.DEBUG) {
            value?.validate()
        }
        putSerializable(key, value)
    },
    bundleGetAction = Bundle::getSerializableUniversally,
    intentPutAction = { key, value ->
        if (BuildConfig.DEBUG) {
            value?.validate()
        }
        putExtra(key, value)
    },
    intentGetAction = Intent::getSerializableUniversally
)

/** Создать [Parcelable] аргумент. */
inline fun <reified T : Parcelable> KeySpec.Companion.parcelable(argKey: String): KeySpec<T?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = Bundle::putParcelable,
    bundleGetAction = Bundle::getParcelableUniversally,
    intentPutAction = Intent::putExtra,
    intentGetAction = Intent::getParcelableUniversally
)

/** Создать аргумент, который будет списком из [Parcelable] объектов. */
inline fun <reified T : Parcelable> KeySpec.Companion.parcelableList(argKey: String): KeySpec<List<T>?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = { key, value -> putParcelableArrayList(key, value?.let(::ArrayList)) },
    bundleGetAction = Bundle::getParcelableArrayListUniversally,
    intentPutAction = { key, value -> putParcelableArrayListExtra(key, value?.let(::ArrayList)) },
    intentGetAction = Intent::getParcelableArrayListUniversally
)

/**
 * Создать аргумент типа [Lambda].
 * Сериализуемый объект дополнительно валидируется на дебаг сборке при помощи [validate].
 */
fun <T> KeySpec.Companion.lambda(argKey: String): KeySpec<T?> = KeySpecImpl(
    key = argKey,
    bundlePutAction = { key, value ->
        value as? Serializable
            ?: throw IllegalArgumentException("Object is not serializable")

        if (BuildConfig.DEBUG) {
            if (!value.isLambda()) throw IllegalArgumentException("Object is not lambda")

            (value as? Serializable)?.validate()
        }
        putSerializable(key, value)
    },
    bundleGetAction = Bundle::getSerializableUniversally,
    intentPutAction = { key, value ->
        value as? Serializable
            ?: throw IllegalArgumentException("Object is not serializable")

        if (BuildConfig.DEBUG) {
            if (!value.isLambda()) throw IllegalArgumentException("Object is not lambda")

            (value as? Serializable)?.validate()
        }
        putExtra(key, value)
    },
    intentGetAction = Intent::getSerializableUniversally
)

private inline fun <reified T : Any> T.isLambda() =
    Lambda::class.java.isAssignableFrom(this::class.java)
