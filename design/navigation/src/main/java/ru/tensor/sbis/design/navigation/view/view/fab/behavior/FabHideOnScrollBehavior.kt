package ru.tensor.sbis.design.navigation.view.view.fab.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior.BottomHideOnScroll
import ru.tensor.sbis.design.utils.extentions.getMargins
import ru.tensor.sbis.design.view_ext.FloatingButtonsBar

/**
 * Класс, определяющий поведение кнопки создания (плавающей кнопки) при скролле.
 * Переопределяет параметры анимации базового класса.
 *
 * @author ma.kolpakov
 * TODO: 4/10/2019 [Поведение будет переработано по задаче](https://online.sbis.ru/opendoc.html?guid=99d81a0b-a567-43be-b54a-139d5b77c163)
 */
open class FabHideOnScrollBehavior @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : ParametrizedHideBottomViewOnScrollBehavior<View>(
    context,
    attrs,
    enterAnimationDuration = 225, // значение подобрано эмпирически
    exitAnimationDuration = 300 // значение подобрано эмпирически
),
    BottomHideOnScroll<View> {
    private var dependentViewChanged = false

    override var enabled: Boolean = true

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return when (dependency) {
            is Snackbar.SnackbarLayout,
            is FloatingButtonsBar -> true

            else -> super.layoutDependsOn(parent, child, dependency)
        }
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (!dependentViewChanged) {
            dependentViewChanged = true
            shiftChildUp(child, dependency)
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        if (dependentViewChanged) {
            dependentViewChanged = false
            shiftChildDown(child)
        }
        super.onDependentViewRemoved(parent, child, dependency)
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

    override fun getBottomSpacing(child: View) = child.getMargins().bottom

    private fun shiftChildUp(child: View, dependency: View) {
        val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
        child.layoutParams = layoutParams.apply {
            bottomMargin += dependency.measuredHeight
        }
        if (currentState == STATE_SCROLLED_DOWN) {
            child.translationY += dependency.measuredHeight
        }
    }

    private fun shiftChildDown(child: View) {
        val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
        child.layoutParams = layoutParams.apply {
            // нецелевое использование. См. задачу по доработке в документации
            bottomMargin = spacing
        }
    }
}