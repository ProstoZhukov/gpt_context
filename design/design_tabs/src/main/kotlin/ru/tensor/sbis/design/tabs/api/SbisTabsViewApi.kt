package ru.tensor.sbis.design.tabs.api

import ru.tensor.sbis.design.theme.zen.ZenThemeSupport
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.LinkedList

interface SbisTabsViewApi : ZenThemeSupport {
    /**
     * Стиль панели вкладок.
     */
    var isAccent: Boolean

    /**
     * Кастомный стиль панели вкладок.
     */
    var style: SbisTabsStyle

    /**
     * Список моделей вкладок, отрисовываются в соответствии с порядком в списке.
     */
    var tabs: LinkedList<SbisTabsViewItem>

    /**
     * Индекс выбранной вкладки.
     */
    var selectedTabIndex: Int

    /**
     * Установлен ли стиль вкладок как в старой шапке.
     *
     * Данный параметр нужно устанавливать перед установкой [tabs].
     */
    var isOldToolbarDesign: Boolean

    /**
     * Видимость нижней границы вкладок
     */
    var isBottomBorderVisible: Boolean

    /**
     * Установить обработчик нажатия на вкладку, возвращает модель нажатой вкладки.
     */
    fun setOnTabClickListener(listener: (SbisTabsViewItem) -> Unit)

    /**
     * Спрятать вкладку по [SbisTabsViewItem.id].
     */
    fun hideTab(tabId: String)

    /**
     * Показать вкладку по [SbisTabsViewItem.id].
     */
    fun showTab(tabId: String)

    /**
     * Спрятать вкладку по [SbisTabsViewItem.navxId].
     */
    fun hideTab(navxId: NavxIdDecl)

    /**
     * Показать вкладку по [SbisTabsViewItem.navxId].
     */
    fun showTab(navxId: NavxIdDecl)
}