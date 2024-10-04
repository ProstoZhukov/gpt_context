package ru.tensor.sbis.design.view.input.base

import android.os.Parcel
import android.os.Parcelable

/**
 * Состояние [BaseInputView] для сохранения и восстановления при поворотах и прочих ситуациях.
 * @property isFocused true если поле находилось в фокусе, иначе false.
 * @property isExpandedTitle true если метка поля ввода развернута, иначе false.
 *
 * @author ps.smirnyh
 */
internal data class BaseInputViewState(
    val isFocused: Boolean,
    val isExpandedTitle: Boolean
) : Parcelable {

    constructor(src: Parcel) : this(
        isFocused = src.readInt() == 1,
        isExpandedTitle = src.readInt() == 1
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (isFocused) 1 else 0)
        dest.writeInt(if (isExpandedTitle) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BaseInputViewState> {

            override fun createFromParcel(source: Parcel) = BaseInputViewState(source)

            override fun newArray(size: Int) = arrayOfNulls<BaseInputViewState>(size)
        }
    }
}