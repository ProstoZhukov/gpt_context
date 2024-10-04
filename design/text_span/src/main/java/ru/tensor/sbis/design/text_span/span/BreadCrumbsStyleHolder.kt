package ru.tensor.sbis.design.text_span.span

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.text_span.span.util.BreadCrumbsAttributes
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Предоставляет параметры внешнего вида хлебных крошек, с учётом текущей темы.
 *
 * @see [BreadCrumbsAttributes]
 *
 * @author us.bessonov
 */
internal class BreadCrumbsStyleHolder(
    private val attributes: BreadCrumbsAttributes
) {

    /** @SelfDocumented */
    @ColorInt
    var textColor = 0

    /** @SelfDocumented */
    @ColorInt
    var highlightsColor = 0

    /** @SelfDocumented */
    @Px
    var horizontalMargin = 0

    /** @SelfDocumented */
    @Px
    var textSize = 0f

    /** @SelfDocumented */
    @Px
    var iconSize = 0f

    /**
     * Получить фактические значения из [Context] и проинициализировать ими поля.
     */
    fun loadStyle(context: Context) = with(context) {
        highlightsColor = getThemeColorInt(attributes.highlightsColor)
        textColor = getThemeColorInt(attributes.textColor)
        horizontalMargin = getDimenPx(attributes.horizontalMargin)
        textSize = getDimen(attributes.textSize)
        iconSize = getDimen(attributes.iconSize)
    }
}