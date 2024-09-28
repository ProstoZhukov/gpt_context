package ru.tensor.sbis.main_screen_decl

import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.env.MainScreenMenu
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenProvider

/**
 * Интерфейс главного экрана
 *
 * @author kv.martyshenko
 */
interface MainScreen {

    /**
     * @property host хост главного экрана
     */
    val host: MainScreenHost

    /**
     * @property menu
     */
    val menu: MainScreenMenu

    /**
     * Поставщики экранов с содержимым разделов/вкладок, предоставляемые прикладными модулями.
     */
    val screenEntries: List<MainScreenEntry>

    /**
     * Метод для активации элемента меню.
     *
     * @param item элемент меню
     */
    fun select(item: NavigationItem)

    /**
     * Выполняется ли в данный момент транзакция выбора пункта.
     */
    fun isCurrentlySelecting(): Boolean

    /**
     * Предоставляет поставщик экранов дашбордов, если доступен.
     */
    fun getDashboardScreenProvider(): DashboardScreenProvider?
}