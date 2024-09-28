package ru.tensor.sbis.main_screen.widget

import androidx.lifecycle.Observer
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Хранит пункт навигации главного экрана со вспомогательными служебными данными.
 *
 * @author us.bessonov
 */
internal class MenuItemRecord(
    val item: NavigationItem,
    private val configuration: ConfigurableMainScreen.MenuItemConfiguration,
    val controller: ContentController?
) {

    /**
     * @see [NavigationItem.persistentUniqueIdentifier]
     */
    val persistentUniqueIdentifier: String
        get() = item.persistentUniqueIdentifier

    /**
     * @see [NavigationItem.navxId]
     */
    val navxIdentifier: NavxIdDecl?
        get() = item.navxId

    /**
     * Сохраняется ли состояние выбора данного пункта при перезапуске приложения.
     */
    val shouldPersistSelectState: Boolean
        get() = configuration.shouldPersistSelectState

    /**
     * Видимость пункта в данный момент.
     */
    val isVisible: Boolean
        get() = configuration.visibilitySource.value ?: false

    /**
     * Было ли получено значение видимости пункта от источника.
     */
    val hasReceivedVisibilityValue: Boolean
        get() = configuration.visibilitySource.value != null

    /**
     * Добавлен ли элемент в аккордеоне.
     */
    val isInstalledInSideMenu: Boolean
        get() = configuration.installationOptions.sideMenu

    /**
     * Добавлен ли элемент в ННП.
     */
    val isInstalledInBottomMenu: Boolean
        get() = configuration.installationOptions.bottomMenu

    /**
     * Подписаться на обновления видимости пункта.
     */
    fun observeVisibility(onChanged: (Boolean) -> Unit): Observer<Boolean> {
        val observer = Observer(onChanged)
        configuration.visibilitySource.observeForever(observer)
        return observer
    }

    /**
     * Сбрасывает подписчиков видимости пункта.
     */
    fun removeVisibilityObserver(observer: Observer<Boolean>) {
        configuration.visibilitySource.removeObserver(observer)
    }
}