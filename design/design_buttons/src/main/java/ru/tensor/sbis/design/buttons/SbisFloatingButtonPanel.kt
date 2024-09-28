package ru.tensor.sbis.design.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.getMode
import android.view.View.MeasureSpec.getSize
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import ru.tensor.sbis.design.buttons.api.SbisFloatingButtonPanelApi
import ru.tensor.sbis.design.buttons.api.SbisFloatingButtonPanelController
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.HorizontalAlignment.*
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.utils.theme.AbstractHeightCompatibilityView
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Плавающий контейнер, который отвечает за размещение и движение одиночных кнопок и групп при прокрутке. Для вложенных
 * элементов устанавливается [contentElevation].
 *
 * @author ma.kolpakov
 */
class SbisFloatingButtonPanel private constructor(
    context: Context,
    attrs: AttributeSet?,
    private val controller: SbisFloatingButtonPanelController
) : ViewGroup(context, attrs),
    SbisFloatingButtonPanelApi by controller,
    CoordinatorLayout.AttachedBehavior {

    private val visibleItems = ArrayList<View>()

    /**
     * Элементы фиксированной ширины выделены отдельно, измеряются проще.
     */
    private val fixedSizeItems = ArrayList<View>()

    /**
     * Элементы, которые заполняют свободное пространство. Требуют двойного измерения для пропорционального
     * заполнения - выделены отдельно.
     */
    private val flexibleSizeItems = ArrayList<View>()

    /**
     * Стандартный подъём плавающего контента в панели.
     */
    @Dimension
    private val contentElevation =
        resources.getDimension(RDesign.dimen.elevation_extra_high)

    @Px
    private val padding = Offset.M.getDimenPx(context)

    @Px
    private var measuredContentWidth: Int = 0

    @Px
    private var measuredContentHeight: Int = 0

    private val inlinedViews = mutableListOf<AbstractHeightCompatibilityView<*>>()
    private var maxSize: AbstractHeight = InlineHeight.X4S

    init {
        // тени плавающих кнопок не должны обрываться
        clipToPadding = false
        updatePadding(padding, padding, padding, padding)
        controller.attach(this, attrs)
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) :
        this(context, attrs, SbisFloatingButtonPanelController())

    override fun addView(child: View, index: Int, params: LayoutParams) {
        super.addView(child, index, params)
        child.elevation = contentElevation

        child.contentDescription = FAB_VIEW_DESC

        if (child is AbstractHeightCompatibilityView<*>) {
            if (maxSize.getDimen(context) < child.inlineHeight.globalVar.getDimen(context)) {
                maxSize = child.inlineHeight.globalVar
                inlinedViews.onEach { it.setInlineHeight(maxSize) }
            } else if (maxSize.getDimen(context) > child.inlineHeight.globalVar.getDimen(context)) {
                child.setInlineHeight(maxSize)
            }
            inlinedViews.add(child)
        }
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)

        if (child is AbstractHeightCompatibilityView<*> &&
            inlinedViews.remove(child) &&
            inlinedViews.isEmpty()
        ) {
            maxSize = InlineHeight.X4S
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val panelHeightMode = getMode(heightMeasureSpec)
        var panelHeight = getSize(heightMeasureSpec)

        visibleItems.clear()
        fixedSizeItems.clear()
        flexibleSizeItems.clear()
        // оценим количество видимых элементов
        for (childIndex in 0 until childCount) {
            val child = getChildAt(childIndex)
            if (child.isGone) continue
            visibleItems.add(child)
            if (child.layoutParams.width == LayoutParams.MATCH_PARENT) {
                flexibleSizeItems.add(child)
            } else {
                fixedSizeItems.add(child)
            }
        }

        measuredContentWidth = 0
        measuredContentHeight = 0
        if (visibleItems.isNotEmpty()) {
            val containerWidth = getSize(widthMeasureSpec)
            measureFixedSizeItems(fixedSizeItems, containerWidth, heightMeasureSpec)
            measureFlexibleSizeItems(
                flexibleSizeItems,
                containerWidth - measuredContentWidth - paddingStart - paddingEnd,
                heightMeasureSpec
            )
        }

        panelHeight = when (panelHeightMode) {
            AT_MOST, UNSPECIFIED -> suggestedMinimumHeight
            else -> panelHeight
        }

        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), panelHeight)
    }

    override fun getSuggestedMinimumHeight(): Int {
        return maxOf(
            measuredContentHeight + paddingTop + paddingBottom,
            super.getSuggestedMinimumHeight()
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var dX = when (controller.align) {
            LEFT -> paddingStart
            CENTER -> (measuredWidth - measuredContentWidth) / 2
            RIGHT -> measuredWidth - paddingStart - measuredContentWidth
        }
        visibleItems.onEach { child ->
            // выравнивание по вертикальному центру
            val top = paddingTop + (measuredContentHeight - child.measuredHeight) / 2
            child.layout(dX, top, dX + child.measuredWidth, top + child.measuredHeight)
            dX += child.measuredWidth + padding
        }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> =
        controller.getSbisViewBehavior()

    override fun offsetTopAndBottom(offset: Int) {
        if (controller.checkOffsetTopAndBottomInability(offset)) return
        super.offsetTopAndBottom(offset)
    }

    /**
     * Измерить элемент с фиксированной шириной.
     */
    private fun measureFixedSizeItems(children: List<View>, @Px remainSpace: Int, heightSpec: Int) {
        children.asReversed().forEach { child ->
            measureChild(child, makeMeasureSpec(remainSpace - measuredContentWidth, AT_MOST), heightSpec)
            measuredContentWidth += child.measuredWidth + padding
            measuredContentHeight = maxOf(measuredContentHeight, child.measuredHeight)
        }
        // удаляем последний один паддинг так как последний элемент не должен иметь отступ справа
        measuredContentWidth -= padding
    }

    /**
     * Измерить элемент с пропорциональным заполнением [remainSpace].
     */
    private fun measureFlexibleSizeItems(children: List<View>, @Px remainSpace: Int, heightSpec: Int) {
        // ширина элементов, которые хотят занять всё оставшееся пространство (без учёта внутренних отступов)
        var flexibleWidth = 0
        val widthSpec = makeMeasureSpec(remainSpace, AT_MOST)
        children.onEach { child ->
            // сначала измерим так, чтобы элемент занял минимальное пространство
            measureChild(child, widthSpec, heightSpec)
            flexibleWidth += child.measuredWidth
            measuredContentHeight = maxOf(measuredContentHeight, child.measuredHeight)
        }
        // перераспределим свободное пространство между теми, кто хочет занять максимум
        val remainWidth = remainSpace - padding * children.size
        children.onEach { child ->
            val weight = child.measuredWidth / flexibleWidth.toFloat()
            val childWidth = (remainWidth * weight).roundToInt()
            measuredContentWidth += childWidth + padding
            child.measure(makeMeasureSpec(childWidth, EXACTLY), heightSpec)
        }
    }
}

private const val FAB_VIEW_DESC = "fab_view"