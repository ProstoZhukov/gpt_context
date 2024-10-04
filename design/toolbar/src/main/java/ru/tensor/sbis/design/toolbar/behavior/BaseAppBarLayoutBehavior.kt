package ru.tensor.sbis.design.toolbar.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.toolbar.R

/**
 * Базовый Behavior для AppBarLayout
 *
 * @author us.bessonov
 */
abstract class BaseAppBarLayoutBehavior : AppBarLayout.Behavior {

    @JvmField
    @IdRes
    var mScrollViewId: Int = View.NO_ID

    private var mScrollListener: ScrollListener? = null

    constructor()
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        if (attributeSet != null) {
            val a = context.obtainStyledAttributes(attributeSet, R.styleable.BaseAppBarLayoutBehavior)
            mScrollViewId = a.getResourceId(R.styleable.BaseAppBarLayoutBehavior_scrollview_id, mScrollViewId)
            a.recycle()
        }

        disableDrag()

    }

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
            super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    @Suppress("DEPRECATION")
    override fun onNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(parent, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyConsumed != 0) {
            val canScrollContentUnderAppBarLayout = canScrollContentUnderAppBarLayout(parent)
            onNestedScrolled(parent, child, target, dyConsumed, canScrollContentUnderAppBarLayout)
        }
    }

    protected fun canScrollContentUnderAppBarLayout(coordinatorLayout: CoordinatorLayout): Boolean {
        return canScrollContentUnderAppBarLayout(getRecyclerView(coordinatorLayout))
    }

    protected fun canScrollContentUnderAppBarLayout(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                return layoutManager.findFirstCompletelyVisibleItemPosition() > 0
            }
        }
        return false
    }

    private fun getRecyclerView(coordinatorLayout: CoordinatorLayout): RecyclerView? {
        return if (mScrollViewId != View.NO_ID) {
            val scrollView = coordinatorLayout.findViewById<View>(mScrollViewId)
            getRecyclerView(scrollView)
        } else {
            null
        }
    }

    private fun getRecyclerView(scrollView: View?): RecyclerView? {
        var recyclerView: RecyclerView? = null
        if (scrollView is RecyclerView) {
            recyclerView = scrollView
        } else if (scrollView is AbstractListView<*, *>) {
            recyclerView = scrollView.recyclerView
        }
        return recyclerView
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun disableDrag() {
        setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }

    protected fun addScrollListener(parent: CoordinatorLayout, child: AppBarLayout) {
        val recyclerView = getRecyclerView(parent)
        if (recyclerView != null) {
            mScrollListener = ScrollListener(child)
            recyclerView.addOnScrollListener(mScrollListener!!)
        }
    }

    /**
     * Дополнительный слушатель, необходимый из-за того,
     * что основной слушатель [.onNestedScroll]
     * не всегда отрабатывает из-за бага Support Library 28.0.0
     * https://online.sbis.ru/opendoc.html?guid=ac93cfc4-6110-4531-b958-7d2409fba121
     * https://issuetracker.google.com/issues/115569344
     */
    private inner class ScrollListener(private val mAppBarLayout: AppBarLayout) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy != 0) {
                val canScrollContentUnderAppBarLayout = canScrollContentUnderAppBarLayout(recyclerView)
                onNestedScrolled(mAppBarLayout, recyclerView, canScrollContentUnderAppBarLayout)
            }
        }
    }

    abstract fun onNestedScrolled(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dyConsumed: Int,
        isContentUnderAppBarLayout: Boolean
    )

    abstract fun onNestedScrolled(
        appBarLayout: AppBarLayout,
        recyclerView: RecyclerView,
        isContentUnderAppBarLayout: Boolean
    )
}