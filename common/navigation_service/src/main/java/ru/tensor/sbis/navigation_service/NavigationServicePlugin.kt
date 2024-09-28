package ru.tensor.sbis.navigation_service

import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин модуля сервиса доступных для пользователя разделов навигации приложения.
 *
 * @author us.bessonov
 */
object NavigationServicePlugin : BasePlugin<Unit>() {

    private val navigationService by lazy {
        NavigationServiceImpl()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(NavigationService::class.java) { navigationService }
    )

    override val dependency: Dependency = Dependency.EMPTY

    override val customizationOptions = Unit
}