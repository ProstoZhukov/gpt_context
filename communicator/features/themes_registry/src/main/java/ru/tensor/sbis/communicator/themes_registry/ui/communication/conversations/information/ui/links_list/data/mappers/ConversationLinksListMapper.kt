package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.mappers

import android.content.Context
import ru.tensor.sbis.communicator.generated.LinkViewModel
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinksListViewModelBindingModel
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui.helpers.LinkItemLongClickHandler
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.section.SectionOptions
import ru.tensor.sbis.richtext.converter.cfg.Configuration
import ru.tensor.sbis.richtext.converter.json.JsonRichTextConverter
import ru.tensor.sbis.list.view.section.Options as ItemOptions

/**
 * Реализация ItemMapper для crud3 списка ссылок на экране информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListMapper(
    context: Context,
    private val actionHandler: LinkItemLongClickHandler
) : ItemInSectionMapper<LinkViewModel, AnyItem> {

    private val converter = JsonRichTextConverter(context, Configuration.withDecoratedLinks())

    override fun map(item: LinkViewModel, defaultClickAction: (LinkViewModel) -> Unit): AnyItem {
        return BindingItem(
            ConversationLinksListViewModelBindingModel(
                item.id,
                item.link,
                item.messageId,
                item.isPinned,
                onLongItemClick = { view -> actionHandler.onLongItemClick(item, view) },
                convertAction = { link -> converter.convert(link) }
            ),
            DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(
                    R.layout.commuincator_item_conversation_links_list
                ),
            ),
            options = Options(
                customBackground = true,
                customSidePadding = true,
                useCustomListeners = true
            )
        )
    }

    override fun mapSection(item: LinkViewModel): SectionOptions = ItemOptions(
        hasDividers = false,
        hasTopMargin = false,
    )
}