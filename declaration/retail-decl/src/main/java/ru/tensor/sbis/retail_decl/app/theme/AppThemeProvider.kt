package ru.tensor.sbis.retail_decl.app.theme

import ru.tensor.sbis.plugin_struct.feature.Feature

/** Провайдер активной темы розницы. */
fun interface AppThemeProvider : Feature {

    /** @SelfDocumented */
    fun getAppType(): AppThemeType
}