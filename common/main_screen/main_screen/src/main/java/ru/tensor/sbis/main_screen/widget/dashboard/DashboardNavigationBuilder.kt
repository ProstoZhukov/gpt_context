package ru.tensor.sbis.main_screen.widget.dashboard

import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.util.mapTextToIconRes
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.main_screen.widget.MainScreenWidget
import ru.tensor.sbis.main_screen.widget.MenuConfigurator
import ru.tensor.sbis.main_screen.widget.util.NOT_FOUND_ICON_STUB
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.content.DashboardContentController
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import timber.log.Timber

/**
 * Добавляет в навигацию приложения экраны с дашбордами, согласно данным от сервиса навигации.
 *
 * @author us.bessonov
 */
internal class DashboardNavigationBuilder(
    private val menuConfigurator: MenuConfigurator,
    private val mainScreen: MainScreenWidget? = null
) {

    private val dashboardConfigProviderById = mutableMapOf<NavxIdDecl, DashboardContentController>()

    /**
     * Добавляет ранее отсутствующие разделы с дашбордами.
     */
    fun addDashboardPages(items: List<NavigationServiceItem>) {
        // Добавляем пункты навигации для всех страниц с дашбордами, для которых их ещё не добавили.
        items.filter { it.pageData?.frameId != null && !menuConfigurator.hasItem(it.itemId) }
            .forEach {
                val item = it.makeNavigationItem()
                val dashboardController = it.navxId?.let(dashboardConfigProviderById::get)
                    ?: DefaultDashboardContentController(it.navxId!!)
                val record = menuConfigurator.addItem(
                    item,
                    ConfigurableMainScreen.MenuItemConfiguration(
                        visibilitySource = dashboardController.getVisibilitySource()
                    ),
                    dashboardController
                )
                dashboardController.dashboardConfig.menuItem = item
                mainScreen?.onItemAdded(record)
            }
    }

    /** @SelfDocumented */
    fun registerDashboardContentController(navxId: NavxIdDecl, controller: DashboardContentController) {
        dashboardConfigProviderById[navxId] = controller
    }

    private fun NavigationServiceItem.makeNavigationItem() = DefaultNavigationItem(
        NavigationItemLabel(
            default = PlatformSbisString.Value(title.orEmpty()),
            short = PlatformSbisString.Value(shortTitle.orEmpty())
        ),
        getIcon(),
        itemId,
        navxIdentifier = itemId,
        ordinal = navxId?.ordinal ?: itemId.hashCode()
    )

    private fun DashboardContentController?.getVisibilitySource(): LiveData<Boolean> {
        val default = MutableLiveData(true)
        if (this == null || mainScreen == null) {
            return default
        }
        return dashboardConfig.visibilitySourceProvider.invoke(mainScreen)
    }

    private fun NavigationServiceItem.getIcon(): NavigationItemIcon {
        val icon = mapTextToIconRes(this.icon) ?: getStubIconAndReport(itemId)
        return NavigationItemIcon(icon?.default ?: ResourcesCompat.ID_NULL, icon?.selected ?: ResourcesCompat.ID_NULL)
    }

    private fun getStubIconAndReport(id: String) = if (AppConfig.isDebug()) {
        ControllerNavIcon(NOT_FOUND_ICON_STUB, NOT_FOUND_ICON_STUB)
    } else {
        null
    }.also { Timber.w("Cannot determine icon for item with id $id") }
}