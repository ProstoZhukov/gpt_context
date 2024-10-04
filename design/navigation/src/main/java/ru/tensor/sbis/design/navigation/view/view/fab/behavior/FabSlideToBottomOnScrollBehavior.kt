package ru.tensor.sbis.design.navigation.view.view.fab.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior.BottomHideOnScroll
import ru.tensor.sbis.design.view_ext.FloatingButtonsBar

/**
 * Класс, определяющий поведение плавающей кнопки при скролле. В отличие от [FabHideOnScrollBehavior], не скрывает
 * кнопку, а прижимает к низу.
 *
 * @author us.bessonov
 */
class FabSlideToBottomOnScrollBehavior @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : ParametrizedHideBottomViewOnScrollBehavior<View>(
    context,
    attrs
),
    BottomHideOnScroll<View> {

    private var additionalTranslationY = 0f

    override var enabled: Boolean = true

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return when (dependency) {
            is Snackbar.SnackbarLayout,
            is FloatingButtonsBar -> true

            else -> super.layoutDependsOn(parent, child, dependency)
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean =
        if (dependency.isVisible) {
            if (dependency is Snackbar.SnackbarLayout) {
                val height = dependency.height.toFloat()
                if (!isSliding) {
                    val newTranslation = dependency.translationY - height
                    updateAdditionalTranslationY(newTranslation, child)
                } else {
                    additionalTranslationY = -height
                    false
                }
            } else shiftChildUp(child, dependency)
        } else false

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        cancelAnimation(child)
        shiftChildDown(child)
    }

    override fun slideDown(child: View) {
        if (enabled) {
            super.slideDown(child)
        }
    }

    override fun slideUp(child: View) {
        if (enabled) {
            super.slideUp(child)
        }
    }

    public override fun getSlideDownTargetY(child: View) =
        getBottomSpacing(child).toFloat() + additionalTranslationY

    public override fun getSlideUpTargetY(child: View) =
        super.getSlideUpTargetY(child) + additionalTranslationY

    private fun shiftChildUp(child: View, dependency: View): Boolean {
        return updateAdditionalTranslationY(-dependency.measuredHeight.toFloat(), child)
    }

    private fun shiftChildDown(child: View): Boolean {
        return updateAdditionalTranslationY(0f, child)
    }

    private fun updateAdditionalTranslationY(translation: Float, child: View): Boolean {
        additionalTranslationY = translation
        val newTranslation = when (currentState) {
            STATE_SCROLLED_DOWN -> getSlideDownTargetY(child)
            else -> getSlideUpTargetY(child)
        }
        if (child.translationY == newTranslation) return false
        child.translationY = newTranslation
        return true
    }
}