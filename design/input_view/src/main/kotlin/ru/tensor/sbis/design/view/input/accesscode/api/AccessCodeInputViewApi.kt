package ru.tensor.sbis.design.view.input.accesscode.api

import ru.tensor.sbis.design.view.input.accesscode.AccessCodeInputView

/**
 * Api поля ввода кода доступа с фиксированной маской [AccessCodeInputView].
 *
 * @author mb.kruglova
 */
interface AccessCodeInputViewApi {

    /**
     * Слушатель, вызываемый по достижению максимальной длины ввода текста.
     */
    var maxLengthReachedListener: ((String) -> Unit)?
}