package ru.tensor.sbis.base_app_components.navx

import android.content.Context
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.toolbox_decl.navigation.AvailableAppNavigationFilterInitializer

internal object NavxInitializerFactory {

    fun create(
        context: Context,
        elements: List<NavxId>?
    ): AvailableAppNavigationFilterInitializer? = null

}