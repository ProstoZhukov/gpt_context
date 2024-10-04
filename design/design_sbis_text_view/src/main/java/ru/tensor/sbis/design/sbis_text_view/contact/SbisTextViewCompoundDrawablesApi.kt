package ru.tensor.sbis.design.sbis_text_view.contact

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * API компонента [SbisTextView] по присоединным [Drawable] к 4-ем сторонам текста:
 * слева, сверху, справа и снизу.
 *
 * @author vv.chekurda
 */
interface SbisTextViewCompoundDrawablesApi {

    /**
     * Устанавливает размер отступа между составными элементами drawable и текстом.
     * @see setCompoundDrawables
     */
    var compoundDrawablePadding: Int

    /**
     * Вернуть paddingStart + [compoundDrawablePadding] для левого [Drawable], если таковой имеется.
     * @see setCompoundDrawables
     */
    val compoundPaddingStart: Int

    /**
     * Вернуть paddingTop + [compoundDrawablePadding] для верхнего [Drawable], если таковой имеется.
     * @see setCompoundDrawables
     */
    val compoundPaddingTop: Int

    /**
     * Вернуть paddingEnd + [compoundDrawablePadding] для правого [Drawable], если таковой имеется.
     * @see setCompoundDrawables
     */
    val compoundPaddingEnd: Int

    /**
     * Вернуть paddingBottom + [compoundDrawablePadding] для нижнего [Drawable], если таковой имеется.
     * @see setCompoundDrawables
     */
    val compoundPaddingBottom: Int

    /**
     * Вернуть массив [Drawable].
     * @see setCompoundDrawables
     */
    val compoundDrawables: Array<Drawable?>

    /**
     * Установить признак необходимости оборачивать [compoundDrawables] вокруг однострочного текста,
     * независимо от размеров view с учетом gravity.
     *
     * Уникальное apo SbisTextView, поэтому при необходимости можно будет поддержать
     * и для многострочного, если будут такие кейсы использования.
     */
    var isWrappedCompoundDrawables: Boolean

    /**
     * Устанавливает Drawables так, чтобы они отображались слева, сверху, справа и под текстом.
     * Используйте null если вам не нужен [Drawable].
     * Вызов этого метода перезапишет все Drawables ранее установленные drawables.
     *
     * @param useIntrinsicBounds true, чтобы у всех [Drawable]
     * использовались [Drawable.getIntrinsicWidth] и [Drawable.getIntrinsicHeight] в качестве ширины и высоты.
     * В ином случае (по умолчанию) у [Drawable] уже должен быть вызван [Drawable.setBounds].
     */
    fun setCompoundDrawables(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null,
        useIntrinsicBounds: Boolean = false
    )

    /**
     * Устанавливает Drawables по индентификаторам ресурсов аналогично основному методу [setCompoundDrawables].
     */
    fun setCompoundDrawables(
        @DrawableRes start: Int? = null,
        @DrawableRes top: Int? = null,
        @DrawableRes end: Int? = null,
        @DrawableRes bottom: Int? = null,
        useIntrinsicBounds: Boolean = false
    )
}