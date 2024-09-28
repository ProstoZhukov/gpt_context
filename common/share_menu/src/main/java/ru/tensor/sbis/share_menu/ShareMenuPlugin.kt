package ru.tensor.sbis.share_menu

import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.share_menu.contract.ShareMenuDependency
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * Плагин меню приложения для функциональности "поделиться.
 *
 * @author vv.chekurda
 */
object ShareMenuPlugin : BasePlugin<ShareMenuPlugin.CustomizationOptions>() {

    class CustomizationOptions internal constructor() {

        /**
         * Разрешена ли проверка прав
         */
        var isPermissionCheckEnabled: Boolean = true
    }

    private lateinit var navigationServiceProvider: FeatureProvider<NavigationService>
    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private var shareHandlerProviders: Set<FeatureProvider<ShareHandler>>? = null
    private var permissionFeatureProvider: FeatureProvider<PermissionFeature>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null

    internal var isInitialized: Boolean = false

    internal val menuDependency: ShareMenuDependency by lazy {
        object : ShareMenuDependency,
            LoginInterface.Provider by loginInterfaceProvider.get() {

            override val shareHandlers: List<ShareHandler>
                get() = shareHandlerProviders?.map { it.get() }.orEmpty()

            override val permissionFeature: PermissionFeature?
                get() = permissionFeatureProvider?.get()

            override val navigationService: NavigationService
                get() = navigationServiceProvider.get()

            override val analyticsUtil: AnalyticsUtil?
                get() = analyticsUtilFeatureProvider?.get()?.getAnalyticsUtil()
        }
    }

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
            .require(NavigationService::class.java) { navigationServiceProvider = it }
            .require(customizationOptions.isPermissionCheckEnabled, PermissionFeature::class.java) {
                permissionFeatureProvider = it
            }
            .optionalSet(ShareHandler::class.java) { shareHandlerProviders = it }
            .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
            .build()
    }

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()
    override val customizationOptions = CustomizationOptions()

    override fun initialize() {
        super.initialize()
        isInitialized = true
    }

    private fun <F : Feature> Dependency.Builder.require(
        condition: Boolean,
        type: Class<F>,
        inject: (FeatureProvider<F>?) -> Unit,
    ): Dependency.Builder = apply {
        if (condition) optional(type, inject)
    }
}