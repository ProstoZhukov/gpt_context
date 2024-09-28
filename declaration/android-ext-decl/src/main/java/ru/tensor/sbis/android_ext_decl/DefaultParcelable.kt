package ru.tensor.sbis.android_ext_decl

import android.os.Parcel
import android.os.Parcelable

/**
 * Generic Parcelable interface for easily creating parcelables.
 */
interface DefaultParcelable : Parcelable {

    override fun describeContents(): Int = 0

    companion object {

        inline fun <reified T> generateCreator(crossinline create: (Parcel) -> T) =
            object : Parcelable.Creator<T> {

                override fun createFromParcel(source: Parcel) = create(source)

                override fun newArray(size: Int) = arrayOfNulls<T>(size)
            }
    }
}