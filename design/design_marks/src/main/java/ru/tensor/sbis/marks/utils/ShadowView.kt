package ru.tensor.sbis.marks.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import ru.tensor.sbis.marks.R

/**
 * View-элемент тени для верхней границы для [NestedScrollView].
 *
 * @author ra.geraskin
 */

private const val FADE_ANIMATION_DURATION = 100
private const val SCROLL_DIRECTION_UP = -1

internal class ShadowView constructor(context: Context) : View(context) {

    /**
     * Слушатель изменения скрола [NestedScrollView] и показа/скрытия тени.
     */
    val scrollListener = NestedScrollView.OnScrollChangeListener { scrollView, _, _, _, _ ->
        val canScrollUp = scrollView.canScrollVertically(SCROLL_DIRECTION_UP)
        if (!canScrollUp) hideShadow()
        if (canScrollUp && visibility != VISIBLE) showShadow()
    }

    init {
        visibility = INVISIBLE
        background = ContextCompat.getDrawable(context, R.drawable.top_shadow)
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.design_marks_scroll_view_shadow_height),
            Gravity.TOP
        )
    }

    private fun showShadow() {
        alpha = 0f
        visibility = VISIBLE
        animate()
            .alpha(0.3f)
            .setDuration(FADE_ANIMATION_DURATION.toLong())
            .setListener(null)
    }

    private fun hideShadow() {
        animate()
            .alpha(0f)
            .setDuration(FADE_ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = INVISIBLE
                }
            })
    }
}