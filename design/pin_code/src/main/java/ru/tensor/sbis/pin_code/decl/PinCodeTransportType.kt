package ru.tensor.sbis.pin_code.decl

import java.io.Serializable

/**
 * Тип доставки кода
 * @param countDownTime время в секундах для таймера обратного отсчета до предоставления возможности повторной отправки кода. Если 0 то счетчик не отобразится.
 *
 * @author mb.kruglova
 */
sealed class PinCodeTransportType(internal val countDownTime: Long) : Serializable {

    /**
     * По смс. Таймер на 5 минут.
     */
    object SMS : PinCodeTransportType(300L)

    /**
     * По звонку. Таймер на 1 минуту.
     */
    object CALL : PinCodeTransportType(60L)

    /**
     * По почте. Таймер на 1 минуту.
     */
    object EMAIL : PinCodeTransportType(60L)

    /**
     * Отсутсвует. Таймер скрыт.
     */
    object NONE : PinCodeTransportType(0)

    /**
     * Настраиваемый таймер.
     */
    class CUSTOM(timerTime: Long) : PinCodeTransportType(timerTime)

}