package ru.tensor.sbis.toolbox_decl.dashboard

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик экрана с дашбордом.
 * Содержит шапку с заголовком раздела и список виджетов.
 *
 * @author am.boldinov
 */
interface DashboardScreenProvider : Feature {

    /**
     * Возвращает [Fragment] для отображения дашборда.
     *
     * @param request
     * @param options Набор опций для кастомизации экрана.
     */
    fun getDashboardScreenFragment(
        request: DashboardRequest,
        options: DashboardScreenOptions = DashboardScreenOptions()
    ): Fragment
}