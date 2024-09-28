package ru.tensor.sbis.base_app_components.settings

import ru.tensor.sbis.settings_screen.content.ContentHolder

/**
 * Модель настроек
 * @param items пункты настроек
 * @param title Текст заголовка настроек
 *
 * @author ma.kolpakov
 */
data class SettingsModel(val items: ContentHolder, val title: String?)