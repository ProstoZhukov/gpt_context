package ru.tensor.sbis.design.view.input.money

import android.text.Editable
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px

/**
 * Содержит состояния о цветах и размерах для декорации денег. Дополнительно решает проблему с [Editable.clearSpans],
 * который замораживает поле.
 * @property integerColorSpan информация о цвете целой части, см. [ForegroundColorSpan].
 * @property integerSizeSpan информация о размере целой части, см. [AbsoluteSizeSpan].
 * @property fractionColorSpan информация о цвете дробной части, см. [ForegroundColorSpan].
 * @property fractionSizeSpan информация о размере дробной части, см. [AbsoluteSizeSpan].
 * @property defaultSize цвет недекорированного поля.
 *
 * @author ps.smirnyh
 */
internal class MoneyInputViewDecorationHelper(
    private var integerColorSpan: ForegroundColorSpan,
    private var integerSizeSpan: AbsoluteSizeSpan,
    private var fractionColorSpan: ForegroundColorSpan,
    private var fractionSizeSpan: AbsoluteSizeSpan,
    @Px val defaultSize: Int
) {
    /**
     * Дополнительный конструктор.
     * @param integerPartColor цвет текста целой части.
     * @param integerPartSize размер текста целой части.
     * @param fractionPartColor цвет текста дробной части.
     * @param fractionPartSize размер текста дробной части.
     * @param defaultSize размер текста если не декорировано.
     */
    constructor(
        @ColorInt integerPartColor: Int,
        @Px integerPartSize: Int,
        @ColorInt fractionPartColor: Int,
        @Px fractionPartSize: Int,
        @Px defaultSize: Int
    ) : this(
        ForegroundColorSpan(integerPartColor),
        AbsoluteSizeSpan(integerPartSize),
        ForegroundColorSpan(fractionPartColor),
        AbsoluteSizeSpan(fractionPartSize),
        defaultSize
    )

    private var integerSpanRange = IntRange(0, 0)
    private var fractionSpanRange = IntRange(0, 0)

    /**
     * Возвращает размер текста для поля ввода.
     * @param isDecorated true - поле декорируется, false - нет.
     * @return размер текста.
     */
    @Px
    fun getTextSize(isDecorated: Boolean) =
        if (isDecorated) {
            integerSizeSpan.size.toFloat()
        } else {
            defaultSize.toFloat()
        }

    /**
     * Делает декорацию для целой части.
     * @param to экземпляр [Editable], для которого нужно сделать декорацию.
     * @param start идекс начала, см. [Editable.setSpan].
     * @param end идекс конца, см. [Editable.setSpan].
     */
    fun addIntegerPartSpans(to: Editable, start: Int, end: Int) {
        to.setSpan(integerColorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        to.setSpan(integerSizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        integerSpanRange = start..end
    }

    /**
     * Удаляет декорацию для целой части.
     * @param from экземпляр [Editable], для которого нужно удалить декорацию.
     */
    fun removeIntegerPartSpans(from: Editable) {
        from.removeSpan(integerColorSpan)
        from.removeSpan(integerSizeSpan)
    }

    /**
     * Делает декорацию для дробной части.
     * @param to экземпляр [Editable], для которого нужно сделать декорацию.
     * @param start индекс начала, см. [Editable.setSpan].
     * @param end индекс конца, см. [Editable.setSpan].
     */
    fun addFractionPartSpans(to: Editable, start: Int, end: Int) {
        to.setSpan(fractionColorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        to.setSpan(fractionSizeSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        fractionSpanRange = start..end
    }

    /**
     * Удаляет декорацию для дробной части.
     * @param from экземпляр [Editable], для которого нужно удалить декорацию.
     */
    fun removeFractionPartSpans(from: Editable) {
        from.removeSpan(fractionColorSpan)
        from.removeSpan(fractionSizeSpan)
    }

    /**
     * Устанавливает новый размер целой части.
     *
     * @param editable [Editable], у которого нужно установить новый декоратор размера.
     * @param size новое значение размера шрифта.
     */
    fun setIntegerPartSize(editable: Editable, @Px size: Int) {
        editable.removeSpan(integerSizeSpan)
        integerSizeSpan = AbsoluteSizeSpan(size)
        editable.setSpan(
            integerSizeSpan,
            integerSpanRange.first,
            integerSpanRange.last,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    /**
     * Устанавливает новый цвет целой части.
     *
     * @param editable [Editable], у которого нужно установить новый декоратор размера.
     * @param color новое значение цвета шрифта.
     */
    fun setIntegerPartColor(editable: Editable, @ColorInt color: Int) {
        editable.removeSpan(integerColorSpan)
        integerColorSpan = ForegroundColorSpan(color)
        editable.setSpan(
            integerColorSpan,
            integerSpanRange.first,
            integerSpanRange.last,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    /**
     * Устанавливает новый размер дробной части.
     *
     * @param editable [Editable], у которого нужно установить новый декоратор размера.
     * @param size новое значение размера шрифта.
     */
    fun setFractionPartSize(editable: Editable, @Px size: Int) {
        editable.removeSpan(fractionSizeSpan)
        fractionSizeSpan = AbsoluteSizeSpan(size)
        editable.setSpan(
            fractionSizeSpan,
            fractionSpanRange.first,
            fractionSpanRange.last,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }

    /**
     * Устанавливает новый цвет дробной части.
     *
     * @param editable [Editable], у которого нужно установить новый декоратор размера.
     * @param color новое значение цвета шрифта.
     */
    fun setFractionPartColor(editable: Editable, @ColorInt color: Int) {
        editable.removeSpan(fractionColorSpan)
        fractionColorSpan = ForegroundColorSpan(color)
        editable.setSpan(
            fractionColorSpan,
            fractionSpanRange.first,
            fractionSpanRange.last,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}