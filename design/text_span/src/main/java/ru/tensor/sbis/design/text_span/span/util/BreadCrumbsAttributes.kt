package ru.tensor.sbis.design.text_span.span.util

import androidx.annotation.AttrRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.text_span.span.BreadCrumbsSpan

/**
 * Набор атрибутов, описывающих вид хлебных крошек.
 *
 * @param textColor цвет текста хлебных крошек
 * @param highlightsColor цвет выделения текста
 * @param horizontalMargin отступ между крошками
 * @param textSize размер текста крошек
 * @param iconSize размер иконок в крошках
 *
 * @see [BreadCrumbsSpan]
 *
 * @author us.bessonov
 */
class BreadCrumbsAttributes(
    @AttrRes internal val textColor: Int = R.attr.secondaryTextColor,
    @AttrRes internal val highlightsColor: Int = R.attr.textBackgroundColorDecoratorHighlight,
    @AttrRes internal val horizontalMargin: Int = R.attr.offset_2xs,
    @AttrRes internal val textSize: Int = R.attr.fontSize_xs_scaleOff,
    @AttrRes internal val iconSize: Int = R.attr.iconSize_2xs
)