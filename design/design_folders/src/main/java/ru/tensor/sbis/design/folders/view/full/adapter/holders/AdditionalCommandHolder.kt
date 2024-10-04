package ru.tensor.sbis.design.folders.view.full.adapter.holders

import android.util.TypedValue
import androidx.core.view.isVisible
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewAditionalCommandBinding
import ru.tensor.sbis.design.folders.view.full.adapter.FolderHolderResourceProvider

/**
 * Вьюхолдер для отображения дополнительной команды в списке
 *
 * @param viewBinding представление view для отображения
 * @param resourceProvider провайдер ресурсов для доступа к цветам и размерам
 *
 * @author ma.kolpakov
 */
internal class AdditionalCommandHolder(
    private val viewBinding: DesignFoldersViewAditionalCommandBinding,
    private val folderActionHandler: FolderActionHandler?,
    resourceProvider: FolderHolderResourceProvider,
) : FolderFullHolderBase<AdditionalCommand>(viewBinding, resourceProvider) {

    companion object {
        /** Идентификатор отсутствует для дополнительных команд и конревой папки */
        const val NO_FOLDER = ROOT_FOLDER_ID
    }

    fun bind(item: AdditionalCommand) {

        viewBinding.apply {
            designFoldersContainer.background = selectorDrawable
            designFoldersTitle.setPaddingByDepth(item)

            designFoldersTitle.setOnClickListener {
                folderActionHandler?.handleAction(FolderActionType.ADDITIONAL_COMMAND_TITLE_CLICK, NO_FOLDER)
            }
            designFoldersStateIcon.setOnClickListener {
                folderActionHandler?.handleAction(FolderActionType.ADDITIONAL_COMMAND_ICON_CLICK, NO_FOLDER)
            }

            designFoldersContainer.setOnClickListener {
                folderActionHandler?.handleAction(FolderActionType.ADDITIONAL_COMMAND_CLICK, NO_FOLDER)
            }

            designFoldersTitle.text = item.title

            designFoldersStateIcon.isVisible = item.type.hasIcon
            if (item.type.hasIcon) {
                designFoldersStateIcon.apply {
                    text = item.type.icon!!.character.toString()
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, resourceProvider.getDimen(item.type.size))
                }
            }
        }
    }
}
