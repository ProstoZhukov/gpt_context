package ru.tensor.sbis.main_screen.widget.util

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.view.NavigationView
import ru.tensor.sbis.main_screen.widget.MainScreenPlugin
import ru.tensor.sbis.main_screen.widget.MainScreenWidget
import ru.tensor.sbis.main_screen.widget.MenuConfigurator
import ru.tensor.sbis.main_screen.widget.MenuItemRecord
import ru.tensor.sbis.main_screen.widget.NavigationItemsManager
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.env.SideMenu

/**
 * Метод для автоматиского управления виджетом главного экрана на основе жизненного цикла компонента.
 *
 * @param lifecycleOwner
 * @param mainFabInContainer находится ли главная плавающая кнопка в общем контейнере кнопок
 *
 * @author kv.martyshenko
 */
fun MainScreenWidget.manageBy(lifecycleOwner: LifecycleOwner, mainFabInContainer: Boolean = false) {
    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            this@manageBy.setup(mainFabInContainer)
        }

        override fun onStart(owner: LifecycleOwner) {
            this@manageBy.activate()
        }

        override fun onResume(owner: LifecycleOwner) {
            this@manageBy.resume()
        }

        override fun onPause(owner: LifecycleOwner) {
            this@manageBy.pause()
        }

        override fun onStop(owner: LifecycleOwner) {
            this@manageBy.deactivate()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            this@manageBy.reset()
            owner.lifecycle.removeObserver(this)
        }
    })
}

/**
 * Инструмент для поддержки использования сервиса навигации в приложениях, где не интегрирован компонент главного
 * экрана.
 *
 * @param items Список пунктов навигации, предусмотренных приложением.
 * @param sideNavView Аккордеон.
 * @param bottomNavView ННП.
 * @param navAdapter Адаптер навигации.
 * @param scope Скоуп для операций с сервисом навигации.
 * @param getSelectedItem Должна возвращать текущий выбранный элемент.
 * @param getInstallationOptions Должна возвращать опции размещения пункта в компонентах навигации.
 * @param getVisibilitySource Должна возвращать подписку на обновление видимости пункта.
 * @param header Шапка аккордеона.
 * @param footer "Подвал" аккордеона.
 */
fun <ITEM : NavigationItem> setupWithNavigationService(
    items: List<ITEM>,
    sideNavView: NavigationView?,
    bottomNavView: NavigationView?,
    navAdapter: NavAdapter<ITEM>,
    scope: CoroutineScope,
    lifecycleOwner: LifecycleOwner? = null,
    getSelectedItem: () -> ITEM?,
    getInstallationOptions: (ITEM) -> ConfigurableMainScreen.MenuInstallationOptions = {
        ConfigurableMainScreen.MenuInstallationOptions.bothMenu()
    },
    getVisibilitySource: (ITEM) -> LiveData<Boolean> = { MutableLiveData(true) },
    header: SideMenu.DefaultHeader? = null,
    footer: SideMenu.DefaultFooter? = null
) {
    val records = items.associate {
        it.persistentUniqueIdentifier to MenuItemRecord(
            it,
            ConfigurableMainScreen.MenuItemConfiguration(
                getInstallationOptions(it),
                visibilitySource = getVisibilitySource(it)
            ),
            null
        )
    }
    NavigationItemsManager(
        sideNavView,
        bottomNavView,
        header,
        footer,
        MenuConfigurator(records),
        toolbarTabsController = MainScreenPlugin.tabsVisibilityController?.get(),
        onItemHidden = { item, visibleIds ->
            if (getSelectedItem()?.persistentUniqueIdentifier == item.persistentUniqueIdentifier) {
                items.find { visibleIds.contains(it.persistentUniqueIdentifier) }
                    ?.let { navAdapter.setSelected(it) }
            }
        },
        onTabSelected = { },
        onPageDataAvailable = { _, _ -> },
        ensureItemsOrdered = { },
        topNavigationTitleUpdateManager = TopNavigationTitleUpdateManager()
    ).apply {
        init(scope)
        records.values.forEach { onItemAdded(it) }
    }
}

/**
 * Метод для получения [SavedStateHandle]
 *
 * @param defaultArgs аргументы по умолчанию.
 *
 * @author kv.martyshenko
 */
@Suppress("UNCHECKED_CAST")
internal fun MainScreenHost.getSavedStateHandle(defaultArgs: Bundle?): SavedStateHandle {
    return ViewModelProvider(
        viewModelStoreOwner,
        object : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {

            override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                return if (modelClass.isAssignableFrom(SavedStateHolder::class.java)) {
                    SavedStateHolder(handle) as T
                } else {
                    throw IllegalStateException("Unknown $modelClass")
                }
            }

        })[SavedStateHolder::class.java].state
}

/**
 * Фейковая реализация [ViewModel] для получения [SavedStateHandle].
 *
 * @author kv.martyshenko
 */
internal class SavedStateHolder(val state: SavedStateHandle) : ViewModel()

internal val NOT_FOUND_ICON_STUB = R.string.design_mobile_icon_cross_black