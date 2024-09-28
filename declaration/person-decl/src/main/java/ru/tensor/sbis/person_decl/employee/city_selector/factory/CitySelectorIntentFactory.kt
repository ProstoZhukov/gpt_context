package ru.tensor.sbis.person_decl.employee.city_selector.factory

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания [Intent] селектора города.
 *
 * @author ra.temnikov
 */
interface CitySelectorIntentFactory : Feature {

    /**
     * Создать [Intent] селектора города
     *
     * @param context     контекст.
     * @param currentCity строковый литерал текущего выбранного города (опционально).
     */
    fun createCitySelectorIntent(context: Context, currentCity: String? = null): Intent
}

/**
 * Ключ для получения строкового литерала выбранного города из extras intent.
 */
const val CURRENT_CITY_NAME_EXTRA_KEY: String = "CITY_SELECTOR_CURRENT_CITY_EXTRA_NAME_KEY"