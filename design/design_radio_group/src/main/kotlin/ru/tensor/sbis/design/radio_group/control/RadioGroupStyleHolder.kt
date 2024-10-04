package ru.tensor.sbis.design.radio_group.control

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.radio_group.R
import ru.tensor.sbis.design.R as RDesign

/**
 * Вспомогательный класс для получения атрибутов стилизации компонента и их хранения.
 *
 * @author ps.smirnyh
 */
internal class RadioGroupStyleHolder {

    /** Радиус внешнего круга маркера радиокнопки. */
    var borderCircleRadius = 0

    /** Радиус внутреннего круга маркера радиокнопки. */
    var selectedCircleRadius = 0

    /** Ширина внешнего круга маркера радиокнопки. */
    var borderWidth = 0

    /** Стандартный отступ от маркера до контента радиокнопки. */
    var defaultMarkerPadding = 0

    /** Стандартный отступ между радиокнопками в горизонтальной ориентации. */
    var defaultHorizontalPadding = 0

    /** Размер текста для стандартного контента. */
    var defaultContentTextSize = 0

    /** Цвет выбранного состояния маркера радиокнопки. */
    var selectedCircleColor = 0

    /** Цвет обычного состояния маркера радиокнопки. */
    var unselectedCircleColor = 0

    /** Цвет выбранного состояния маркера радиокнопки в режиме чтения. */
    var readOnlySelectedCircleColor = 0

    /** Цвет маркера радиокнопки в режиме чтения. */
    var readOnlyUnselectedCircleColor = 0

    /** Цвет текста для стандартного контента в режиме чтения. */
    var defaultContentTextColorReadOnly = 0

    /** Цвет текста для стандартного контента в выбранном состоянии. */
    var defaultContentTextColorSelected = 0

    /** Цвет текста для стандартного контента. */
    var defaultContentTextColor = 0

    /** Минимальная высота радиокнопки. */
    var minHeight = 0

    /** Отступ радиокнопки от края родителя при иерархическом размещении элементов. */
    var hierarchyPadding = 0

    /** Верхний отступ радиокнопки до стандартного контента. */
    var topPadding = 0

    /** Нижний отступ радиокнопки от стандартного контента. */
    var bottomPadding = 0

    /** Верхний отступ маркера радиокнопки. */
    var circleTopPadding = 0

    /** Скругление рамки валидации. */
    var validationBorderRadius = 0

    /** Цвет рамки валидации. */
    var validationBorderColor = 0

    /** Толщина рамки валидации. */
    var validationBorderWidth = 0

    /** Отступ от рамки валидации до контента. */
    var validationPadding = 0

    /** Получить значения стилевых атрибутов. */
    fun loadStyle(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val globalAttrs = intArrayOf(
            RDesign.attr.inlineHeight_s,
            RDesign.attr.offset_xl
        )

        context.withStyledAttributes(attrs, R.styleable.SbisRadioGroupView, defStyleAttr, defStyleRes) {
            borderCircleRadius = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_borderCircleRadius,
                borderCircleRadius
            )
            selectedCircleRadius = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_selectedCircleRadius,
                selectedCircleRadius
            )
            borderWidth =
                getDimensionPixelSize(R.styleable.SbisRadioGroupView_SbisRadioGroupView_borderWidth, borderWidth)
            defaultMarkerPadding =
                getDimensionPixelSize(
                    R.styleable.SbisRadioGroupView_SbisRadioGroupView_defaultMarkerPadding,
                    defaultMarkerPadding
                )
            defaultHorizontalPadding =
                getDimensionPixelSize(
                    R.styleable.SbisRadioGroupView_SbisRadioGroupView_defaultHorizontalPadding,
                    defaultHorizontalPadding
                )
            defaultContentTextSize = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_defaultContentTextSize,
                defaultContentTextSize
            )
            selectedCircleColor =
                getColor(R.styleable.SbisRadioGroupView_SbisRadioGroupView_selectedCircleColor, selectedCircleColor)
            unselectedCircleColor =
                getColor(R.styleable.SbisRadioGroupView_SbisRadioGroupView_unselectedCircleColor, unselectedCircleColor)
            readOnlySelectedCircleColor = getColor(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_readOnlySelectedCircleColor,
                readOnlySelectedCircleColor
            )
            readOnlyUnselectedCircleColor = getColor(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_readOnlyUnselectedCircleColor,
                readOnlyUnselectedCircleColor
            )
            defaultContentTextColorReadOnly = getColor(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_defaultContentTextColorReadOnly,
                defaultContentTextColorReadOnly
            )
            defaultContentTextColorSelected = getColor(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_defaultContentTextColorSelected,
                defaultContentTextColorSelected
            )
            defaultContentTextColor = getColor(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_defaultContentTextColor,
                defaultContentTextColor
            )
            topPadding = getDimensionPixelSize(R.styleable.SbisRadioGroupView_SbisRadioGroupView_topPadding, topPadding)
            bottomPadding =
                getDimensionPixelSize(R.styleable.SbisRadioGroupView_SbisRadioGroupView_bottomPadding, bottomPadding)
            circleTopPadding = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_circleTopPadding,
                circleTopPadding
            )
            validationBorderRadius = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_validationBorderRadius,
                validationBorderRadius
            )
            validationBorderWidth = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_validationBorderWidth,
                validationBorderWidth
            )
            validationBorderColor =
                getColor(R.styleable.SbisRadioGroupView_SbisRadioGroupView_validationBorderColor, validationBorderColor)
            validationPadding = getDimensionPixelSize(
                R.styleable.SbisRadioGroupView_SbisRadioGroupView_validationPadding,
                validationPadding
            )
        }

        context.withStyledAttributes(attrs = globalAttrs) {
            minHeight = getDimensionPixelSize(globalAttrs.indexOf(RDesign.attr.inlineHeight_s), minHeight)
            hierarchyPadding = getDimensionPixelSize(globalAttrs.indexOf(RDesign.attr.offset_xl), hierarchyPadding)
        }
    }
}