package ru.tensor.sbis.design.navigation.view.view.fab.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

/**
 * Класс, определяющий поведение плавающей кнопки для [CoordinatorLayout] при скрытии/появлении ННП.
 *
 * @author ma.kolpakov
 */
class BottomNavigationFABBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<View>(
    context,
    attrs
) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        child.translationY = 0f
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (dependency is Snackbar.SnackbarLayout && dependency.isVisible) {
            val oldTranslation = child.translationY
            val height = dependency.height.toFloat()
            val newTranslation = dependency.translationY - height
            child.translationY = newTranslation

            return oldTranslation != newTranslation
        }
        return false
    }
}