package ru.tensor.sbis.design.person_suggest.suggest.contract

import ru.tensor.sbis.common.util.AdjustResizeHelper

/**
 * Интерфейс поведения горизонтального саггеста персон, которое отвечает за взаимодействия с клавиатурой.
 *
 * Для организации стандартной реакции компонента на события клавиатуры, необходимо сделать 2 вещи:
 * 1) Делегировать события клавиатуры по контракту [AdjustResizeHelper.KeyboardEventListener].
 * 2) Регулировать необходимость показа компонента при подъеме клавиатуры флагом [showOnKeyboard].
 *
 * Если поведение компонента должно отличаться от стандартного,
 * то вы можете самостоятельно управлять его видимостью и позицией без использования данного контракта.
 *
 * @author vv.chekurda
 */
interface PersonSuggestKeyboardBehavior : AdjustResizeHelper.KeyboardEventListener {

    /**
     * Признак необходимости показывать саггест при подъеме клавиатуры.
     */
    var showOnKeyboard: Boolean

    /**
     * Признак необходимости перемещать компонент вслед за клавиатурой.
     */
    var translateOnKeyboard: Boolean
}