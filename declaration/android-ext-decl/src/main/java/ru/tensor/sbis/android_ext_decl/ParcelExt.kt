@file:JvmName("ParcelExt")

package ru.tensor.sbis.android_ext_decl

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

@Deprecated("Use readValue or readNullableValue instead", replaceWith = ReplaceWith("readValue"))
inline fun <reified T> Parcel.read(): T = readValue(T::class.javaClass.classLoader) as T

fun Parcel.write(vararg values: Any?) = values.forEach { writeValue(it) }

//добавлен префикс ext из-за конфликта именования с Sdk "Call requires API level 29 (current min is 21): android.os.Parcel#readBoolean"
fun Parcel.extReadBoolean() = readInt() != 0
fun Parcel.extWriteBoolean(value: Boolean) = writeInt(if (value) 1 else 0)

fun Parcel.writeUUID(value: UUID?) = writeString(value?.toString())
fun Parcel.readUUID(): UUID = readNullableUUID()
    ?: throw IllegalStateException("Unable to read UUID")

fun Parcel.readNullableUUID(): UUID? = readString()?.run(UUID::fromString)

fun Parcel.writeBigDecimal(value: BigDecimal?) = writeString(value?.toPlainString())
fun Parcel.readBigDecimal(): BigDecimal = readNullableBigDecimal()
    ?: throw IllegalStateException("Unable to read BigDecimal")

fun Parcel.readNullableBigDecimal(): BigDecimal? = readString()?.run { BigDecimal(this) }

fun <T : Enum<T>> Parcel.writeEnum(value: T?) = writeInt(value?.ordinal ?: -1)

@Deprecated(
    "Use nullable variant if null values supported",
    replaceWith = ReplaceWith("readNullableEnum")
)
inline fun <reified T : Enum<T>> Parcel.readEnum() = readInt().let { if (it >= 0) enumValues<T>()[it] else null }

// TODO: 1/26/2019  rename to readEnum() when deprecated method will be removed
inline fun <reified T : Enum<T>> Parcel.readNoNullEnum(): T = readNullableEnum<T>()
    ?: throw IllegalStateException("Unable to read enum ${T::class}")

inline fun <reified T : Enum<T>> Parcel.readNullableEnum(): T? = readInt().takeIf { it >= 0 }
    ?.let { enumValues<T>()[it] }

inline fun <reified T : Enum<T>> Parcel.readEnumList(): List<T> {
    val enumValues = enumValues<T>()
    return createIntArray()!!.map { enumValues[it] }
}

fun <T : Enum<T>> Parcel.writeEnumList(value: List<T>) = writeIntArray(IntArray(value.size) { value[it].ordinal })

/**
 * Взято из [Parcel.readTypedObject], нужно для [readTypedObjectCompat]
 */
inline fun <T> Parcel.readNullable(reader: () -> T) = if (readInt() != 0) reader() else null

/**
 * Взято из [Parcel.writeTypedObject], нужно для [writeTypedObjectCompat]
 */
inline fun <T> Parcel.writeNullable(value: T?, writer: (T) -> Unit) {
    if (value != null) {
        writeInt(1)
        writer(value)
    } else {
        writeInt(0)
    }
}

/**
 * Аналог [Parcel.readTypedObject], но для любой версии API
 */
fun <T : Parcelable> Parcel.readTypedObjectCompat(c: Parcelable.Creator<T>) =
    readNullable { c.createFromParcel(this) }

/**
 * Аналог [Parcel.writeTypedObject], но для любой версии API
 */
fun <T : Parcelable> Parcel.writeTypedObjectCompat(value: T?, parcelableFlags: Int) =
    writeNullable(value) { it.writeToParcel(this, parcelableFlags) }

/**
 * Псевдоним для [Parcel.readParcelable] с автоматическим вычислением загрузчика
 *
 * @see [Parcel.readNullableParcelable]
 *
 * @throws NullPointerException, если [Parcel.readParcelable] вернул `null`
 */
inline fun <reified T : Parcelable> Parcel.readParcelable(): T = readParcelable(T::class.java.classLoader)
    ?: throw NullPointerException("Parcel was broken. ${T::class} expected but null found")

/**
 * Псевдоним для [Parcel.readParcelable] с автоматическим вычислением загрузчика, который допускает
 * загрузку `null` значений
 *
 * @see [Parcel.readParcelable]
 */
@Deprecated(
    message = "устаревший метод для новых API, начиная с андройд 13+ https://developer.android.com/reference/android/content/Intent#getSerializableExtra(java.lang.String,%20java.lang.Class%3CT%3E)",
    replaceWith = ReplaceWith("readNullableParcelableCompat() /*ru.tensor.sbis.android_ext_decl.readNullableParcelableCompat*/ ")
)
inline fun <reified T : Parcelable> Parcel.readNullableParcelable(): T? = readParcelable(T::class.java.classLoader)

/**
 * @see [readNullableParcelable]
 */
@RequiresApi(33)
inline fun <reified T : Parcelable> Parcel.readNullableParcelable(cl: Class<T>): T? =
    readParcelable(T::class.java.classLoader, cl)

/**
 * @see [readNullableParcelable]
 */
inline fun <reified T : Parcelable> Parcel.readNullableParcelableCompat(cl: Class<T>): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        readNullableParcelable(cl)
    } else {
        @Suppress("DEPRECATION")
        readNullableParcelable()
    }



/**
 * Псевдоним для [Parcel.readValue] с автоматическим вычислением загрузчика
 *
 * @see [Parcel.readNullableValue]
 *
 * @throws NullPointerException, если [Parcel.readValue] вернул `null`
 */
inline fun <reified T> Parcel.readValue(): T = readValue(T::class.java.classLoader) as? T
    ?: throw NullPointerException("Parcel was broken. ${T::class} expected but null found")

/**
 * Псевдоним для [Parcel.readValue] с автоматическим вычислением загрузчика, который допускает
 * загрузку `null` значений
 *
 * @see [Parcel.readValue]
 */
inline fun <reified T> Parcel.readNullableValue(): T? = readValue(T::class.java.classLoader) as T?

fun Parcel.readDate() = readNullable { Date(readLong()) }

fun Parcel.writeDate(value: Date?) = writeNullable(value) { writeLong(it.time) }

/**
 * Запись списка [Parcelable] объектов, используется в случае если не известен конкретный тип объектов.
 * @see [Parcel.extReadList]
 */
fun <T : Parcelable> Parcel.extWriteList(list: List<T>, flags: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        writeParcelableList(list, flags)
    } else {
        writeList(list)
    }
}

/**
 * Чтение списка [Parcelable] объектов, используется в случае если не известен конкретный тип объектов.
 * @see [Parcel.extWriteList]
 */
inline fun <reified T : Parcelable> Parcel.extReadList(): List<T> {
    val arr: List<T> = arrayListOf()
    val classLoader = T::class.java.classLoader
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        readParcelableList(arr, classLoader)
    } else {
        readList(arr, classLoader)
        arr
    }
}