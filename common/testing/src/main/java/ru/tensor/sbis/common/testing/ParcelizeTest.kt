package ru.tensor.sbis.common.testing

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/**
 * Тестирование Parcelable реализованного посредством аннотации @Parcelize через создание контейнера Bundle.
 * Parcelables не подойдет, не хватает поля CREATOR (присутсвует в декомпилированном коде)
 */
inline fun <reified R : Parcelable> R.testParcelize(): R {
    val bytes = marshallParcelable(this)
    return unmarshallParcelable(bytes)
}

inline fun <reified R : Parcelable> marshallParcelable(parcelable: R): ByteArray {
    val bundle = Bundle().apply { putParcelable(R::class.java.name, parcelable) }
    val parcel = Parcel.obtain()
    parcel.writeBundle(bundle)
    val marshall = parcel.marshall()
    parcel.recycle()
    return marshall
}

@SuppressLint("ParcelClassLoader")
inline fun <reified R : Parcelable> unmarshallParcelable(bytes: ByteArray): R =
    Parcel.obtain().run {
        unmarshall(bytes, 0, bytes.size)
        setDataPosition(0)
        val bundle = readBundle()!!
        recycle()
        bundle
    }.run {
        classLoader = R::class.java.classLoader
        getParcelable(R::class.java.name)!!
    }