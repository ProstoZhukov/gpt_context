package ru.tensor.sbis.app_file_browser.presentation

import androidx.annotation.StringRes
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeTextItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.TextItem
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import java.util.UUID

/**
 * Вьюмодель элемента списка файлов и папок.
 *
 * @author us.bessonov
 */
internal data class FileInfoItemViewModel(
    val id: UUID,
    val name: String,
    val path: String,
    val size: String,
    @StringRes val fileIcon: Int,
    val checkboxValue: SbisCheckboxValue,
    val isSizeVisible: Boolean,
    /**
     * Иконка, показывающая возможность проваливания в папку.
     */
    val isArrowVisible: Boolean,
    val checkStateChangeAction: () -> Unit,
    private val swipeMenu: List<SwipeMenuItem>,
    val onClickAction: () -> Unit
) : ComparableItem<FileInfoItemViewModel> {

    val swipeableVM: SwipeableVm = SwipeableVm(id.toString(), menu = swipeMenu)

    /** @SelfDocumented */
    fun onCheckStateChanged() = checkStateChangeAction()

    override fun areTheSame(otherItem: FileInfoItemViewModel) = id == otherItem.id
}

