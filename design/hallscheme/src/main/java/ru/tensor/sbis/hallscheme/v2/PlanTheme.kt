package ru.tensor.sbis.hallscheme.v2

/**
 * Enum, представляющий тему схемы зала.
 * ВАЖНО! При добавлении новой темы убедитесь, что значение поля stringName уникально для каждой темы.
 */
internal enum class PlanTheme(private val stringName: String) {
    /**
     * Плоская тема.
     */
    THEME_FLAT("flat"),

    /**
     * Объёмная тема.
     */
    THEME_3D("picture");

    companion object {
        /**@SelfDocumented*/
        fun getByStringName(name: String?): PlanTheme {
            return values().find { it.stringName == name } ?: THEME_FLAT
        }
    }
}