package ru.tensor.sbis.wizard.impl

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView

/**
 * Своя реализация [FragmentContainerView] с возможностью смены очерёдности отрисовки фрагментов
 * Реализация скопирована из [FragmentContainerView], но метод [setDrawDisappearingViewsLast] стал публичным
 *
 * [FragmentContainerView] решал проблему очерёдности отрисовки, см. https://issuetracker.google.com/issues/137310379
 * Но либо не дорешал, либо в мастере сошлись условия, обработка которых из коробки не предполагалась
 * А именно:
 *  1.  Невозможность использовать add транзакции, а только replace, т.к. не все шаги могли бы работать через add
 *      т.к. для этого требуется умение шага восстанавливать работающее состояние, если мастер вернулся к нему
 *  2.  "Хитрые" анимации переходов между шагами
 *      Если переход к новому шагу, то текущий "остаётся на месте", а новые "выезжает" справа
 *      Если возврат к предыдущему шагу, то текущий "уезжается" вправо, а предыдущий "уже отображён"
 *
 * @author sa.nikitin
 */
internal class WizardFragmentContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mDisappearingFragmentChildren: MutableList<View> by lazy { mutableListOf() }
    private val mTransitioningFragmentViews: MutableList<View> by lazy { mutableListOf() }

    private var mDrawDisappearingViewsFirst = true

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.dispatchApplyWindowInsets(WindowInsets(insets))
        }
        return insets
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mDrawDisappearingViewsFirst) {
            for (i in mDisappearingFragmentChildren.indices) {
                super.drawChild(canvas, mDisappearingFragmentChildren[i], drawingTime)
            }
        }
        super.dispatchDraw(canvas)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        if (mDrawDisappearingViewsFirst && mDisappearingFragmentChildren.isNotEmpty()) {
            if (mDisappearingFragmentChildren.contains(child)) {
                return false
            }
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    override fun startViewTransition(view: View) {
        if (view.parent === this) {
            mTransitioningFragmentViews.add(view)
        }
        super.startViewTransition(view)
    }

    override fun endViewTransition(view: View) {
        mTransitioningFragmentViews.remove(view)
        if ((mDisappearingFragmentChildren.remove(view))) {
            mDrawDisappearingViewsFirst = true
        }
        super.endViewTransition(view)
    }

    /**
     * @see FragmentContainerView.setDrawDisappearingViewsLast
     */
    fun setDrawDisappearingViewsLast(drawDisappearingViewsFirst: Boolean) {
        mDrawDisappearingViewsFirst = drawDisappearingViewsFirst
    }

    override fun removeViewAt(index: Int) {
        val view = getChildAt(index)
        addDisappearingFragmentView(view)
        super.removeViewAt(index)
    }

    override fun removeViewInLayout(view: View) {
        addDisappearingFragmentView(view)
        super.removeViewInLayout(view)
    }

    override fun removeView(view: View) {
        addDisappearingFragmentView(view)
        super.removeView(view)
    }

    override fun removeViews(start: Int, count: Int) {
        for (i in start until start + count) {
            val view = getChildAt(i)
            addDisappearingFragmentView(view)
        }
        super.removeViews(start, count)
    }

    override fun removeViewsInLayout(start: Int, count: Int) {
        for (i in start until start + count) {
            val view = getChildAt(i)
            addDisappearingFragmentView(view)
        }
        super.removeViewsInLayout(start, count)
    }

    override fun removeAllViewsInLayout() {
        for (i in childCount - 1 downTo 0) {
            val view = getChildAt(i)
            addDisappearingFragmentView(view)
        }
        super.removeAllViewsInLayout()
    }

    override fun removeDetachedView(child: View, animate: Boolean) {
        if (animate) {
            addDisappearingFragmentView(child)
        }
        super.removeDetachedView(child, animate)
    }

    /**
     * @see FragmentContainerView.addDisappearingFragmentView
     */
    private fun addDisappearingFragmentView(v: View) {
        if (mTransitioningFragmentViews.contains(v)) {
            mDisappearingFragmentChildren.add(v)
        }
    }
}