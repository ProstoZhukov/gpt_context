package ru.tensor.sbis.app_file_browser.presentation

import ru.tensor.sbis.app_file_browser.R
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.mfb.generated.FileInfo
import ru.tensor.sbis.mfb.generated.FileType
import ru.tensor.sbis.mfb.generated.SelectionState
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeTextItemStyle
import ru.tensor.sbis.swipeablelayout.api.menu.TextItem

/**
 * Коллбэк обработки событий на вью.
 */
internal typealias FileAction = (FileInfo) -> Unit

/**
 * Преобразует модель элемента списка от контроллера во вьюмодель для использования в компоненте.
 *
 * @author us.bessonov
 */
internal class Mapper(
    private val onSelectionChanged: Lazy<FileAction>,
    private val onShowSize: Lazy<FileAction>,
    private val onDelete: Lazy<FileAction>
) : ItemInSectionMapper<FileInfo, AnyItem> {

    override fun map(item: FileInfo, defaultClickAction: FileAction): AnyItem {
        val isFolder = item.fileType == FileType.DIR
        val data = item.toItemViewModel(
            defaultClickAction.takeIf { isFolder },
            onSelectionChanged.value,
            onShowSize.value.takeIf { isFolder },
            onDelete.value.takeIf { item.isDeletable }
        )
        return BindingItem(
            data,
            R.layout.app_file_browser_list_item,
            data,
            Options(
                customSidePadding = true
            )
        )
    }
}

private fun FileInfo.toItemViewModel(
    onClickAction: FileAction?,
    onSelectionChanged: FileAction,
    onShowSize: FileAction?,
    onDelete: FileAction?
) = FileInfoItemViewModel(
    id,
    name,
    path,
    size,
    fileIcon = if (fileType == FileType.DIR) ru.tensor.sbis.design.R.string.design_mobile_icon_folder_open
    else ru.tensor.sbis.design.R.string.design_mobile_icon_sabydoc,
    checkboxValue = when (isSelected) {
        SelectionState.SELECTED -> SbisCheckboxValue.CHECKED
        SelectionState.NOT_SELECTED -> SbisCheckboxValue.UNCHECKED
        SelectionState.PARTIALLY_SELECTED -> SbisCheckboxValue.UNDEFINED
    },
    isSizeVisible = size.isNotEmpty(),
    isArrowVisible = fileType == FileType.DIR,
    { onSelectionChanged(this) },
    mutableListOf<TextItem>().apply {
        onShowSize?.let {
            add(TextItem(R.string.app_file_browser_show_size, SwipeTextItemStyle.DEFAULT) { it(this@toItemViewModel) })
        }
        onDelete?.let {
            add(TextItem(R.string.app_file_browser_delete, SwipeTextItemStyle.DANGER) { it(this@toItemViewModel) })
        }
    }
) {
    onClickAction?.invoke(this)
}