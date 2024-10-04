package ru.tensor.sbis.design.tabs.api

import ru.tensor.sbis.design.tabs.util.SbisTabsViewDsl
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.LinkedList

/**
 * Модель вкладки [SbisTabsView].
 *
 * @param content Массив вкладок.
 * @param isMain Является ли вкладка главной.
 * @param position Положение вкладки относительно панели.
 * @param customTitleColor Кастомный цвет заголовка.
 * @param customIconColor Кастомный цвет иконки.
 * @param id ID вкладки.
 * @param navxId Navx ID вкладки.
 *
 * Для создания можно использовать [SbisTabsViewDsl].
 * @author da.zolotarev
 */
data class SbisTabsViewItem(
    val content: LinkedList<SbisTabViewItemContent> = LinkedList<SbisTabViewItemContent>(),
    /**
     *  Будет удалено по https://dev.sbis.ru/opendoc.html?guid=07557630-6756-41e2-aa41-58bdbea1add1&client=3
     */
    @Deprecated("Теперь выбранностью управляет панель вкладок (selectedTabIndex)")
    val isSelected: Boolean = false,
    val isMain: Boolean = false,
    val position: HorizontalPosition = HorizontalPosition.LEFT,
    val customTitleColor: SbisColor? = null,
    val customIconColor: SbisColor? = null,
    val id: String? = null,
    val navxId: NavxIdDecl? = null
)
