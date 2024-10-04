package ru.tensor.sbis.design_notification.popup.state_machine.util

/**
 * Продолжительность отображения компонента "Панель-информер"
 *
 * @author as.mozgolin
 */
sealed interface DisplayDuration {
    /**
     * Не скрывать панель-информер
     */
    object Indefinite: DisplayDuration

    /**
     * По умолчанию
     */
    object Default: DisplayDuration
}