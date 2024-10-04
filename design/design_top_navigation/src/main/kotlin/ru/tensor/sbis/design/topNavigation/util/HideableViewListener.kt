package ru.tensor.sbis.design.topNavigation.util

import android.animation.Animator
import android.view.View
import androidx.core.view.isVisible

/**
 * Слушатель, меняющий видимость view в конце анимации.
 *
 * @author da.zolotarev
 */
internal class HideableViewListener(val view: View, val isVisible: Boolean) : Animator.AnimatorListener {

    override fun onAnimationStart(animation: Animator) = Unit

    override fun onAnimationEnd(animation: Animator) {
        view.isVisible = isVisible
    }

    override fun onAnimationCancel(animation: Animator) = Unit

    override fun onAnimationRepeat(animation: Animator) = Unit
}