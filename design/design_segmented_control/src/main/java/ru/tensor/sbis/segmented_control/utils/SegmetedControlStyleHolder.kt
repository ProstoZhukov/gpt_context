package ru.tensor.sbis.segmented_control.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.theme.global_variables.Elevation
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.loadEnum
import ru.tensor.sbis.segmented_control.R
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlDistribution
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlSize
import ru.tensor.sbis.segmented_control.control.models.SbisSegmentedControlStyle

/**
 * Класс для управления считыванием стилей из XML.
 *
 * @author ps.smirnyh
 */
internal class SegmentedControlStyleHolder {

    /** Ширина обводки. */
    @Px
    internal var borderWidth = 0

    /** Радиус скругления. */
    @Px
    internal var cornerRadius = 0

    /** Значение для тени. */
    @Px
    internal var elevation = 0

    /** Контрастный режим. */
    internal var contrast = false

    /** Размер компонента. */
    internal var size = SbisSegmentedControlSize.S

    /** Расположение элементов внутри компонента. */
    internal var distribution = SbisSegmentedControlDistribution.EQUAL

    /** Наличие тени. */
    internal var hasShadow = false

    /** Индекс выбранного элемента. */
    internal var selectedSegmentIndex = 0

    /** Цвет фона компонента. */
    @ColorInt
    internal var backgroundColor = 0

    /** Цвет контрастного фона компонента. */
    @ColorInt
    internal var backgroundColorContrast = 0

    /** Цвет фона выбранного элемента. */
    @ColorInt
    internal var itemBackgroundColor = 0

    /** Цвет контрастного фона выбранного элемента. */
    @ColorInt
    internal var itemBackgroundColorContrast = 0

    /** Цвет текста элемента. */
    @ColorInt
    internal var itemTextColor = 0

    /** Цвет контрастного текста элемента. */
    @ColorInt
    internal var itemTextColorContrast = 0

    /** Цвет текста выбранного элемента. */
    @ColorInt
    internal var itemTextColorSelected = 0

    /** Цвет контрастного текста выбранного элемента. */
    @ColorInt
    internal var itemTextColorContrastSelected = 0

    /** Цвет текста выключенного элемента. */
    @ColorInt
    internal var itemTextColorDisabled = 0

    /** Цвет контрастного текста выключенного элемента. */
    @ColorInt
    internal var itemTextColorContrastDisabled = 0

    /** Цвет иконки элемента. */
    @ColorInt
    internal var itemIconColor = 0

    /** Цвет контрастной иконки элемента. */
    @ColorInt
    internal var itemIconColorContrast = 0

    /** Цвет иконки выбранного элемента. */
    @ColorInt
    internal var itemIconColorSelected = 0

    /** Цвет контрастной иконки выбранного элемента. */
    @ColorInt
    internal var itemIconColorContrastSelected = 0

    /** Цвет иконки выключенного элемента. */
    @ColorInt
    internal var itemIconColorDisabled = 0

    /** Цвет контрастной иконки выключенного элемента. */
    @ColorInt
    internal var itemIconColorContrastDisabled = 0

    /** Начальное считывание значений атрибутов компонента. */
    internal fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        context.withStyledAttributes(
            attrs,
            R.styleable.SbisSegmentedControl,
            defStyleAttr,
            defStyleRes
        ) {
            loadConfiguration(this)
            elevation = Elevation.L.getDimenPx(context)
            loadStyle(this)
        }
    }

    /** Callback при изменении стиля компонента. */
    internal fun onStyleChanged(context: Context, style: SbisSegmentedControlStyle) {
        ThemeContextBuilder(context, style.segmentedControlStyle, style.baseSegmentedControlStyle).build()
            .withStyledAttributes(attrs = R.styleable.SbisSegmentedControl, block = ::loadStyle)
    }

    /** Получить цвета текста для выбранного состояния в зависимости от контрастного режима. */
    internal fun getItemTextColorBySelected(isContrast: Boolean): ColorsByState =
        if (isContrast) {
            ColorsByState(itemTextColorContrastSelected, itemTextColorContrast)
        } else {
            ColorsByState(itemTextColorSelected, itemTextColor)
        }

    /** Получить цвета иконки для выбранного состояния в зависимости от контрастного режима. */
    internal fun getItemIconColorBySelected(isContrast: Boolean): ColorsByState =
        if (isContrast) {
            ColorsByState(itemIconColorContrastSelected, itemIconColorContrast)
        } else {
            ColorsByState(itemIconColorSelected, itemIconColor)
        }

    /** Получить цвета текста для выключенного состояния в зависимости от контрастного режима. */
    internal fun getItemTextColorByEnabled(isContrast: Boolean): ColorsByState =
        if (isContrast) {
            ColorsByState(itemTextColorContrastDisabled, itemTextColorContrast)
        } else {
            ColorsByState(itemTextColorDisabled, itemTextColor)
        }

    /** Получить цвета иконки для выключенного состояния в зависимости от контрастного режима. */
    internal fun getItemIconColorByEnabled(isContrast: Boolean): ColorsByState =
        if (isContrast) {
            ColorsByState(itemIconColorContrastDisabled, itemIconColorContrast)
        } else {
            ColorsByState(itemIconColorDisabled, itemIconColor)
        }

    private fun loadConfiguration(typedArray: TypedArray) = with(typedArray) {
        contrast = getBoolean(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_contrast,
            contrast
        )
        size = loadEnum(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_size,
            size,
            *SbisSegmentedControlSize.values()
        )
        distribution = loadEnum(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_distribution,
            distribution,
            *SbisSegmentedControlDistribution.values()
        )
        hasShadow = getBoolean(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_hasShadow,
            hasShadow
        )

        selectedSegmentIndex = getInt(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_selectedIndex,
            selectedSegmentIndex
        )
    }

    private fun loadStyle(typedArray: TypedArray) = with(typedArray) {
        backgroundColor = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_backgroundColor,
            Color.MAGENTA
        )
        backgroundColorContrast = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_backgroundColorContrast,
            Color.MAGENTA
        )

        itemBackgroundColor = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemBackgroundColor,
            Color.MAGENTA
        )
        itemBackgroundColorContrast = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemBackgroundColorContrast,
            Color.MAGENTA
        )

        itemTextColor = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemTextColor,
            Color.MAGENTA
        )
        itemTextColorContrast = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemTextColorContrast,
            Color.MAGENTA
        )

        itemTextColorSelected = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemTextColorSelected,
            Color.MAGENTA
        )
        itemTextColorContrastSelected = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemTextColorContrastSelected,
            Color.MAGENTA
        )

        itemTextColorDisabled = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemTextColorDisabled,
            Color.MAGENTA
        )
        itemTextColorContrastDisabled = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemTextColorContrastDisabled,
            Color.MAGENTA
        )

        itemIconColor = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemIconColor,
            Color.MAGENTA
        )
        itemIconColorContrast = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemIconColorContrast,
            Color.MAGENTA
        )

        itemIconColorSelected = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemIconColorSelected,
            Color.MAGENTA
        )
        itemIconColorContrastSelected = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemIconColorContrastSelected,
            Color.MAGENTA
        )

        itemIconColorDisabled = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemIconColorDisabled,
            Color.MAGENTA
        )
        itemIconColorContrastDisabled = getColor(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_itemIconColorContrastDisabled,
            Color.MAGENTA
        )

        borderWidth = getDimensionPixelSize(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_borderWidth,
            0
        )

        cornerRadius = getDimensionPixelSize(
            R.styleable.SbisSegmentedControl_SbisSegmentedControl_cornerRadius,
            -1
        )
    }
}