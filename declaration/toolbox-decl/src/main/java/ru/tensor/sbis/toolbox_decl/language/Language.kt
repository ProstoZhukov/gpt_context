package ru.tensor.sbis.toolbox_decl.language


/**
 * @author av.krymov
 *
 * Модель языка.
 * @property name - название например "Русский"
 * @property code - код языка iso639-1 например "ru"
 * @property flagEmoji - код символа флага страны
 * @property isCurrent - текущий ли выбранный
 * @property isEnabled - доступен ли для смены
 */
data class Language(
    val name: String,
    val code: String,
    val flagEmoji: String,
    val isCurrent: Boolean,
    val isEnabled: Boolean
)