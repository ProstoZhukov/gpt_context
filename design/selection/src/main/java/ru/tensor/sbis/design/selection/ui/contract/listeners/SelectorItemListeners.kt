package ru.tensor.sbis.design.selection.ui.contract.listeners

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Группа подписок на нажатия по областям элементов в компоненте выбора
 *
 * [Parcelable] реализован вручную, так как в плагине kotlin-parcelize 1.4.21 есть баг генерации @Parcelize с дженериками:
 * https://youtrack.jetbrains.com/issue/KT-43474
 *
 * @author ma.kolpakov
 */
data class SelectorItemListeners<DATA : SelectorItemModel, ACTIVITY : FragmentActivity> @JvmOverloads constructor(
    /**
     * Нажатиие на ячейку
     */
    val itemClickListener: ItemClickListener<DATA, ACTIVITY>? = null,

    /**
     * Длительное нажатии на ячейку
     */
    val itemLongClickListener: ItemClickListener<DATA, ACTIVITY>? = null,

    /**
     * Нажатие на иконку элемента
     */
    val iconClickListener: ItemClickListener<DATA, ACTIVITY>? = null,

    /**
     * Нажатие на (+) или (-) расположенный в правой части ячейки, и при наличии таковой части
     */
    val rightActionListener: ItemClickListener<DATA, ACTIVITY>? = null,
) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        itemClickListener = parcel.readSerializable() as ItemClickListener<DATA, ACTIVITY>?,
        itemLongClickListener = parcel.readSerializable() as ItemClickListener<DATA, ACTIVITY>?,
        iconClickListener = parcel.readSerializable() as ItemClickListener<DATA, ACTIVITY>?,
        rightActionListener = parcel.readSerializable() as ItemClickListener<DATA, ACTIVITY>?,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(itemClickListener)
        parcel.writeSerializable(itemLongClickListener)
        parcel.writeSerializable(iconClickListener)
        parcel.writeSerializable(rightActionListener)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SelectorItemListeners<SelectorItemModel, FragmentActivity>> {
        override fun createFromParcel(parcel: Parcel): SelectorItemListeners<SelectorItemModel, FragmentActivity> {
            return SelectorItemListeners(parcel)
        }

        override fun newArray(size: Int): Array<SelectorItemListeners<SelectorItemModel, FragmentActivity>?> {
            return arrayOfNulls(size)
        }
    }
}
