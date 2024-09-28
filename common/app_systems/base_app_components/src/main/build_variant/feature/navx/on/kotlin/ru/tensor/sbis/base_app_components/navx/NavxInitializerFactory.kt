package ru.tensor.sbis.base_app_components.navx

import android.content.Context
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.navigation_service.getNavigationFilterInitializer
import ru.tensor.sbis.toolbox_decl.navigation.AvailableAppNavigationFilterInitializer

internal object NavxInitializerFactory {

    fun create(
        context: Context,
        elements: List<NavxId>?
    ): AvailableAppNavigationFilterInitializer? {
        return elements
            .takeIf { isNavigationFilterFeatureEnabled(context) }
            ?.let(::getNavigationFilterInitializer)
    }

    private fun isNavigationFilterFeatureEnabled(context: Context) =
        LocalFeatureToggleService(context).isFeatureActivated(FeatureSet.NAVIGATION_FILTER)

}