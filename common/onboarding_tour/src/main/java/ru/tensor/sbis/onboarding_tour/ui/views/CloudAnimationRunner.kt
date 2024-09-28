package ru.tensor.sbis.onboarding_tour.ui.views

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.databinding.OnboardingTourFragmentBinding as Binding

/**
 * Контроллер управления анимацией плавающего синего облака.
 *
 * https://www.figma.com/proto/N8ztcntmCIRenioyYg3nEk/Onboarding?page-id=1%3A2&node-id=13-2363&scaling=min-zoom&starting-point-node-id=13%3A2363&hide-ui=1
 * http://axure.tensor.ru/MobileAPP/#g=1&p=%D1%87%D1%82%D0%BE_%D0%BD%D0%BE%D0%B2%D0%BE%D0%B3%D0%BE_
 *
 * @author ar.leschev
 */
class CloudAnimationRunner(rootView: View, animatedView: () -> View) {

    constructor(binding: Binding, animatedView: () -> View) : this(binding.root, animatedView)

    private var animator: ValueAnimator? = null
    private var defaultFrameDelay = 0L

    init {
        with(rootView) {
            val marginBottom = resources.getDimension(R.dimen.onboarding_tour_blur_shapes_margin_bottom)
            findViewTreeLifecycleOwner()?.let { owner -> setupCloudAnimation(owner, animatedView, marginBottom) }
        }
    }

    private fun setupCloudAnimation(
        lifecycleOwner: LifecycleOwner,
        animatedView: () -> View,
        marginBottom: Float
    ) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onCreate(owner: LifecycleOwner) {
                animator = initAnimator(animatedView, marginBottom)
            }

            override fun onResume(owner: LifecycleOwner) {
                animator?.start()
            }

            override fun onPause(owner: LifecycleOwner) {
                animator?.cancel()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                clearAnimator()
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    private fun initAnimator(animatedView: () -> View, marginBottom: Float): ValueAnimator? {
        if (ValueAnimator.getFrameDelay() < MAX_FRAME_DELAY) {
            defaultFrameDelay = ValueAnimator.getFrameDelay()
            ValueAnimator.setFrameDelay(MAX_FRAME_DELAY)
        }
        return ValueAnimator.ofFloat(0f, marginBottom).apply {
            addUpdateListener { newValue ->
                animatedView().translationY = -(newValue.animatedValue as Float)
            }
            interpolator = AccelerateDecelerateInterpolator()
            duration = ANIMATION_DURATION
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
    }

    private fun clearAnimator() {
        if (defaultFrameDelay != 0L) {
            ValueAnimator.setFrameDelay(defaultFrameDelay)
        }
        animator?.cancel()
        animator?.removeAllUpdateListeners()
        animator = null
    }

    private companion object {
        const val ANIMATION_DURATION = 3100L
        const val MAX_FRAME_DELAY = 250L
    }
}