package ru.tensor.sbis.motivation_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.motivation_decl.features.common.FragmentOpenArgs
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича предоставляющая фрагменты-точки входа
 * в модуль "Мотивация"
 */
interface MotivationFragmentsProvider: Feature {

    /** Получить фрагмент  дашбоардом виджетов мотивации. */
    fun createWidgetsDashboard(args: FragmentOpenArgs = FragmentOpenArgs()): Fragment
}