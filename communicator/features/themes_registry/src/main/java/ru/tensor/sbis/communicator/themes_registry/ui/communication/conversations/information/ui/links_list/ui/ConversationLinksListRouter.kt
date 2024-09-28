package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.ui

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.data.ConversationLinkOption
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.mvi_extension.router.Router
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter

/**
 * Интерфейс роутера экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal interface ConversationLinksListRouter : Router<Fragment> {

    /** Показать меню опций при лонглике на ссылку. */
    fun showLinkMenu(
        anchor: View,
        isPinned: Boolean,
        fromMessage: Boolean,
        onOptionSelected: (option: ConversationLinkOption) -> Unit
    )
}

/**
 * Реализация роутера экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal class ConversationLinksListRouterImpl : FragmentRouter(), ConversationLinksListRouter {
    override fun showLinkMenu(
        anchor: View,
        isPinned: Boolean,
        fromMessage: Boolean,
        onOptionSelected: (option: ConversationLinkOption) -> Unit
    ) = execute { showMenu(anchor, isPinned, fromMessage, onOptionSelected) }

    private fun Fragment.showMenu(
        anchor: View,
        isPinned: Boolean,
        fromMessage: Boolean,
        onOptionSelected: (option: ConversationLinkOption) -> Unit
    ) {
        val sbisMenu = SbisMenu(
            children = getOptions(requireContext(), isPinned, fromMessage, onOptionSelected)
        )
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.BOTTOM,
                force = false
            ).apply { anchorView = anchor },
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT,
                force = false,
                innerPosition = true
            ).apply { anchorView = anchor },
            dimType = DimType.SOLID
        )
    }

    private fun getOptions(
        context: Context,
        isPinned: Boolean,
        fromMessage: Boolean,
        onOptionSelected: (option: ConversationLinkOption) -> Unit,
    ): List<MenuItem> = buildList {
        add(ConversationLinkOption.OPEN_LINK.getItem(context, onOptionSelected))
        doIf(isPinned) { add(ConversationLinkOption.UNPIN.getItem(context, onOptionSelected)) }
        doIf(!isPinned) { add(ConversationLinkOption.PIN.getItem(context, onOptionSelected)) }
        add(ConversationLinkOption.COPY.getItem(context, onOptionSelected))
        doIf(fromMessage) { add(ConversationLinkOption.GO_TO_MESSAGE.getItem(context, onOptionSelected)) }
        add(ConversationLinkOption.DELETE.getItem(context, onOptionSelected))
    }

    private fun ConversationLinkOption.getItem(
        context: Context,
        onOptionSelected: (option: ConversationLinkOption) -> Unit
    ) = MenuItem(
        title = context.getString(textRes),
        image = iconRes,
        imageColor = iconColor,
        imageAlignment = HorizontalPosition.LEFT
    ) {
        onOptionSelected(this)
    }
}