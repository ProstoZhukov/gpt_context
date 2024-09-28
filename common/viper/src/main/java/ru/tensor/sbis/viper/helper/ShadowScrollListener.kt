package ru.tensor.sbis.viper.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.utils.AnimationUtil
import ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.headeritems.HeaderItemDecoration
import java.lang.ref.WeakReference

/**
 * Имплементация листенера для затенения списков при скролинге
 * @param shadowView - вьха с тенью
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ShadowScrollListener(private val shadowView: View) : RecyclerView.OnScrollListener() {

    private var shadowEnabled = true
        set(value) {
            field = value
            if (value.not()) shadowView.isInvisible = true
        }

    private var animatorRef: WeakReference<ViewPropertyAnimator>? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val toInvisible = recyclerView.isShadowOnScrollEnabled().not()
        if (toInvisible != shadowView.isInvisible && (toInvisible || (toInvisible.not() && shadowEnabled))) {
            animatorRef?.get()?.cancel()
            val animator = shadowView.animate()
                ?.setDuration(AnimationUtil.ANIMATION_DURATION)
                ?.setInterpolator(FastOutLinearInInterpolator())
            animatorRef = WeakReference(animator)

            // Сохраним альфу вью и восстановим значение после окончания анимации
            val startAlpha = shadowView.alpha

            shadowView.alpha = if (toInvisible) startAlpha else 0F

            animatorRef?.get()?.alpha(if (toInvisible) 0F else 1F)
                ?.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        shadowView.isVisible = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        shadowView.isInvisible = toInvisible
                        shadowView.alpha = startAlpha
                    }
                })
            animatorRef?.get()?.start()
        }
    }

    /**@SelfDocumented*/
    fun withHeaderItemDecoration(headerItemDecoration: HeaderItemDecoration): ShadowScrollListener {
        headerItemDecoration.let {
            it.addOnHeaderAttachedListener { shadowEnabled = false }
            it.addOnHeaderDetachedListener { shadowEnabled = true }
            it.withShadowOnlyOnScroll()
        }
        return this
    }
}

/**
 * Нужно ли рисовать тень при скролле
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun RecyclerView.isShadowOnScrollEnabled() = computeVerticalScrollOffset() > 0

/**
 * Заголовок с тенью
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun HeaderItemDecoration.withShadowOnlyOnScroll() {
    parent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            shadowEnabled = recyclerView.isShadowOnScrollEnabled()
        }
    })
}