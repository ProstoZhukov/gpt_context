package ru.tensor.sbis.viper.ui.utils

import android.content.res.Resources
import ru.tensor.sbis.design.custom_view_tools.utils.dp

/**
 * Статичная функция расчета паддинга
 */
object PaddingForContentWithFab {

    /**
     * Получить размер отступа == размеру fab
     * @param resources - для доступа к ресурсам приложения
     */
    fun getContentViewBottomPaddingWithFab(resources: Resources): Int = getPadding(resources, 48)

    /**
     * Получить размер отступа == размеру fab
     * @param resources - для доступа к ресурсам приложения
     */
    fun getContentViewBottomPaddingWithCircleFab(resources: Resources): Int = getPadding(resources, 56)

    private fun getPadding(resources: Resources, fabSize: Int): Int {
        val fabPaddingTop = 12
        val fabPaddingBottom = 12

        return resources.dp(fabSize + fabPaddingTop + fabPaddingBottom)
    }
}