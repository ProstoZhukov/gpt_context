package ru.tensor.sbis.design.view.input.accesscode

import android.text.Editable
import android.text.Spanned
import android.text.style.ForegroundColorSpan

/**
 * Содержит состояния о цветах для декорации поля ввода кода доступа с фиксированной маской.
 *
 * @property permanentMaskColorSpan информация о цвете неизменяемой части, см. [ForegroundColorSpan].
 * @property blankMaskColorSpan информация о цвете незаполненной изменяемой части, см. [ForegroundColorSpan].
 * @property filledMaskColorSpan информация о цвете заполненной изменяемой части, см. [ForegroundColorSpan].
 *
 * @author mb.kruglova
 */
internal class AccessCodeInputViewDecorationHelper(
    private var permanentMaskColorSpan: ForegroundColorSpan,
    private var blankMaskColorSpan: ForegroundColorSpan,
    private var filledMaskColorSpan: ForegroundColorSpan
) {
    /**
     * Устанавливает первоначальную декорацию.
     *
     * @param to экземпляр [Editable], для которого нужно сделать декорацию.
     * @param position индекс конца неизменяемой части и начала изменяемой части, см. [Editable.setSpan].
     */
    fun setInitialColorSpan(to: Editable, position: Int) {
        setPermanentMaskColorSpan(to, 0, position)
        setBlankMaskColorSpan(to, position, to.length)
    }

    /**
     * Устанавливает декорацию для неизменяемой части.
     * @param to экземпляр [Editable], для которого нужно сделать декорацию.
     * @param start индекс начала, см. [Editable.setSpan].
     * @param end индекс конца, см. [Editable.setSpan].
     */
    fun setPermanentMaskColorSpan(to: Editable, start: Int, end: Int) {
        to.setSpan(permanentMaskColorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    /**
     * Устанавливает декорацию для незаполненной изменяемой части.
     * @param to экземпляр [Editable], для которого нужно сделать декорацию.
     * @param start индекс начала, см. [Editable.setSpan].
     * @param end индекс конца, см. [Editable.setSpan].
     */
    fun setBlankMaskColorSpan(to: Editable, start: Int, end: Int) {
        to.setSpan(blankMaskColorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    /**
     * Устанавливает декорацию для заполненной изменяемой части.
     * @param to экземпляр [Editable], для которого нужно сделать декорацию.
     * @param start индекс начала, см. [Editable.setSpan].
     * @param end индекс конца, см. [Editable.setSpan].
     */
    fun setFilledMaskColorSpan(to: Editable, start: Int, end: Int) {
        to.setSpan(filledMaskColorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}