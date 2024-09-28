package ru.tensor.sbis.design.chips.item

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.chips.R
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Класс содержащий ресурсы для [SbisChipsItemView].
 *
 * @author da.zolotarev
 */
internal data class SbisChipsItemStyleHolder(

    /** Цвет фона при нажатии. */
    var clickedColor: Int = Color.MAGENTA,

    /** Максимальная ширина. */
    var maxWidth: Int = Int.MAX_VALUE,

    /** Отступ иконки слева. */
    var iconStartMargin: Int = 0,

    /** Отступ счетчика слева. */
    var counterStartMargin: Int = 0,

    /** Цвет текста. */
    var titleColor: Int = Color.MAGENTA,

    /** Цвет иконки. */
    var iconColor: Int = Color.MAGENTA,

    /** Тень. */
    var elevation: Float = 0f,

    /** Внутренние горизонтальные отступы компонента. */
    var horizontalPadding: Int = 0
) {

    companion object Factory {
        /**
         * Фабричный метод [SbisChipsItemStyleHolder], заполяет поля на основе темы
         */
        fun create(context: Context, attributeSet: AttributeSet?): SbisChipsItemStyleHolder {
            return SbisChipsItemStyleHolder().apply {
                elevation = context.resources.getDimension(R.dimen.design_chips_elevation)

                context.withStyledAttributes(
                    attributeSet,
                    R.styleable.SbisChipsItemView,
                    R.attr.sbisChipsItemViewTheme,
                    R.style.SbisChipsItemViewStyle
                ) {
                    maxWidth = getDimensionPixelSize(R.styleable.SbisChipsItemView_android_maxWidth, maxWidth)

                    horizontalPadding = Offset.M.getDimenPx(context)

                    iconStartMargin = Offset.X2S.getDimenPx(context)
                    counterStartMargin = Offset.XS.getDimenPx(context)

                    titleColor = getColor(R.styleable.SbisChipsItemView_SbisChipsItemView_titleColor, titleColor)
                    iconColor = getColor(R.styleable.SbisChipsItemView_SbisChipsItemView_iconColor, iconColor)

                    clickedColor = getColor(R.styleable.SbisChipsItemView_SbisChipsItemView_clickedColor, clickedColor)
                }
            }
        }
    }
}
