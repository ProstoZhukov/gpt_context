package ru.tensor.sbis.our_organisations.feature

/**
 * Контракт обработчика действий для необязательного выбора организации из списка.
 *
 * @author aa.mezencev
 */
interface OurOrgUnnecessaryChoiceHostContract {
    /**
     * Контракт обработчика действий.
     *
     * @author aa.mezencev
     */
    interface ActionHandler {
        /**
         * Сбросить выбор организации.
         */
        fun onReset()

        /**
         * Подтвердить выбор организации.
         */
        fun onApply()
    }
}