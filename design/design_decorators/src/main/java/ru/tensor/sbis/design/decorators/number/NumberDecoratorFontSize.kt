package ru.tensor.sbis.design.decorators.number

import android.content.Context
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.res.SbisDimen

/**
 * Размер текста.
 *
 * @author ps.smirnyh
 */
sealed interface NumberDecoratorFontSize {

    /** Стандартная линейка размеров текста. */
    enum class Defaults(
        internal val integerPartSize: FontSize,
        internal val fractionPartSize: FontSize
    ) : NumberDecoratorFontSize {
        XS(FontSize.XS, FontSize.XS),
        S(FontSize.S, FontSize.XS),
        M(FontSize.M, FontSize.XS),
        L(FontSize.L, FontSize.XS),
        XL(FontSize.XL, FontSize.XS),
        X2L(FontSize.X2L, FontSize.XS),
        X3L(FontSize.X3L, FontSize.L),
        X4L(FontSize.X4L, FontSize.L),
        X5L(FontSize.X5L, FontSize.L),
        X6L(FontSize.X6L, FontSize.X3L),
        X7L(FontSize.X7L, FontSize.X3L),
        X8L(FontSize.X8L, FontSize.X3L);

        override fun getIntegerPartSizePx(context: Context): Int {
            return integerPartSize.getScaleOnDimenPx(context)
        }

        override fun getFractionPartSizePx(context: Context): Int {
            return fractionPartSize.getScaleOnDimenPx(context)
        }
    }

    /** Кастомный размер, если значение не входит в стандартную линейку. */
    class Custom(
        internal val integerPartSize: SbisDimen,
        internal val fractionPartSize: SbisDimen
    ) : NumberDecoratorFontSize {
        override fun getIntegerPartSizePx(context: Context): Int {
            return integerPartSize.getDimenPx(context)
        }

        override fun getFractionPartSizePx(context: Context): Int {
            return fractionPartSize.getDimenPx(context)
        }
    }

    /** Получить значение размера целой части числа. */
    fun getIntegerPartSizePx(context: Context): Int

    /** Получить значение размера дробной части числа. */
    fun getFractionPartSizePx(context: Context): Int
}