package ru.tensor.sbis.folderspanel

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.databinding.ObservableField
import ru.tensor.sbis.common.util.ItemSwipeState
import ru.tensor.sbis.common_filters.base.Clickable
import ru.tensor.sbis.common_filters.base.ClickableVm
import ru.tensor.sbis.common_filters.base.FilterItem
import ru.tensor.sbis.common_filters.base.FilterType
import ru.tensor.sbis.common_filters.base.NoType
import ru.tensor.sbis.common_filters.base.Selectable
import ru.tensor.sbis.common_filters.base.SelectableVm
import ru.tensor.sbis.swipeablelayout.DefaultMenuItem
import ru.tensor.sbis.swipeablelayout.DefaultSwipeMenu
import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.swipeablevm.MutableSwipeableVmHolder
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import ru.tensor.sbis.swipeablelayout.util.SwipeableViewmodelsHolder

/**
 * ViewModel папки
 */
data class FolderViewModel @JvmOverloads constructor(
    override val uuid: String,
    val level: Int,
    override val title: String,
    @field:IdRes
    val statusIcon: Int = 0,
    val unreadCount: Int,
    val showUnreadCount: Boolean = unreadCount > 0,
    val totalCount: Int,
    val showTotalCount: Boolean = totalCount > 0,
    val swipeEnabled: Boolean,
    override val type: FilterType = NoType,
    var onClick: ((FolderViewModel) -> Unit)? = null,
    var longestTotalCount: String = "",
    val parentUuid: String? = null,
    var swipeMenuItems: List<DefaultMenuItem>? = null,
    val swipeState: ObservableField<ItemSwipeState> = ObservableField(ItemSwipeState.CLOSED),
    var actionOnSwipeOpenStart: Runnable? = null,
    val clickableVm: ClickableVm = ClickableVm(),
    val selectableVm: SelectableVm = SelectableVm(),
    override var swipeableVm: SwipeableVm = SwipeableVm(""),
    val canChange: Boolean = true,
    val canRemove: Boolean = true
) : FilterItem, MutableSwipeableVmHolder, Clickable by clickableVm, Selectable by selectableVm, Parcelable {

    override fun onClick() {
        selectableVm.onClick()
        clickableVm.onClick()
        onClick?.invoke(this)
    }

    /**@SelfDocumented*/
    fun setSwipeMenu(
        swipeItems: List<DefaultMenuItem>,
        isDragLocked: Boolean,
        actionOnDismiss: Runnable
    ) {
        swipeableVm = SwipeableVm(
            uuid,
            DefaultSwipeMenu(swipeItems),
            isDragLocked = isDragLocked,
            onDismissed = actionOnDismiss
        )
    }

    /**@SelfDocumented*/
    fun getSwipeMenu(): SwipeMenu<*>? {
        return swipeableVm.swipeMenu
    }

    // region parcelable
    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readString().orEmpty(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        longestTotalCount = parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeInt(level)
        parcel.writeString(title)
        parcel.writeInt(statusIcon)
        parcel.writeInt(unreadCount)
        parcel.writeByte(if (showUnreadCount) 1 else 0)
        parcel.writeInt(totalCount)
        parcel.writeByte(if (showTotalCount) 1 else 0)
        parcel.writeByte(if (swipeEnabled) 1 else 0)
        parcel.writeString(longestTotalCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FolderViewModel> {
        override fun createFromParcel(parcel: Parcel): FolderViewModel {
            return FolderViewModel(parcel)
        }

        override fun newArray(size: Int): Array<FolderViewModel?> {
            return arrayOfNulls(size)
        }

        val areItemsTheSame = { old: FolderViewModel, new: FolderViewModel -> old.uuid == new.uuid }

        /**
         * Выполняет упрощённое сравнение содержимого, позволяющее избежать избыточных выполнений повторной привязки
         * данных. Поскольку при сравнении не учитывается [swipeableVm], требуется применять [SwipeableViewmodelsHolder]
         * для правильной работы свайп-меню
         */
        val areContentsTheSame
            get() = { old: FolderViewModel, new: FolderViewModel ->
                old.level == new.level
                        && old.title == new.title
                        && old.unreadCount == new.unreadCount
                        && old.totalCount == new.totalCount
                        && old.swipeableVm == new.swipeableVm
            }

    }
    // endregion
}