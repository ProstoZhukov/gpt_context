package ru.tensor.sbis.navigation_service

import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.desktop.navigation.generated.NavigationFilterNative
import ru.tensor.sbis.navigation_mobile.NavigationFilterMobile
import ru.tensor.sbis.toolbox_decl.navigation.AvailableAppNavigationFilterInitializer

/**
 * Сконфигурировать набор доступных в приложении разделов.
 */
fun getNavigationFilterInitializer(itemIds: List<NavxId>) = object : AvailableAppNavigationFilterInitializer {
    override fun init() {
        val ids = ArrayList(itemIds.flatMap { it.ids })
        NavigationFilterMobile.setFilter(object : NavigationFilterNative() {
            override fun getFilter(): ArrayList<String> = ids
        })
    }
}