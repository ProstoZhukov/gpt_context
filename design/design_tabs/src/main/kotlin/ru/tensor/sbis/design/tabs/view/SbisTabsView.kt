package ru.tensor.sbis.design.tabs.view

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.HorizontalScrollView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.tabs.R
import ru.tensor.sbis.design.tabs.api.SbisTabsViewApi
import ru.tensor.sbis.design.tabs.api.SbisTabsViewApiInternal
import ru.tensor.sbis.design.tabs.tabItem.SbisTabView
import ru.tensor.sbis.design.tabs.util.SbisTabsViewSavedState
import ru.tensor.sbis.design.tabs.util.isContainsTextContent
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.extentions.setBottomMargin
import java.util.LinkedList

/**
 * Компонент Вкладки.
 *
 * Стандарт - https://n.sbis.ru/article/af25b185-6e15-4866-9250-bd199575a5ca
 * @author da.zolotarev
 */
class SbisTabsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.sbisTabsView_Theme,
    @StyleRes defStyleRes: Int = R.style.SbisTabsViewDefaultTheme,
    private val controller: SbisTabsViewController = SbisTabsViewController()
) : HorizontalScrollView(
    ThemeContextBuilder(context, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    SbisTabsViewApi by controller,
    SbisTabsViewApiInternal by controller {
    private val styleHolder
        get() = controller.styleHolder

    private var isLockedRequestLayout = false

    internal val tabsContainer = LinearLayoutWithMarker(getContext())
    internal var itemViews: LinkedList<SbisTabView> = LinkedList<SbisTabView>()

    /**
     * Настройка, если необходимо чтобы View мерялась по своей ширине, а не занимала все пространство.
     * TODO Удалить и найти замену по https://online.sbis.ru/opendoc.html?guid=37f81d1f-9d7b-4e5e-96e6-93f68d311002&client=3
     */
    @Deprecated(
        "Временное решение для нужд авторизации, не использовать. " +
            "Выпилить по https://online.sbis.ru/opendoc.html?guid=37f81d1f-9d7b-4e5e-96e6-93f68d311002&client=3"
    )
    var isWidthWrapContent: Boolean = false

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
        isSaveEnabled = true
        addView(tabsContainer)
        isHorizontalFadingEdgeEnabled = true
        setFadingEdgeLength(styleHolder.fadeEdgeWidth)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        // Защита от выставления паддинга у SbisTabsView
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val parentMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpec = MeasureSpec.makeMeasureSpec(
            if (parentMode == MeasureSpec.EXACTLY) {
                minOf(
                    styleHolder.maxPanelHeight,
                    parentHeight
                )
            } else {
                styleHolder.maxPanelHeight
            },
            MeasureSpec.EXACTLY
        )
        if (isWidthWrapContent) {
            tabsContainer.measure(widthMeasureSpec, heightSpec)
            super.onMeasure(tabsContainer.measuredWidth, heightSpec)

        } else {
            super.onMeasure(widthMeasureSpec, heightSpec)
        }
        isLockedRequestLayout = true
        updateItemsBottomMargin()
        isLockedRequestLayout = false
    }

    override fun requestLayout() {
        if (!isLockedRequestLayout) {
            super.requestLayout()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return SbisTabsViewSavedState(super.onSaveInstanceState()).apply {
            selectedItemIndex = itemViews.indexOfFirst { it.isSelected }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SbisTabsViewSavedState) {
            super.onRestoreInstanceState(state.superState)
            controller.restoreSelectedState(state.selectedItemIndex)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Отключение сохранения состояния у детей, чтобы не перезатирался state у [SbisTabsView].
     * Подробности - https://www.netguru.com/blog/how-to-correctly-save-the-state-of-a-custom-view-in-android
     */
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        controller.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        controller.onDetachedFromWindow()
    }

    /**
     * Обновить отступ снизу у всех элементов (под капотом зовёт requestLayout).
     */
    private fun updateItemsBottomMargin() {
        // По стандарту базовая линия текста таба должна быть на 39 dp от верха.
        val itemBottomMargin =
            styleHolder.maxPanelHeight - styleHolder.tabTextBaseline - (
                itemViews.filterIndexed { index, view -> !tabs[index].isMain }.maxOfOrNull { it.getBaselineOffset() }
                    ?: 0
                )

        itemViews.forEachIndexed { index, view ->
            if (tabs[index].content.isContainsTextContent() && !tabs[index].isMain) {
                view.setBottomMargin((itemBottomMargin).coerceAtLeast(0))
            } else {
                view.setBottomMargin(
                    (styleHolder.maxPanelHeight - styleHolder.tabTextBaseline - view.getBaselineOffset()).coerceAtLeast(
                        0
                    ) + controller.iconCounterBottomMargin(tabs[index].content)
                )
            }
        }
    }
}
