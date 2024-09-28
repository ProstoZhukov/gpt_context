package ru.tensor.sbis.main_screen_decl.env

import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.model.NavigationHeaderData
import ru.tensor.sbis.design.navigation.view.model.UNDEFINED_NAVX_IDENTIFIER
import ru.tensor.sbis.design.navigation.view.view.NavView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView
import ru.tensor.sbis.main_screen_decl.MainScreen

/**
 * Компонент бокового меню
 *
 * @property navView
 * @property drawerLayout
 *
 * @author kv.martyshenko
 */
class SideMenu(
    private val navView: NavView,
    val drawerLayout: DrawerLayout,
    val header: Header? = DefaultHeader(navView),
    val footer: Footer? = DefaultFooter(navView)
) {

    /**
     * Доступ к [NavView] имеет только [MainScreen]
     */
    @Suppress("UnusedReceiverParameter")
    fun MainScreen.getNavView(): NavView = navView

    /**
     * Интерфейс шапки бокового меню.
     */
    interface Header

    /**
     * Интерфейс "подвала" бокового меню.
     */
    interface Footer

    /**
     * Дефолтная реализация шапки бокового меню [Header]
     */
    class DefaultHeader(private val navView: NavView) : Header {

        /**
         * Значение, соответствующее идентификатору связанного пункта навигации в конфигурации, получаемой от сервиса
         * навигации. В аддоне, управляющем шапкой, должно быть установлено необходимое значение.
         */
        var navxIdentifier: NavxId? = null

        @Deprecated(
            "Будет удалено по https://online.sbis.ru/opendoc.html?guid=307c8603-08ac-44b8-a0f0-0673ef3c6293&client=3",
            replaceWith = ReplaceWith("navxIdentifier")
        )
        var navxId: String = UNDEFINED_NAVX_IDENTIFIER
            set(value) {
                field = value
                navxIdentifier = NavxId.of(value)
            }

        /**
         * Состояние выделения шапки аккордеона.
         * События, инициированные вызовом [setSelected], не меняющие состояния выделения, не публикуются
         */
        val selectionLiveData: LiveData<Boolean>
            get() = navView.headerSelectionLiveData

        @Deprecated("https://online.sbis.ru/opendoc.html?guid=ec38d634-c12d-496b-8367-27214f232ac1&client=3")
        val selectionFlow: Flow<Boolean>
            get() = navView.headerSelectionFlow

        /**
         * Настраивает стандартную шапку аккордеона.
         * Стандартная шапка может быть использована только если не задан атрибут [R.styleable.NavView_header]
         */
        @Suppress("unused")
        fun configure(
            data: NavigationHeaderData.LogoData,
            lifecycleOwner: LifecycleOwner,
            navxId: String
        ) {
            this.navxId = navxId
            navView.configureDefaultHeader(data, lifecycleOwner)
        }

        /**
         * Устанавливает состояние выделения шапки
         */
        fun setSelected(selected: Boolean) {
            navView.setHeaderSelected(selected)
        }

        /** @SelfDocumented */
        fun setEnabled(enabled: Boolean) {
            navView.header?.isEnabled = enabled
        }

        /** @SelfDocumented */
        fun setHeaderCounterVisibility(isVisible: Boolean) {
            navView.setHeaderCounterVisibility(isVisible)
        }
    }

    /**
     * Дефолтная реализация "подвала" бокового меню [Footer]
     */
    class DefaultFooter(private val navView: NavView) : Footer {

        /**
         * Значение, соответствующее идентификатору связанного пункта навигации в конфигурации, получаемой от сервиса
         * навигации. В аддоне, управляющем "подвалом", должно быть установлено необходимое значение.
         */
        var navxIdentifier: NavxId? = null

        @Deprecated(
            "Будет удалено по https://online.sbis.ru/opendoc.html?guid=307c8603-08ac-44b8-a0f0-0673ef3c6293&client=3",
            replaceWith = ReplaceWith("navxIdentifier")
        )
        var navxId: String = UNDEFINED_NAVX_IDENTIFIER
            set(value) {
                field = value
                navxIdentifier = NavxId.of(value)
            }

        /**
         * Состояние выделения "подвала" аккордеона.
         * События, инициированные вызовом [setSelected], не меняющие состояния выделения, не публикуются
         */
        val selectionLiveData: LiveData<Boolean>
            get() = navView.footerSelectionLiveData

        @Deprecated("https://online.sbis.ru/opendoc.html?guid=ec38d634-c12d-496b-8367-27214f232ac1&client=3")
        val selectionFlow: Flow<Boolean>
            get() = navView.footerSelectionFlow

        /**
         * Устанавливает состояние выделения "подвала"
         */
        fun setSelected(selected: Boolean) {
            navView.setFooterSelected(selected)
        }

        /** @SelfDocumented */
        fun setVisible(visible: Boolean) {
            navView.setFooterVisible(visible)
        }
    }
}

/**
 * Компонент ННП
 *
 * @author kv.martyshenko
 */
class BottomMenu(
    private val navView: TabNavView
) {

    /**
     * Доступ к [TabNavView] имеет только [MainScreen]
     */
    @Suppress("UnusedReceiverParameter")
    fun MainScreen.getNavView(): TabNavView = navView
}

/**
 * Счетчик на элементе меню.
 */
data class MenuCounters(
    var name: String,
    var unreadCounter: Int,
    var unviewedCounter: Int,
    var totalCounter: Int
)

/**
 * Компонент меню главного экрана.
 *
 * @property sideMenu боковое меню
 * @property bottomMenu ННП
 *
 * @author kv.martyshenko
 */
class MainScreenMenu private constructor(
    val sideMenu: SideMenu?,
    val bottomMenu: BottomMenu?,
    val counters: Flow<Map<String, MenuCounters>>? = null
) {

    companion object {

        /**
         * Метод для создания меню главного экрана.
         *
         * @param sideMenu боковое меню
         * @param bottomMenu ННП
         */
        @JvmStatic
        fun fullMenu(
            sideMenu: SideMenu,
            bottomMenu: BottomMenu,
            counters: Flow<Map<String, MenuCounters>>? = null
        ): MainScreenMenu {
            return MainScreenMenu(sideMenu, bottomMenu, counters)
        }

        /**
         * Метод для создания меню главного экрана.
         *
         * @param bottomMenu ННП
         */
        @Suppress("unused")
        @JvmStatic
        fun bottomMenu(bottomMenu: BottomMenu, counters: Flow<Map<String, MenuCounters>>? = null): MainScreenMenu {
            return MainScreenMenu(null, bottomMenu, counters)
        }

        /**
         * Метод для создания меню главного экрана.
         *
         * @param sideMenu боковое меню
         */
        @Suppress("unused")
        @JvmStatic
        fun sideMenu(sideMenu: SideMenu, counters: Flow<Map<String, MenuCounters>>? = null): MainScreenMenu {
            return MainScreenMenu(sideMenu, null, counters)
        }
    }
}