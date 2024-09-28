package ru.tensor.sbis.segmented_control.utils

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * Класс для сохранения состояния сегмент-контрола.
 *
 * @author ps.smirnyh
 */
internal class SavedState : View.BaseSavedState {

    var selectedSegmentIndex = 0

    // Конструктор для сохранения стейта
    constructor(superState: Parcelable?) : super(superState)

    // Конструктоп для восстановления стейта
    constructor(source: Parcel?) : super(source) {
        source?.apply {
            selectedSegmentIndex = readInt()
        }
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(selectedSegmentIndex)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel?): SavedState = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}