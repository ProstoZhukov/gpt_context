package ru.tensor.sbis.person_decl.motivation.ui

/**
 * Конфигурация вкладок мотивации для навигации при открытии хост фрагмента из внешних модулей
 */
enum class MotivationStartTabs(val id: Int) {
    /** Вкладка Зарплата */
    SALARY_TAB(0),

    /** Вкладка Выплаты */
    PAYOUTS_TAB(1),

    /** Вкладка Стимулы */
    MOTIVATIONS_TAB(2);

    companion object {
        /** @return возвращает MotivationStartTabs по id */
        fun getTabById(id: Int): MotivationStartTabs? {
            return values().findLast { it.id == id }
        }
    }
}