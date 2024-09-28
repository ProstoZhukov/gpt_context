package ru.tensor.sbis.person_decl.motivation.ui

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes

/**
 * Данные для экрана детальная по KPI
 * @param label - Название KPI
 * @param value - Значение KPI
 * @param markId - id KPI
 * @param accuracy - Точность после запятой
 * @param plan - Значение плана
 * @param comment - Комментарий
 * @param percentage - Проценты выполнения плана
 * @param color - цвет value
 * @param planUnitAppearanceStyleRes - стиль для плана
 * @param valueUnitAppearanceStyleRes - стиль для значения
 * @param isMoney - показатель является денежным
 */
data class KpiDetailsData(
    var label: String,
    var value: String,
    val markId: Long,
    val accuracy: Int,
    val plan: String,
    val comment: String,
    val percentage: Int,
    @ColorInt val color: Int,
    @StyleRes val planUnitAppearanceStyleRes: Int?,
    @StyleRes val valueUnitAppearanceStyleRes: Int? = null,
    val isMoney: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(label)
        parcel.writeString(value)
        parcel.writeLong(markId)
        parcel.writeInt(accuracy)
        parcel.writeString(plan)
        parcel.writeString(comment)
        parcel.writeInt(percentage)
        parcel.writeInt(color)
        parcel.writeValue(planUnitAppearanceStyleRes)
        parcel.writeValue(valueUnitAppearanceStyleRes)
        parcel.writeByte(if (isMoney) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KpiDetailsData> {
        override fun createFromParcel(parcel: Parcel): KpiDetailsData {
            return KpiDetailsData(parcel)
        }

        override fun newArray(size: Int): Array<KpiDetailsData?> {
            return arrayOfNulls(size)
        }

        val EMPTY = KpiDetailsData(
            "",
            "",
            0L,
            0,
            "",
            "",
            0,
            0,
            0,
            0,
            true
        )
    }
}