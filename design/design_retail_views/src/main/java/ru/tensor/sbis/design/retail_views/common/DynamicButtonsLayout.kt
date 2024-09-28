package ru.tensor.sbis.design.retail_views.common

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.retail_views.R
import timber.log.Timber

/**
 * Контейнер призванный автоматически скрывать View, не влезшие в контейнер по ширине или количеству [maxVisibleChildren].
 * Внутри контейнера обязана быть кнопка предназначения для открытия меню скрытых действий,
 * если такой кнопки не будет - лейаут может выкинуть исключение, если сборка дебажная.
 * Для работы контейнера необходимо:
 * - Добавить в контейнер кнопку, которая будет использоваться для раскрытия меню скрытых действий.
 * - Передать в контейнер идентификатор кнопки из предыдущего пункта через параметр app:hiddenActionsButtonId.
 * - Предоставить действие через параметр app:onHiddenActionsButtonListener, которое будет происходить при нажатии на кнопку из пункта 1.
 * - У каждого ребенка в контейнере, который может быть скрыт, должен быть свой идентификатор действия, устанавливаемый через параметр bind:toolbarActionIdTag.
 * Идентификаторы скрывшихся кнопок будут передаваться в слушатель из предыдущего пункта.
 */
@SuppressLint("CustomViewStyleable")
class DynamicButtonsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    @IdRes
    private var hiddenActionsButtonId: Int? = null
    private var isHiddenActionsButtonAlwaysVisible: Boolean = false
    private var maxVisibleChildren = INFINITE_NUMBER_OF_CHILDREN

    init {
        val attrValues = context.obtainStyledAttributes(
            attrs,
            R.styleable.RetailViewsDynamicButtonsLayout,
            defStyleAttr,
            0
        )

        val hiddenActionsButtonIdAttr = attrValues.getResourceId(
            R.styleable.RetailViewsDynamicButtonsLayout_retail_views_hiddenActionsButtonId,
            HIDDEN_ACTION_BUTTON_ID_NOT_SET
        )
        if (hiddenActionsButtonIdAttr != HIDDEN_ACTION_BUTTON_ID_NOT_SET) {
            hiddenActionsButtonId = hiddenActionsButtonIdAttr
        }

        isHiddenActionsButtonAlwaysVisible =
            attrValues.getBoolean(
                R.styleable.RetailViewsDynamicButtonsLayout_retail_views_isHiddenActionsButtonAlwaysVisible,
                false
            )

        maxVisibleChildren =
            attrValues.getInt(
                R.styleable.RetailViewsDynamicButtonsLayout_retail_views_maxVisibleChildren,
                INFINITE_NUMBER_OF_CHILDREN
            )

        attrValues.recycle()
    }

    /**
     * Нужно ли отображать кнопку меню скрытых действий всегда, вне зависимости от того есть не влезшие кнопки, или нет.
     */
    fun setHiddenActionsButtonAlwaysVisible(isHiddenActionsButtonAlwaysVisible: Boolean) {
        this.isHiddenActionsButtonAlwaysVisible = isHiddenActionsButtonAlwaysVisible
    }

    /** Выставить id кнопки меню скрытых действий. */
    fun setHiddenActionsButtonId(@IdRes buttonId: Int?) {
        getHiddenActionsButton()?.setOnClickListener(null) // при смене кнопки скрытых действий убираем слушателя со старой
        hiddenActionsButtonId = buttonId
    }

    /** Задать максимальное количество видимых элементов. */
    fun setMaxChildren(maxChildren: Int) {
        maxVisibleChildren = maxChildren
        requestLayout()
    }

    /** Выставить действие при нажатии на кнопку меню скрытых действий. */
    fun setOnHiddenActionsButtonListener(onMoreActionsClick: ((View, List<String>) -> Unit)?) {
        getHiddenActionsButton()?.setOnClickListener {
            onMoreActionsClick?.invoke(it, getAllHiddenButtonsActionId())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // При новом пересчете убираем кнопку скрытых действий чтобы она не мешала в расчетах, если она не показывается всегда
        if (!isHiddenActionsButtonAlwaysVisible) {
            getHiddenActionsButton()?.let {
                it.visibility = View.GONE
            }
        }

        val layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
        var widthUsed = paddingLeft + paddingRight
        var maxChildHeight = 0

        var hasNotFittingChildren = false
        var lastFittingChildIndex = 0

        val buttonsInPriorityOrder = getChildrenSortedByPriority()

        buttonsInPriorityOrder.forEachIndexed { index, child ->
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val childWidth = getChildWidth(child)

            val isChildLimited = (maxVisibleChildren != INFINITE_NUMBER_OF_CHILDREN && (index + 1) > maxVisibleChildren)
            if (layoutWidth - (widthUsed + childWidth) < 0 || isChildLimited) {
                hasNotFittingChildren = true
                return@forEachIndexed
            }
            widthUsed += childWidth
            maxChildHeight = Integer.max(getChildHeight(child), maxChildHeight)

            lastFittingChildIndex = index
        }

        // Если в контейнере есть кнопки и они не влезают, то показываем кнопку скрытых действий
        if (!isHiddenActionsButtonAlwaysVisible && hasNotFittingChildren) {
            val hiddenActionsButton = getHiddenActionsButton()
            if (hiddenActionsButton == null) {
                val errorMessage = "Кнопка скрытых действий контейнера должна быть предоставлена в лейаут!"
                Timber.w(errorMessage)
            } else {
                hiddenActionsButton.isVisible = true
                widthUsed -= getChildWidth(buttonsInPriorityOrder[lastFittingChildIndex])

                measureChild(hiddenActionsButton, widthMeasureSpec, heightMeasureSpec)
                widthUsed += getChildWidth(hiddenActionsButton)
                maxChildHeight = Integer.max(getChildHeight(hiddenActionsButton), maxChildHeight)
            }
        }

        setMeasuredDimension(
            reconcileSize(widthUsed, widthMeasureSpec),
            reconcileSize(maxChildHeight + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    private fun getChildWidth(child: View): Int {
        val childLayoutParams = child.layoutParams as ButtonsLayoutParams
        return child.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
    }

    private fun getChildHeight(child: View): Int {
        val childLayoutParams = child.layoutParams as ButtonsLayoutParams
        return child.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin
    }

    private fun getChildrenSortedByPriority(): List<View> {
        val buttonsSortedByPriority = getButtonsByPriorities()
        return listOfNotNull(buttonsSortedByPriority.topPriority).plus(
            buttonsSortedByPriority.highPriority
        ).plus(buttonsSortedByPriority.lowPriority)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var widthLeft = measuredWidth - (paddingLeft + paddingRight)
        var childToLayoutLeftPosition = paddingLeft
        val childrenToLayoutWithLayoutWidth = getChildrenFitInRemainingSpace(
            getChildrenSortedByPriority(), widthLeft
        )
        val childrenToLayout = childrenToLayoutWithLayoutWidth.second
        childToLayoutLeftPosition += widthLeft - childrenToLayoutWithLayoutWidth.first

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childLayoutParams = child.layoutParams as ButtonsLayoutParams

            if (child.isVisible && childrenToLayout.contains(child)) {
                val childToLayoutTopPosition = paddingTop + childLayoutParams.topMargin
                childToLayoutLeftPosition += childLayoutParams.leftMargin
                child.layout(
                    childToLayoutLeftPosition,
                    childToLayoutTopPosition,
                    childToLayoutLeftPosition + child.measuredWidth,
                    childToLayoutTopPosition + child.measuredHeight
                )
                val drawnChildTakenWidth = child.measuredWidth
                widthLeft -= drawnChildTakenWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
                childToLayoutLeftPosition += drawnChildTakenWidth + childLayoutParams.rightMargin
            } else {
                // Скрываем child
                child.layout(0, 0, 0, 0)
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?) = ButtonsLayoutParams(context, attrs)

    override fun checkLayoutParams(p: LayoutParams?) = p is ButtonsLayoutParams

    override fun generateLayoutParams(p: LayoutParams?) = ButtonsLayoutParams(p)

    override fun generateDefaultLayoutParams() = ButtonsLayoutParams()

    private fun getAllHiddenButtonsActionId(): List<String> {
        val hiddenActions: MutableList<String> = mutableListOf()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE && child.width == 0) {
                val tag = child.getTag(R.id.retail_views_buttons_layout_action_id) as? String
                if (tag != null) {
                    hiddenActions.add(tag)
                }
            }
        }

        return hiddenActions
    }

    private fun getHiddenActionsButton(): View? = hiddenActionsButtonId?.let(::findViewById)

    private fun getButtonsByPriorities(): ButtonsByPriority {
        val lowPriorityChildren = mutableListOf<View>()
        val highPriorityChildren = mutableListOf<View>()
        var moreActionsButton: View? = null
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.isVisible && child != null) {
                if (hiddenActionsButtonId == child.id) {
                    moreActionsButton = child
                } else {
                    val childLayoutParams = child.layoutParams as ButtonsLayoutParams
                    with(childLayoutParams) {
                        if (hideLast) {
                            highPriorityChildren.add(child)
                        } else {
                            lowPriorityChildren.add(child)
                        }
                    }
                }
            }
        }
        return ButtonsByPriority(lowPriorityChildren, highPriorityChildren, moreActionsButton)
    }

    private fun getChildrenFitInRemainingSpace(children: List<View>, remainingWidthToUse: Int): Pair<Int, List<View>> {
        var childrenWidth = 0
        val fittingChildren = mutableListOf<View>()
        children.forEach {
            val childLayoutParams = it.layoutParams as ButtonsLayoutParams
            val childWidth = it.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
            if (remainingWidthToUse - (childrenWidth + childWidth) < 0) {
                return@forEach
            }
            childrenWidth += childWidth
            fittingChildren.add(it)
        }

        return childrenWidth to fittingChildren
    }

    private fun reconcileSize(contentSize: Int, measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> if (contentSize < specSize) {
                contentSize
            } else {
                specSize
            }
            else -> contentSize
        }
    }

    /** @SelfDocumented */
    class ButtonsLayoutParams : MarginLayoutParams {

        /** Дети с флагом стоящим в false будут скрываться первыми при нехватке места. */
        var hideLast: Boolean = false

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RetailViewsDynamicButtonsLayout_Layout)
            hideLast = a.getBoolean(R.styleable.RetailViewsDynamicButtonsLayout_Layout_retail_views_hideLast, false)

            a.recycle()
        }

        constructor(params: LayoutParams?) : super(params) {
            if (params is ButtonsLayoutParams) {
                hideLast = params.hideLast
            }
        }

        constructor() : super(MATCH_PARENT, MATCH_PARENT)
    }

    /** @SelfDocumented */
    data class ButtonsByPriority(
        /** Кнопки которые можно скрывать */
        val lowPriority: List<View>,
        /** Кнопки которые должны быть видимы всегда, но будут скрыты если места для них не хватает */
        val highPriority: List<View>,
        /** Кнопка меню с доп. действиями. Должна быть обязательно видна, если в контейнере не влезла кнопка */
        val topPriority: View?
    )

    companion object {
        private const val HIDDEN_ACTION_BUTTON_ID_NOT_SET = -1
        private const val INFINITE_NUMBER_OF_CHILDREN = -1
    }
}

/** Задать флаг, который отвечает за то будет ли элемент скрываться в последнюю очередь. */
@BindingAdapter("hideLast")
fun View.setIsHideLast(hideLast: Boolean) {
    val lp = layoutParams
    if (lp is DynamicButtonsLayout.ButtonsLayoutParams) {
        lp.hideLast = hideLast
    }
}

/** Сохраняет в тег ребенка идентификатор привязанного действия */
@BindingAdapter("dynamicButtonsActionId")
fun View.setDynamicButtonsActionId(toolbarActionId: String?) {
    toolbarActionId?.let {
        setTag(R.id.retail_views_buttons_layout_action_id, it)
    }
}