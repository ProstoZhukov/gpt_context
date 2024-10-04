package ru.tensor.sbis.design.tabs.tabItem

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.tabs.api.SbisTabsStyle
import ru.tensor.sbis.design.tabs.util.SbisTabInternalStyle
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.theme.global_variables.TextColor

/**
 * Держатель ресурсов вкладки.
 *
 * @author da.zolotarev
 */
internal class SbisTabItemStyleHolder private constructor(
    /** Цвет текста. */
    var textColor: ColorStateList = ColorStateList(STATES, intArrayOf(Color.MAGENTA, Color.MAGENTA)),
    /** Цвет иконки. */
    @ColorInt
    val iconColor: Int = Color.MAGENTA,
    /** Цвет доп. текста. */
    @ColorInt
    val additionalTextColor: Int = Color.MAGENTA,
    /** Шрифт текста. */
    var textFont: Typeface? = null,
    /** Шрифт  доп. текста. */
    var additionalTextFont: Typeface?,
    /** Шрифт иконки. */
    val iconFont: Typeface,
    /** Размер текста. */
    var textSize: Float = 0f,
    /** Размер иконки. */
    val iconSize: Float = 0f,
    /** Размер доп. текста. */
    var additionalTextSize: Float = 0f,
    /** Размер изображения */
    val imageSize: Int = 0,
    /** Отсуп между элементами вкладки. */
    val itemContentOffset: Int = 0,
    /** Маленький отсуп между элементами вкладки. */
    val itemContentOffsetSmall: Int = 0,
    /** Паддинги вкладки. */
    val horizontalPadding: Int = 0,
    /** Отступ SbisCounter от верхней границы вкладки */
    val iconCounterTopPadding: Int = 0,
    /** Смещение счетчика по горизонтали относительно центра текста, если в нем больше 2 цифр */
    val iconCounterStartPadding: Int = 0,
    /** Размер текстового счетчика. */
    val counterSize: Float
) {

    /**
     * Установить стиль [SbisTabItemStyleHolder].
     */
    fun setStyle(context: Context, style: SbisTabInternalStyle, customStyle: SbisTabsStyle) {
        textColor = style.getTextColorStateList(
            context,
            customStyle.customSelectedTitleColor,
            customStyle.customUnselectedTitleColor
        )
        textSize = customStyle.customTitleFontSize?.getDimen(context) ?: style.getTextSize(context)
        additionalTextSize = style.getTextSize(context)

        textFont = if (customStyle.useMediumTitleFontStyle) {
            TypefaceManager.getRobotoMediumFont(context)
        } else {
            if (isMainStyleHolder(context)) {
                TypefaceManager.getRobotoBoldFont(context)
            } else {
                TypefaceManager.getRobotoRegularFont(context)
            }
        }
    }

    /** @SelfDocumented */
    private fun isMainStyleHolder(context: Context): Boolean = FontSize.X5L.getScaleOffDimen(context) == counterSize

    companion object Factory {
        val STATES = arrayOf(
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_selected)
        )

        /** @SelfDocumented */
        fun create(context: Context, isOldToolbarDesign: Boolean = false): SbisTabItemStyleHolder {
            return SbisTabItemStyleHolder(
                textColor = ColorStateList(
                    STATES,
                    if (isOldToolbarDesign) {
                        intArrayOf(
                            TextColor.DEFAULT.getValue(context),
                            TextColor.DEFAULT.getValue(context)
                        )
                    } else {
                        intArrayOf(
                            StyleColor.SECONDARY.getTextColor(context),
                            StyleColor.PRIMARY.getTextColor(context)
                        )
                    }
                ),
                iconColor = TextColor.DEFAULT.getValue(context),
                additionalTextColor = TextColor.DEFAULT.getValue(context),
                textFont = TypefaceManager.getRobotoRegularFont(context),
                additionalTextFont = TypefaceManager.getRobotoRegularFont(context),
                iconFont = TypefaceManager.getSbisMobileIconTypeface(context),
                textSize = FontSize.L.getScaleOffDimen(context),
                iconSize = IconSize.M.getDimen(context),
                additionalTextSize = FontSize.L.getScaleOffDimen(context),
                imageSize = IconSize.XS.getDimenPx(context),
                itemContentOffset = Offset.XS.getDimenPx(context),
                itemContentOffsetSmall = Offset.X3S.getDimenPx(context),
                horizontalPadding = Offset.M.getDimenPx(context),
                iconCounterTopPadding = Offset.X2S.getDimenPx(context),
                iconCounterStartPadding = Offset.X2S.getDimenPx(context),
                counterSize = FontSize.L.getScaleOffDimen(context)
            )
        }

        /** @SelfDocumented */
        fun createMainTabStyleHolder(
            context: Context
        ): SbisTabItemStyleHolder {
            return SbisTabItemStyleHolder(
                textColor = ColorStateList(
                    STATES,
                    intArrayOf(TextColor.DEFAULT.getValue(context), TextColor.DEFAULT.getValue(context))
                ),
                textFont = TypefaceManager.getRobotoBoldFont(context),
                additionalTextFont = TypefaceManager.getRobotoBoldFont(context),
                iconFont = TypefaceManager.getSbisMobileIconTypeface(context),
                textSize = FontSize.X5L.getScaleOffDimen(context),
                itemContentOffset = Offset.XS.getDimenPx(context),
                itemContentOffsetSmall = Offset.X3S.getDimenPx(context),
                horizontalPadding = Offset.M.getDimenPx(context),
                iconCounterTopPadding = Offset.X2S.getDimenPx(context),
                iconCounterStartPadding = Offset.X2S.getDimenPx(context),
                counterSize = FontSize.X5L.getScaleOffDimen(context)
            )
        }
    }
}