package ru.tensor.sbis.design.design_menu.view.shadow

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * View-элемент тени для верхней и нижней границы списка меню быстрых действий.
 *
 * @author ra.geraskin
 */

@SuppressLint("ViewConstructor")
internal class ShadowView(context: Context, position: ShadowPosition, styleHolder: MenuShadowStyleHolder) :
    View(context) {

    /**
     * Слушатель изменения скрола [RecyclerView] и показа/скрытия тени.
     */
    val scrollListener = ScrollBoundsListener(
        scrollDirection = position.scrollDirection,
        onScrolled = this::showShadow,
        onBorderReached = this::hideShadow
    )

    private var isShown = false

    init {
        visibility = INVISIBLE
        background = GradientDrawable(
            position.gradientOrientation,
            intArrayOf(styleHolder.shadowColor, Color.TRANSPARENT)
        )
        layoutParams = FrameLayout.LayoutParams(
            WRAP_CONTENT,
            styleHolder.shadowHeight,
            position.gravity
        )
    }

    private fun showShadow() {
        if (isShown) return
        isShown = true
        alpha = 0f
        visibility = VISIBLE
        animate()
            .alpha(1f)
            .setDuration(FADE_ANIMATION_DURATION.toLong())
            .setListener(null)
    }

    private fun hideShadow() {
        isShown = false
        animate()
            .alpha(0f)
            .setDuration(FADE_ANIMATION_DURATION.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isShown = false
                    visibility = INVISIBLE
                }
            })
    }
}

private const val FADE_ANIMATION_DURATION = 150