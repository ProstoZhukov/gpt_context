package ru.tensor.sbis.design.design_menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.design_menu.quick_action_menu.GridSpacingItemDecoration
import ru.tensor.sbis.design.design_menu.quick_action_menu.LeftOrientationGidLayoutManager
import ru.tensor.sbis.design.design_menu.quick_action_menu.QuickActionMenuAdapter
import ru.tensor.sbis.design.design_menu.quick_action_menu.QuickActionMenuStyleHolder
import ru.tensor.sbis.design.design_menu.view.shadow.MenuShadowStyleHolder
import ru.tensor.sbis.design.design_menu.view.shadow.ShadowPosition
import ru.tensor.sbis.design.design_menu.view.shadow.ShadowView

private const val SCROLL_DIRECTION_UP = -1
private const val SCROLL_DIRECTION_DOWN = 1

/**
 * Компонент меню быстрых действий.
 *
 * @author ra.geraskin
 */
class QuickActionMenu(val items: List<QuickActionMenuItem>) {
    /**
     * Метод обратного вызова для получения момента, когда меню быстрого действия уже полностью сконфигурировано
     * и можно получить его актуальные размеры.
     * Необходим, т.к. при изменении LayoutManager списка с Linear на Grid изменяется высота списка.
     */
    var menuLayoutReady: (() -> Unit)? = null

    private val styleHolder = QuickActionMenuStyleHolder()

    /**
     * Создать view меню быстрых действий.
     *
     */
    fun createView(
        context: Context,
        container: ViewGroup,
        @DimenRes itemWidthRes: Int? = null
    ): View {
        styleHolder.loadStyle(context = context, itemWidthRes = itemWidthRes)
        val root: View = LayoutInflater.from(context).inflate(R.layout.quick_action_menu, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.quick_action_menu_recycler)
        val menuAdapter = QuickActionMenuAdapter(styleHolder)
        recyclerView.adapter = menuAdapter
        menuAdapter.setItems(items)
        menuAdapter.clickListener = { item ->
            item.handler?.invoke()
        }

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(1, styleHolder.quickActionMenuItemOffset))
        with(recyclerView) {
            doOnPreDraw {
                val isRecyclerCanScroll =
                    canScrollVertically(SCROLL_DIRECTION_UP) || canScrollVertically(SCROLL_DIRECTION_DOWN)

                val isWidthAvailable =
                    container.measuredWidth > (styleHolder.minItemWidth * 2 + styleHolder.quickActionMenuItemOffset)

                if (isRecyclerCanScroll && isWidthAvailable) {
                    removeItemDecoration(getItemDecorationAt(0))
                    addItemDecoration(GridSpacingItemDecoration(2, styleHolder.quickActionMenuItemOffset))
                    layoutManager = LeftOrientationGidLayoutManager(context).apply {
                        spanCount = 2
                        orientation = LinearLayoutManager.VERTICAL
                    }
                }
                menuLayoutReady?.invoke()
            }
        }

        (root as? FrameLayout)?.let {
            root.addShadowView(context, ShadowPosition.BOTTOM, styleHolder, recyclerView)
            root.addShadowView(context, ShadowPosition.TOP, styleHolder, recyclerView)
        }

        return root
    }

    private fun FrameLayout.addShadowView(
        context: Context,
        shadowPosition: ShadowPosition,
        styleHolder: MenuShadowStyleHolder,
        recyclerView: RecyclerView,
    ) = ShadowView(context, shadowPosition, styleHolder).apply {
        recyclerView.addOnScrollListener(scrollListener)
        this@addShadowView.addView(this)
    }

}
