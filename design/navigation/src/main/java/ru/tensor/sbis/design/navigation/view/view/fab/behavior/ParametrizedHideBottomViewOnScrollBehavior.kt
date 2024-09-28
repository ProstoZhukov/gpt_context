package ru.tensor.sbis.design.navigation.view.view.fab.behavior

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnLayout
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

/**
 * Аналог класса [com.google.android.material.behavior.HideBottomViewOnScrollBehavior]
 * с возможностью переопределять параметры анимации.
 *
 * TODO: 4/10/2019 Использовать с осторожностью. [Реализация будет переработана по задаче](https://online.sbis.ru/opendoc.html?guid=99d81a0b-a567-43be-b54a-139d5b77c163)
 *
 * @param enterAnimationDuration время возникновения View, мс
 * @param exitAnimationDuration время исчезновения View, мс
 *
 * @author ma.kolpakov
 */
abstract class ParametrizedHideBottomViewOnScrollBehavior<V : View> @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    private val enterAnimationDuration: Long = 150,
    private val exitAnimationDuration: Long = 150
) : CoordinatorLayout.Behavior<V>(context, attrs), SlideableChildBehavior<V> {

    private var height: Int? = null // высота View
    private var currentAnimator: ViewPropertyAnimator? = null

    protected var currentState = STATE_SCROLLED_UP
    protected var isSliding: Boolean = false

    override var spacing = 0 // расстояние от View до нижней границы экрана

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: V,
        layoutDirection: Int
    ): Boolean {
        height = child.measuredHeight
        return super.onLayoutChild(
            parent,
            child,
            layoutDirection
        )
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int
    ): Boolean {
        return nestedScrollAxes == 2
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        if (currentState != STATE_SCROLLED_DOWN && dyConsumed > 0) {
            slideDown(child)
        } else if (currentState != STATE_SCROLLED_UP && dyConsumed < 0) {
            slideUp(child)
        }
    }

    override fun slideUp(child: V, animated: Boolean) {
        if (animated) {
            slideUp(child)
        } else {
            currentState = STATE_SCROLLED_UP
            cancelAnimation(child)
            child.translationY = getSlideUpTargetY(child)
        }
    }

    override fun slideDown(child: V, animated: Boolean) {
        if (animated) {
            slideDown(child)
        } else {
            currentState = STATE_SCROLLED_DOWN
            cancelAnimation(child)
            if (child.isAttachedToWindow) {
                child.translationY = getSlideDownTargetY(child)
            } else {
                child.doOnLayout {
                    if (currentState == STATE_SCROLLED_DOWN) {
                        child.translationY = getSlideDownTargetY(child)
                    }
                }
            }
        }
    }

    /**
     * Возникновение View путём передвижения вверх
     * @param child View, управляемая данным Behavior-ом
     */
    protected open fun slideUp(child: V) {
        cancelAnimation(child)

        currentState = STATE_SCROLLED_UP
        animateChildTo(
            child,
            getSlideUpTargetY(child),
            enterAnimationDuration,
            LinearOutSlowInInterpolator()
        )
    }

    /**
     * Исчезновение View путём передвижения вниз
     * @param child View, управляемая данным Behavior-ом
     */
    protected open fun slideDown(child: V) {
        cancelAnimation(child)

        currentState = STATE_SCROLLED_DOWN
        animateChildTo(
            child,
            getSlideDownTargetY(child),
            exitAnimationDuration,
            FastOutLinearInInterpolator()
        )
    }

    /** @SelfDocumented */
    protected open fun getBottomSpacing(child: V) = spacing

    /** @SelfDocumented */
    protected fun cancelAnimation(child: V) {
        currentAnimator?.run {
            cancel()
            child.clearAnimation()
        }
    }

    /**
     * Получение координаты Y верхней позиции, на которую нужно переместить [child].
     */
    protected open fun getSlideUpTargetY(child: V) = 0f

    /**
     * Получение координаты Y нижней позиции, на которую нужно переместить [child].
     */
    protected open fun getSlideDownTargetY(child: V) =
        ((height ?: child.measuredHeight) + getBottomSpacing(child)).toFloat()

    private fun animateChildTo(
        child: V,
        targetY: Float,
        duration: Long,
        interpolator: TimeInterpolator
    ) {
        isSliding = true
        currentAnimator = child.animate()
            .translationY(targetY)
            .setInterpolator(interpolator)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                    isSliding = false
                }
            })
    }

    protected companion object {
        const val STATE_SCROLLED_DOWN = 1
        const val STATE_SCROLLED_UP = 2
    }
}