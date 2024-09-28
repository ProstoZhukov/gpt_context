package ru.tensor.sbis.design.tabs.util

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * @author da.zolotarev
 */
internal class SbisTabsViewSavedState : View.BaseSavedState {
    var selectedItemIndex = 0

    // Конструктор для сохранения стейта.
    constructor(superState: Parcelable?) : super(superState)

    // Конструктор для восстановления стейта.
    constructor(source: Parcel?) : super(source) {
        source?.apply {
            selectedItemIndex = readInt()
        }
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeInt(selectedItemIndex)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SbisTabsViewSavedState> = object : Parcelable.Creator<SbisTabsViewSavedState> {
            override fun createFromParcel(source: Parcel?): SbisTabsViewSavedState = SbisTabsViewSavedState(source)
            override fun newArray(size: Int): Array<SbisTabsViewSavedState?> = arrayOfNulls(size)
        }
    }
}