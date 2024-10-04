package ru.tensor.sbis.design.folders.view.full.adapter.holders

import android.util.TypedValue.COMPLEX_UNIT_PX
import androidx.core.view.isVisible
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.databinding.DesignFoldersItemViewFullBinding
import ru.tensor.sbis.design.folders.view.full.adapter.FolderHolderResourceProvider

/**
 * Вьюхолдер для отображения папки в списке
 *
 * @param viewBinding представление view для отображения
 * @param folderActionHandler слушатель действия папки
 * @param resourceProvider провайдер ресурсов для доступа к цветам и размерам
 *
 * @author ma.kolpakov
 */
internal class FolderFullHolder(
    private val viewBinding: DesignFoldersItemViewFullBinding,
    private val folderActionHandler: FolderActionHandler?,
    resourceProvider: FolderHolderResourceProvider,
) : FolderFullHolderBase<Folder>(viewBinding, resourceProvider) {

    fun bind(item: Folder, selectedFolderId: String? = null) {
        viewBinding.apply {
            designFoldersContainer.background = selectorDrawable
            designFoldersTitle.apply {
                setPaddingByDepth(item)
                text = item.title
            }
            designFoldersMarker.isVisible = item.id == selectedFolderId
            designFoldersTextCounter.accentedCounter = item.unreadContentCount
            designFoldersTextCounter.unaccentedCounter = item.totalContentCount
        }
        setIcon(item)
        setClickListener(item)
        setSwipeActions(item)
    }

    private fun setIcon(item: Folder) {
        val hasIcon = item.type.hasIcon
        viewBinding.designFoldersStateIcon.isVisible = hasIcon
        if (hasIcon) {
            viewBinding.designFoldersStateIcon.apply {
                setText(item.type.iconRes)
                setTextSize(COMPLEX_UNIT_PX, resourceProvider.getDefaultStateIconSize())
            }
        }
    }

    private fun setClickListener(item: Folder) {
        viewBinding.designFoldersContainer.setOnClickListener {
            folderActionHandler?.handleAction(FolderActionType.CLICK, item.id)
        }
    }

    private fun setSwipeActions(item: Folder) {
        val hasActions = item.type.hasActions
        viewBinding.designFoldersSwipeLayout.isDragLocked = !hasActions

        if (hasActions) {
            val swipeActions = item.type.actions.map {
                it.swipeMenuItem.clickAction = {
                    viewBinding.designFoldersSwipeLayout.close()
                    folderActionHandler?.handleAction(it, item.id, item.title)
                }
                it.swipeMenuItem.isClickPostponedUntilMenuClosed = true
                it.swipeMenuItem
            }
            viewBinding.designFoldersSwipeLayout.setMenu(swipeActions)
        }
    }
}
