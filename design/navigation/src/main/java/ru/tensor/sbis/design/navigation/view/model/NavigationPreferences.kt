package ru.tensor.sbis.design.navigation.view.model

import android.content.Context
import ru.tensor.sbis.design.navigation.NavigationPlugin

private const val NAVIGATION_PREFERENCES = "NAVIGATION_PREFERENCES"

/**
 * Инструмент предназначен для сохранения параметров компонентов навигации между запусками приложения.
 *
 * @author us.bessonov
 */
internal class NavigationPreferences(context: Context) {

    private val preferences =
        context.getSharedPreferences(NAVIGATION_PREFERENCES, Context.MODE_PRIVATE)

    /**
     * Сохраняет состояние развёрнутости элемента с заданным идентификатором.
     */
    fun saveState(id: String, isExpanded: Boolean) {
        preferences.edit()
            .apply { putBoolean(id, isExpanded) }
            .apply()
    }

    /**
     * Развёрнут ли элемент с заданным идентификатором.
     */
    fun isExpanded(id: String) =
        preferences.getBoolean(id, NavigationPlugin.customizationOptions.areNavigationItemsExpandedByDefault)

    /**
     * Сбрасывает все сохранённые параметры.
     */
    fun clear() {
        preferences.edit()
            .apply { clear() }
            .apply()
    }
}