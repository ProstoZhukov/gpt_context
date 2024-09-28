package ru.tensor.sbis.business.common.ui.utils.period

import android.os.Parcel
import android.os.Parcelable
import ru.tensor.sbis.common.util.dateperiod.DatePeriod

/**
 * Периоды для сравнения выручки
 *
 * @property current текущий период
 * @property past прошлый период
 * @property areConnected состояние связи периодов
 */
data class CurrentAndPastPeriod(
    var current: DatePeriod = DatePeriod.getDefaultInstance(),
    var past: DatePeriod = DatePeriod.getDefaultInstance(),
    var areConnected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(DatePeriod::class.java.classLoader)!!,
        parcel.readParcelable(DatePeriod::class.java.classLoader)!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(current, flags)
        parcel.writeParcelable(past, flags)
        parcel.writeByte(if (areConnected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentAndPastPeriod> {
        override fun createFromParcel(parcel: Parcel): CurrentAndPastPeriod {
            return CurrentAndPastPeriod(parcel)
        }

        override fun newArray(size: Int): Array<CurrentAndPastPeriod?> {
            return arrayOfNulls(size)
        }
    }
}
