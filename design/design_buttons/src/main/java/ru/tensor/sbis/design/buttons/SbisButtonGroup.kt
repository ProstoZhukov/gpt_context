package ru.tensor.sbis.design.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.buttons.base.models.style.UnaccentedButtonStyle
import ru.tensor.sbis.design.buttons.group.api.SbisButtonGroupApi
import ru.tensor.sbis.design.buttons.group.api.SbisButtonGroupController
import ru.tensor.sbis.design.buttons.group.api.SbisButtonGroupController.Companion.SBIS_BUTTON_VIEW_GROUP_STYLE_MAIN
import ru.tensor.sbis.design.buttons.group.api.SbisButtonGroupController.Companion.SBIS_BUTTON_VIEW_GROUP_STYLE_STANDARD
import androidx.core.view.isGone
import ru.tensor.sbis.design.theme.global_variables.Offset
import kotlin.math.roundToInt

/**
 * Реализация [AbstractSbisButton] для группировки кнопок [SbisButton].
 *
 * @author ma.kolpakov
 */
class SbisButtonGroup private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisButtonGroupController
) : ViewGroup(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
),
    SbisButtonGroupApi by controller {

    private val visibleItems = ArrayList<View>()

    @Dimension
    private val sidePadding = Offset.S.getDimen(context)
    private var hasSidePadding = false

    @Dimension
    private val primaryButtonSpacing = Offset.M.getDimen(context)

    init {
        // TODO: 01.12.2021 https://online.sbis.ru/opendoc.html?guid=5bb5ceee-6827-4b17-8028-0a810e30941f
        clipChildren = false
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.paleSbisButtonGroupTheme,
        @StyleRes defStyleRes: Int = R.style.SbisButtonGroupDefaultPaleTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, SbisButtonGroupController())

    /**
     * Добавить "главную" кнопку в группу.
     */
    fun addMainButton(button: SbisButton) {
        val layoutParams = generateDefaultLayoutParams().apply {
            buttonViewGroupStyle = SBIS_BUTTON_VIEW_GROUP_STYLE_MAIN
        }
        addView(button, layoutParams)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        controller.onChildAdded(child, child.isMainButton)
    }

    override fun generateDefaultLayoutParams() =
        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?) =
        p is LayoutParams

    override fun generateLayoutParams(attrs: AttributeSet?) =
        LayoutParams(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec = MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY)
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var hasAccentButton = false

        visibleItems.clear()
        // предварительный обход для оценки межкнопочных интервалов
        for (childIndex in 0 until childCount) {
            val child = getChildAt(childIndex)
            if (child.isGone) {
                continue
            } else {
                visibleItems.add(child)
                hasAccentButton = hasAccentButton || child.isNeedSidePadding(childIndex)
            }
        }

        var width = when {
            hasAccentButton && visibleItems.size == 1 -> {
                hasSidePadding = false
                0F
            }
            hasAccentButton -> {
                hasSidePadding = true
                sidePadding + primaryButtonSpacing
            }
            else -> {
                hasSidePadding = true
                sidePadding * 2F
            }
        }

        visibleItems.onEach { child ->
            /*
             В большинстве случаев достаточно распределить пространство по остаточному принципу.
             Не отработает, если view в начале списка займут всё пространство. Решение - вводить
             минимальную ширину элементов
             */
            val childWidthSpec = widthMeasureSpec.takeIf { widthMode == MeasureSpec.UNSPECIFIED }
                ?: MeasureSpec.makeMeasureSpec(parentWidth - width.roundToInt(), MeasureSpec.AT_MOST)
            measureChild(child, childWidthSpec, heightSpec)
            width += child.measuredWidth
        }

        // Обновление видимости фона.
        // Если кнопка одна и главная, то нужно скрывать фон из-за ошибки округления,
        // при которой фон группы на пиксель больше кнопки.
        controller.updateVisibilityBackground(hasSidePadding)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(width.roundToInt(), MeasureSpec.EXACTLY),
            heightSpec
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var dX = if (hasSidePadding) sidePadding.roundToInt() else 0
        visibleItems.onEachIndexed { index, child ->
            if (hasSidePadding && child.isNeedSidePadding(index)) {
                dX += primaryButtonSpacing.roundToInt()
            }
            child.layout(dX, 0, dX + child.measuredWidth, child.measuredHeight)
            dX += child.measuredWidth
        }
        this.layoutParams
    }

    /**
     * Кнопка определена как "главная" в группе.
     */
    private val View.isMainButton: Boolean
        get() {
            val params = layoutParams
            return params is LayoutParams &&
                params.buttonViewGroupStyle == SBIS_BUTTON_VIEW_GROUP_STYLE_MAIN ||
                // кнопки со стилем primary тоже считаем главными - частый сценарий
                this is SbisButton && style == PrimaryButtonStyle
        }

    /**
     * Функция определяет нужен ли паддинг для кнопки.
     *
     * Это нужно чтобы бордер кнопки совпадал с бордером группы.
     *
     * Вовзвращает true, если стиль не [UnaccentedButtonStyle] - паддинг не нужен только для этого стиля
     * и если кнопка не является последней в группе.
     */
    private fun View.isNeedSidePadding(index: Int): Boolean =
        this is SbisButton && style != UnaccentedButtonStyle && (index >= visibleItems.size - 1)

    class LayoutParams : ViewGroup.LayoutParams {

        var buttonViewGroupStyle = SBIS_BUTTON_VIEW_GROUP_STYLE_STANDARD

        constructor(width: Int, height: Int) : super(width, height)

        constructor(
            context: Context,
            attrs: AttributeSet?
        ) : super(context, attrs) {
            context.withStyledAttributes(attrs, R.styleable.SbisButtonGroup_Layout) {
                buttonViewGroupStyle = getInteger(
                    R.styleable.SbisButtonGroup_Layout_SbisButtonGroup_buttonStyle,
                    SBIS_BUTTON_VIEW_GROUP_STYLE_STANDARD
                )
            }
        }
    }
}
