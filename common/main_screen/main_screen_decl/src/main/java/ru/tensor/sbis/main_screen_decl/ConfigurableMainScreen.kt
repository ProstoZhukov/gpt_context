package ru.tensor.sbis.main_screen_decl

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.design.navigation.view.model.NavigationCounter
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.content.NavigationItemContent
import ru.tensor.sbis.design.navigation.view.view.navmenu.icon_button.NavIconButton
import ru.tensor.sbis.design.navigation.view.widget.NavItemWidget
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.DashboardContentController
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Настраиваемый главный экран
 *
 * @author kv.martyshenko
 */
interface ConfigurableMainScreen : MainScreen {

    /**
     * Метод для добавления элемента меню.
     * При добавлении элементы автоматически становятся видимыми.

     * @param item элемент меню
     * @param contentController контроллер для управления контентом добавляемого элемента.
     */
    fun addItem(
        item: NavigationItem,
        contentController: ContentController
    )

    /**
     * Метод для добавления элемента меню.
     * При добавлении элементы автоматически становятся видимыми на основе переданной конфигурации.
     *
     * @param item элемент меню.
     * @param configuration конфигурация элемента.
     * @param contentController контроллер для управления контентом добавляемого элемента.
     */
    fun addItem(
        item: NavigationItem,
        configuration: MenuItemConfiguration,
        contentController: ContentController
    )

    /**
     * Метод для удаления элемента меню.
     *
     * @param item элемент меню.
     */
    fun removeItem(item: NavigationItem)

    /**
     * Метод для мониторинга прав по определенной зоне.
     *
     * @param permissionScope зона доступа.
     */
    fun monitorPermissionScope(permissionScope: PermissionScope): LiveData<PermissionLevel?>

    /**
     * Зарегистрировать поставщик конфигурации дашборда.
     */
    fun registerDashboardContentController(dashboardContentController: DashboardContentController)

    /**
     * Метод для получения доступных расширений по обработке [Intent].
     *
     * @param key ключ расширения.
     */
    fun <K : IntentHandleExtension.ExtensionKey, E : IntentHandleExtension<K>> getIntentHandleExtension(key: K): E?

    /**
     * Конфигурация элемента меню.
     *
     * @property installationOptions опции установки в меню.
     * @property parent родительский элемент.
     * @property counter счетчик.
     * @property content доп.контент.
     * @property visibilitySource источник данных о видимости.
     * @property shouldPersistSelectState сохраняем ли мы состояние выбранности данного пункта при перезапуске приложения.
     */
    data class MenuItemConfiguration(
        val installationOptions: MenuInstallationOptions = MenuInstallationOptions.bothMenu(),
        val parent: NavigationItem? = null,
        val alignmentItem: NavigationItem? = null,
        val counter: NavigationCounter? = null,
        val iconButton: NavIconButton? = null,
        val content: NavigationItemContent? = null,
        val embeddedWidget: NavItemWidget? = null,
        val visibilitySource: LiveData<Boolean> = MutableLiveData(true),
        val shouldPersistSelectState: Boolean = true
    )

    /**
     * Опции добавления в меню.
     *
     * @property sideMenu добавляем ли элемент в боковом меню.
     * @property bottomMenu добавляем ли элемент в ННП.
     */
    class MenuInstallationOptions private constructor(
        val sideMenu: Boolean,
        val bottomMenu: Boolean
    ) {

        companion object {

            /**
             * Встраиваем во всех доступные меню.
             */
            @JvmStatic
            fun bothMenu(): MenuInstallationOptions = MenuInstallationOptions(
                sideMenu = true,
                bottomMenu = true
            )

            /**
             * Встраиваем только в боковое меню.
             */
            @JvmStatic
            fun sideMenuOnly(): MenuInstallationOptions = MenuInstallationOptions(
                sideMenu = true,
                bottomMenu = false
            )

            /**
             * Встраиваем только в ННП.
             */
            @JvmStatic
            fun bottomMenuOnly(): MenuInstallationOptions = MenuInstallationOptions(
                sideMenu = false,
                bottomMenu = true
            )
        }
    }
}