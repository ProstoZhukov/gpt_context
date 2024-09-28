package ru.tensor.sbis.share_menu.ui.data

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.tab_panel.TabPanelItem
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuItem

/**
 * Вкладка панели меню для "поделиться".
 *
 * @param navItem элемент навигации.
 *
 * @author vv.chekurda
 */
@Parcelize
internal data class ShareMenuTabItem(
    val navItem: ShareMenuItem
) : TabPanelItem {
    override val id: String
        get() = navItem.id
    override val icon: SbisMobileIcon.Icon
        get() = navItem.icon
    override val title: Int
        get() = navItem.title
}