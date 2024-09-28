package ru.tensor.sbis.design.folders.view.full

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * @author ma.kolpakov
 */
internal class FolderListViewSavedState : View.BaseSavedState {

    /**
     * Внутреннее состояние списка папок
     */
    val folderListState: Bundle

    constructor(superState: Parcelable?) : super(superState) {
        folderListState = Bundle()
    }

    constructor(parcel: Parcel) : super(parcel) {
        folderListState = parcel.readBundle(FolderListView::class.java.classLoader)!!
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeBundle(folderListState)
    }

    companion object CREATOR : Parcelable.Creator<FolderListViewSavedState> {
        override fun createFromParcel(parcel: Parcel): FolderListViewSavedState = FolderListViewSavedState(parcel)
        override fun newArray(size: Int): Array<FolderListViewSavedState?> = arrayOfNulls(size)
    }

    override fun describeContents(): Int = 0
}