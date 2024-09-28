package ru.tensor.sbis.share_menu.ui.data

/**
 * Данные для отображения панели вкладок компонента меню шаринга.
 *
 * @property items вкладки
 * @property selected выбранная вкладка
 *
 * @author vv.chekurda
 */
internal data class ShareMenuTabsData(
    val items: List<ShareMenuTabItem> = emptyList(),
    val selected: ShareMenuTabItem? = null
)