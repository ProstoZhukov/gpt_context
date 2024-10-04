package ru.tensor.sbis.design.buttons.base.models.offset

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.buttons.base.utils.behavior.FloatingViewBehavior
import ru.tensor.sbis.design.buttons.base.utils.behavior.SnackBarInfoBehavior
import kotlin.math.absoluteValue

/**
 * Реализация [SbisViewOffsetBehavior] интерфейса.
 *
 * Компонент должен предоставляться снаружи через [initSbisView] метод.
 *
 * @author mb.kruglova
 */
internal class SbisViewOffsetBehaviorImpl : SbisViewOffsetBehavior {
    private lateinit var view: View

    private val floatingBehavior by lazy(LazyThreadSafetyMode.NONE) {
        (view.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
            ?: FloatingViewBehavior()
    }

    private val snackBarInfo by lazy(LazyThreadSafetyMode.NONE) {
        floatingBehavior as? SnackBarInfoBehavior
    }

    /**
     * Инициализация компонента.
     */
    fun initSbisView(view: View) {
        this.view = view
    }

    override fun checkOffsetTopAndBottomInability(offset: Int): Boolean {
        val offsetValue = offset.absoluteValue
        val info = snackBarInfo
        return info != null && (info.snackBarHeight == offsetValue || info.snackBarBottom == offsetValue)
    }

    override fun getSbisViewBehavior(): CoordinatorLayout.Behavior<*> = floatingBehavior
}