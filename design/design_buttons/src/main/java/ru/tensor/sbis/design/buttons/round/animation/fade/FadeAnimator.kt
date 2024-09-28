package ru.tensor.sbis.design.buttons.round.animation.fade

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import com.google.android.material.R
import com.google.android.material.animation.MotionSpec
import ru.tensor.sbis.design.buttons.SbisRoundButton

/**
 * Аниматор исчезновения и появления кнопки.
 * Анимация представляет собой плавное сжатие кнопки в точку с последующим visibility == GONE. Появление - то же самое,
 * но в обратную сторону. Сжатие и растяжение работает на параметрах [View.scaleX] и [View.scaleY].
 *
 * [По задаче](https://online.sbis.ru/opendoc.html?guid=13b3a727-e70c-4992-9cae-b2a1d5a99aba&client=3)
 *
 * @author ra.geraskin
 */
internal class FadeAnimator(private val button: SbisRoundButton) {

    private var currentAnimator: Animator? = null
    private var typeAnimation: Int = NO_ANIMATION
    private var defaultHideMotionSpec: MotionSpec? = null
    private var defaultShowMotionSpec: MotionSpec? = null
    private var cancelled = false

    /**
     * @SelfDocumented
     */
    internal fun hide(animated: Boolean) {
        if (button.visibility != View.VISIBLE) return
        if (typeAnimation == HIDE_ANIMATION) return
        currentAnimator?.cancel()
        if (animated && shouldAnimateVisibilityChange()) {
            createAnimator(getDefaultHideMotionSpec(), HIDE_SCALE).apply {
                addListener(
                    onStart = {
                        currentAnimator = it
                        typeAnimation = HIDE_ANIMATION
                        cancelled = false
                    },
                    onCancel = {
                        cancelled = true
                    },
                    onEnd = {
                        currentAnimator = null
                        typeAnimation = NO_ANIMATION
                        if (!cancelled) {
                            button.visibility = View.GONE
                        }
                    }
                )
                start()
            }
        } else {
            button.visibility = View.GONE
        }
    }

    /**
     * @SelfDocumented
     */
    internal fun show(animated: Boolean) {
        if (button.visibility != View.VISIBLE) {
            button.scaleX = HIDE_SCALE
            button.scaleY = HIDE_SCALE
        }

        if (typeAnimation == SHOW_ANIMATION) return
        currentAnimator?.cancel()

        if (animated && shouldAnimateVisibilityChange()) {
            createAnimator(getDefaultShowMotionSpec(), SHOW_SCALE).apply {
                addListener(
                    onStart = {
                        button.visibility = View.VISIBLE
                        currentAnimator = it
                        typeAnimation = SHOW_ANIMATION
                    },
                    onEnd = {
                        currentAnimator = null
                        typeAnimation = NO_ANIMATION
                    }
                )
                start()
            }
        } else {
            button.scaleY = SHOW_SCALE
            button.scaleX = SHOW_SCALE
            button.visibility = View.VISIBLE
        }
    }

    private fun shouldAnimateVisibilityChange(): Boolean {
        return ViewCompat.isLaidOut(button) && !button.isInEditMode
    }

    private fun createAnimator(spec: MotionSpec, scale: Float): AnimatorSet {
        val animators: MutableList<Animator> = ArrayList()
        val animatorScaleX: ObjectAnimator = ObjectAnimator.ofFloat(button, View.SCALE_X, scale)
        spec.getTiming("scale").apply(animatorScaleX)
        animators.add(animatorScaleX)
        val animatorScaleY: ObjectAnimator = ObjectAnimator.ofFloat(button, View.SCALE_Y, scale)
        spec.getTiming("scale").apply(animatorScaleY)
        animators.add(animatorScaleY)
        val set = AnimatorSet()
        set.playTogether(animators)
        return set
    }

    private fun getDefaultHideMotionSpec(): MotionSpec {
        if (defaultHideMotionSpec == null) {
            defaultHideMotionSpec = MotionSpec.createFromResource(
                button.context,
                R.animator.design_fab_hide_motion_spec
            )
        }
        return checkNotNull(defaultHideMotionSpec)
    }

    private fun getDefaultShowMotionSpec(): MotionSpec {
        if (defaultShowMotionSpec == null) {
            defaultShowMotionSpec = MotionSpec.createFromResource(
                button.context,
                R.animator.design_fab_show_motion_spec
            )
        }
        return checkNotNull(defaultShowMotionSpec)
    }

}

private const val HIDE_SCALE = 0f
private const val SHOW_SCALE = 1f
private const val HIDE_ANIMATION = 0
private const val SHOW_ANIMATION = 1
private const val NO_ANIMATION = -1