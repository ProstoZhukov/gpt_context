package ru.tensor.sbis.design.text_span.text.util

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import android.view.View

/**
 * Класс, предназначенный для раздельного сохранения состояний дочерних View. Позволяет избежать восстановления одного
 * и того же состояния для нескольких View с одинаковым id на одном экране
 *
 * @author us.bessonov
 */
@Suppress("UNCHECKED_CAST")
class SeparateChildrenSavedState : View.BaseSavedState {
    private var childrenStates = SparseArray<Parcelable>()

    constructor(superState: Parcelable?) : super(superState)

    private constructor(parcel: Parcel, classLoader: ClassLoader?) : super(parcel) {
        parcel.readSparseArray<Parcelable>(classLoader)
            ?.let { childrenStates = it }
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeSparseArray(childrenStates as SparseArray<Any>)
    }

    /** @SelfDocumented */
    fun setChildrenStates(childrenStates: SparseArray<Parcelable>) {
        this.childrenStates = childrenStates
    }

    /** @SelfDocumented */
    fun getChildrenStates() = childrenStates

    companion object {

        @JvmField
        val CREATOR: Parcelable.ClassLoaderCreator<SeparateChildrenSavedState> =
            object : Parcelable.ClassLoaderCreator<SeparateChildrenSavedState> {
                override fun createFromParcel(source: Parcel, loader: ClassLoader?): SeparateChildrenSavedState {
                    return SeparateChildrenSavedState(source, loader)
                }

                override fun createFromParcel(source: Parcel): SeparateChildrenSavedState {
                    return createFromParcel(source, null)
                }

                override fun newArray(size: Int): Array<SeparateChildrenSavedState?> {
                    return arrayOfNulls(size)
                }
            }
    }
}