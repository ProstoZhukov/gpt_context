package ru.tensor.sbis.design.navigation.view.view.fab.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.buttons.SbisFloatingButtonPanel

/**
 * @author ai.abramenko
 */

/**
 * Устанавливает поведение для панели с кнопками с возможностью ручного перемещения по оси "y"
 * [FloatingPanelBehavior.slideTo]
 */
fun SbisFloatingButtonPanel.installHideOnScrollBehavior() {
    updateLayoutParams<CoordinatorLayout.LayoutParams> {
        behavior = FloatingPanelBehavior(context)
    }
}

/**
 * Перемещает панель с кнопки по оси "y" на переданное значение.
 * Необходимо использовать вместо [SbisFloatingButtonPanel.setTranslationY] для поддержки дефолтных состояний анимации.
 */
fun SbisFloatingButtonPanel.slideTo(y: Float) {
    getHideOnScrollBehavior()?.slideTo(this, y) ?: run {
        translationY = y
    }
}

private fun SbisFloatingButtonPanel.getHideOnScrollBehavior(): FloatingPanelBehavior? {
    return (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? FloatingPanelBehavior
}

private class FloatingPanelBehavior @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : FabHideOnScrollBehavior(
    context,
    attrs
) {

    fun slideTo(child: View, y: Float) {
        /**
         * Если панель показана, то отменяем анимацию и устанавливаем translationY
         */
        if (child.translationY <= 0f) {
            cancelAnimation(child)
            child.translationY = y
        }
        child.setTag(R.id.navigation_floating_panel_translation_id, y.toInt())
    }

    override fun getSlideUpTargetY(child: View): Float {
        return super.getSlideUpTargetY(child) + getFloatingTranslationY(child)
    }

    override fun getSlideDownTargetY(child: View): Float {
        return super.getSlideDownTargetY(child) - getFloatingTranslationY(child)
    }

    private fun getFloatingTranslationY(child: View): Int {
        return child.getTag(R.id.navigation_floating_panel_translation_id) as? Int ?: 0
    }
}