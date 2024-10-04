package ru.tensor.sbis.design.buttons.base.zentheme

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.zen.ZenThemeElementsColors
import ru.tensor.sbis.design.theme.zen.ZenThemeModel

/**
 * Контроллер Дзен темизации кнопки с определённого типа. Наследник должен описывать правила темизации конкретной
 * кнопки(ссылки, круглые, обычные ...) с конкретным типом(с заливкой, без заливки ...).
 *
 * Например, для круглой кнопки с прозрачным фоном и для круглой кнопки с заливкой, потребуются два разных контроллера,
 * т.к. для них используются разные правила темизации.
 *
 * @author ra.geraskin
 */
internal abstract class ZenThemeAbstractButtonController : ButtonZenThemeSupport {

    /**
     * Предыдущий стандартный стиль кнопки. В данный момент бесполезен. Нужен для оперативной доработки, если в
     * будущем потребуется быстрый откат с Дзен стиля на предыдущий стандартный стиль.
     */
    private var oldThemeStyle: SbisButtonStyle? = null

    /** @SelfDocumented */
    protected lateinit var button: AbstractSbisButton<*, *>

    /** @SelfDocumented */
    override fun setZenTheme(themeModel: ZenThemeModel) = setZenThemeForced(themeModel, button.style)

    /** @SelfDocumented */
    override fun setZenThemeForced(themeModel: ZenThemeModel, style: SbisButtonStyle) {
        if (isThemeStyle(style)) oldThemeStyle = style
        val zenStyle = getZenButtonStyle(button.getContext(), oldThemeStyle ?: return, themeModel) ?: return
        button.setZenButtonStyle(zenStyle)
    }

    /**
     * Привязать контроллер Дзен темы к кнопке.
     */
    internal fun attach(button: AbstractSbisButton<*, *>) {
        this.button = button
    }

    /**
     * Ассоциативный список для удобного определения в потомках значений отклонений альфы при клике по кнопке, в
     * зависимости от стиля кнопки.
     */
    protected abstract val alphaMap: Map<SbisButtonStyle, OnClickAdditionAlphaValuePair>

    /**
     * Поддерживаемые темы Дзен темизацией для кнопки конкретного потомка абстрактного контролера.
     */
    protected abstract val supportedThemeStyles: List<SbisButtonStyle>

    /**
     * Получить ассоциативный список от потомка, где ключ: стиль кнопки, значение: цвет текста\иконки кнопки.
     */
    protected open fun getTextColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = emptyMap()

    /**
     * Получить ассоциативный список от потомка, где ключ: стиль кнопки, значение: цвет обводки кнопки.
     */
    protected open fun getBorderColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = emptyMap()

    /**
     * Получить ассоциативный список от потомка, где ключ: стиль кнопки, значение: цвет фона кнопки.
     */
    protected open fun getBackgroundColorMap(zenModel: ZenThemeModel): Map<SbisButtonStyle, SbisColor> = emptyMap()

    /**
     * "Собрать" новый стиль для кнопки из предоставленных цветов. Реализуется контроллером каждой кнопки в отдельности.
     *
     * @param alphaAdditionalValue величина изменения альфы цвета при клике. Цветом может быть как цвет текста, так и цвет фона, в зависимости от стиля фона и самой кнопки.
     * @param textColor основной цвет текста\иконки кнопки.
     * @param backgroundColor цвет фона кнопки.
     * @param borderColor цвет обводки кнопки.
     * @param disableColor цвет ReadOnly зен темы.
     */
    abstract fun createZenStyle(
        alphaAdditionalValue: Float?,
        textColor: Int?,
        backgroundColor: Int?,
        borderColor: Int?,
        disableColor: Int
    ): SbisButtonStyle?

    /**
     * Получить все необходимые цвета для создания Дзен стиля кнопки.
     */
    private fun getZenButtonStyle(context: Context, style: SbisButtonStyle, zenModel: ZenThemeModel): SbisButtonStyle? {
        val alphaAdditionalValue = getAlphaClickAdditionalValue(style, zenModel)
        val textColor = getColorByStyle(getTextColorMap(zenModel), style)
        val borderColor = getColorByStyle(getBorderColorMap(zenModel), style)
        val backgroundColor = getColorByStyle(getBackgroundColorMap(zenModel), style)
        val disableColor = zenModel.elementsColors.readonlyColor.getColor(context)
        return createZenStyle(alphaAdditionalValue, textColor, backgroundColor, borderColor, disableColor)
    }

    /**
     * Получить от потомка величины изменения альфы при клике по [стилю кнопки][style] и
     * [Дзен теме (светлая\тёмная)] [model].
     */
    private fun getAlphaClickAdditionalValue(style: SbisButtonStyle, model: ZenThemeModel): Float? =
        alphaMap[style]?.getAlphaBoostByTheme(model.elementsColors)

    /**
     * Проверить поддержку [стандартного стиля][style] Дзен темизацией для конкретной кнопки.
     */
    private fun isThemeStyle(style: SbisButtonStyle): Boolean = supportedThemeStyles.contains(style)

    /**
     * Получить из [ассоциативного списка][colorMap] значения цвета по [стандартному стилю кнопки][style].
     */
    @ColorInt
    private fun getColorByStyle(colorMap: Map<SbisButtonStyle, SbisColor>, style: SbisButtonStyle): Int? =
        colorMap[style]?.getColor(button.getContext())

    /**
     * Пара значений изменений альфы при клике. Используется для удобного определения величин, на которые изменяется
     * альфа для каждой конкретной кнопки, каждого стиля, для светлой и тёмной Дзен темы.
     */
    internal data class OnClickAdditionAlphaValuePair(
        @FloatRange(from = -1.0, to = 1.0) val light: Float,
        @FloatRange(from = -1.0, to = 1.0) val dark: Float
    ) {

        /**
         * @SelfDocumented
         */
        fun getAlphaBoostByTheme(theme: ZenThemeElementsColors) = when (theme) {
            ZenThemeElementsColors.LIGHT -> light
            ZenThemeElementsColors.DARK -> dark
        }
    }

}

/**
 * Добавить к argb цвету [дополнительную альфу][value]. Дополнение может быть, как положительным
 * (увеличение альфы), так и отрицательным, (уменьшение альфы).
 */
fun Int.plusAlpha(@FloatRange(from = -1.0, to = 1.0) value: Float): Int {
    val alpha = Color.alpha(this)
    val newAlpha = (alpha + value * 255).toInt()
    return Color.argb(newAlpha, Color.red(this), Color.green(this), Color.blue(this))
}

/**
 * Заменить альфу в argb цвете на [новую][newAlpha].
 */
fun Int.setNewAlpha(@FloatRange(from = .0, to = 1.0) newAlpha: Float): Int =
    ColorUtils.setAlphaComponent(this, (newAlpha * 0xFF).toInt())
