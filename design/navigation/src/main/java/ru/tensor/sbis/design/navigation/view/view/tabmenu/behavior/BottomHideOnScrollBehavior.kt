package ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import ru.tensor.sbis.design.navigation.view.view.fab.behavior.ParametrizedHideBottomViewOnScrollBehavior
import ru.tensor.sbis.design.view_ext.FloatingButtonsBar

/**
 * Класс определяет поведение, которое размещает [Snackbar] и [FloatingButtonsBar] над нижней,
 * скрывающейся при прокрутке панелью.
 *
 * @author ma.kolpakov
 */
class BottomHideOnScrollBehavior<V : View> @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : ParametrizedHideBottomViewOnScrollBehavior<V>(context, attrs), BottomHideOnScroll<V> {

    override var enabled: Boolean = true

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
        if (params.insetEdge == Gravity.NO_GRAVITY) {
            // по умолчанию установим отталкивание вверх при появлении
            params.insetEdge = Gravity.BOTTOM
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        return when (dependency) {
            is Snackbar.SnackbarLayout -> anchorDependencyToTop(child, dependency).run { false }
            is FloatingButtonsBar -> anchorDependencyToTop(child, dependency).run { false }
            else -> super.layoutDependsOn(parent, child, dependency)
        }
    }

    override fun slideDown(child: V) {
        if (enabled) {
            super.slideDown(child)
        }
    }

    override fun slideUp(child: V) {
        if (enabled) {
            super.slideUp(child)
        }
    }

    /**
     * Метод помещает зависимый объект наверх.
     */
    private fun anchorDependencyToTop(child: View, dependency: View) {
        when (val layoutParams = dependency.layoutParams) {
            is CoordinatorLayout.LayoutParams -> {
                layoutParams.anchorId = child.id
                layoutParams.anchorGravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                dependency.layoutParams = layoutParams
            }
        }
    }
}