package ru.tensor.sbis.design.selection.ui.view.selecteditems.panel

import androidx.annotation.Px
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.common.util.PreviewerUrlUtil.replacePreviewerUrlPartWithCheck
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.GroupSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedCompanyItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedFolderItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedItemWithImage
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedPersonItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.createSelectedRecipientItemsConfiguration
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.factory.RecipientSelectedItemViewHolderFactory
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView

/**
 * Реализация [AbstractSelectionPanel] для отображения выбранных получателей с применением стандартных правил сжатия
 *
 * @author ma.kolpakov
 */
internal class RecipientSelectionPanel(
    private val containerView: SelectedItemsContainerView,
    private val selectionVm: MultiSelectionViewModel<in RecipientSelectorItemModel>
) : AbstractSelectionPanel<RecipientSelectorItemModel> {
    /**
     * Размер изображения для панели выбранных. Используется такой же как для аватарок в списке чтобы работать с кэшем
     * превьювера
     */
    @Px
    private val imageSize =
        containerView.resources.getDimensionPixelSize(R.dimen.selection_recipient_profile_photo_size)

    init {
        containerView.setConfiguration(createSelectedRecipientItemsConfiguration(containerView.context))
        containerView.setItemFactory(RecipientSelectedItemViewHolderFactory(containerView))
    }

    override fun setSelectedItems(list: List<RecipientSelectorItemModel>) {
        containerView.setItems(
            list.map {
                when (it) {
                    is PersonSelectorItemModel -> it.toSelectedPersonItem()
                    is GroupSelectorItemModel -> {
                        SelectedItemWithImage(it.id, it.imageUri.withPreviewerSizes(), it.title)
                    }
                    is DepartmentSelectorItemModel -> SelectedFolderItem(it.id, it.title)
                    is ContractorSelectorItemModel -> SelectedCompanyItem(it.id, it.title, it.photoData)
                    else -> error("Unexpected recipient type ${it::class.java}")
                }.apply {
                    onClick = { selectionVm.removeSelection(it) }
                }
            }
        )
    }

    /**
     * Расширение для преобразования модели данных [PersonSelectorItemModel] в модель представления [SelectedPersonItem]
     */
    private fun PersonSelectorItemModel.toSelectedPersonItem(): SelectedPersonItem =
        SelectedPersonItem(id, personData, personName.firstName, personName.lastName)

    /**
     * Подставляет размеры изображения, если ссылка в формате превьювера
     */
    private fun String.withPreviewerSizes() =
        replacePreviewerUrlPartWithCheck(this, imageSize, imageSize, PreviewerUrlUtil.ScaleMode.RESIZE)
}
