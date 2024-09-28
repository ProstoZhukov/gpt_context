package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.mappers

import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.data.QuickReplyViewModelBindingModel
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.quick_reply.ui.helpers.QuickReplyClickActionHandler
import ru.tensor.sbis.communicator.declaration.crm.model.QuickReplyParams
import ru.tensor.sbis.consultations.generated.QuickReplyViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.section.SectionOptions
import ru.tensor.sbis.swipeablelayout.api.menu.IconItem
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeIcon
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeItemStyle
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVm
import ru.tensor.sbis.list.view.section.Options as ItemOptions

/**
 * Реализация ItemMapper для crud3.
 *
 * @author dv.baranov
 */
internal class QuickReplyMapper(
    private val actionHandler: QuickReplyClickActionHandler,
    private val params: QuickReplyParams
) : ItemInSectionMapper<QuickReplyViewModel, AnyItem> {
    override fun map(
        item: QuickReplyViewModel,
        defaultClickAction: (QuickReplyViewModel) -> Unit,
    ): AnyItem = BindingItem(
        QuickReplyViewModelBindingModel(
            item.id,
            item.text,
            item.isGroup,
            item.isPinned,
            item.isSeparator,
            item.isTitle,
            item.searchResult,
            item.path,
            needMediumFont = item.isPinned && !params.isEditSearch && !item.isTitle,
            onItemClick = { actionHandler.onItemClick(item) }
        ).apply {
            swipeableVm = item.getSwipeableVm()
        },
        DataBindingViewHolderHelper(
            factory = LayoutIdViewFactory(
                R.layout.communicator_crm_quick_reply_item,
            ),
        ),
        options = Options(
            customBackground = true,
            customSidePadding = true,
            useCustomListeners = true
        ),
    )

    override fun mapSection(item: QuickReplyViewModel): SectionOptions = ItemOptions(
        hasDividers = false,
        hasTopMargin = false,
    )

    private fun QuickReplyViewModel.getSwipeableVm(): SwipeableVm = SwipeableVm(
        uuid = id.toString(),
        menu = when {
            params.isEditSearch || !canPin -> emptyList()
            isPinned -> listOf(
                IconItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_Unpin),
                    SwipeItemStyle.BLUE,
                ) { actionHandler.unpinQuickReply(id) },
            )
            else -> listOf(
                IconItem(
                    SwipeIcon(SbisMobileIcon.Icon.smi_PinNull),
                    SwipeItemStyle.BLUE,
                ) { actionHandler.pinQuickReply(id) },
            )
        },
        isDragLocked = !canPin,
    )
}
