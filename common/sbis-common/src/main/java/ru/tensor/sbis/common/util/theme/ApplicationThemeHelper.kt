package ru.tensor.sbis.common.util.theme

interface ApplicationThemeHelper {

    var currentTheme: Int

    val currentAuthTheme: Int

    val currentCatalogTheme: Int
        get() = -1
}