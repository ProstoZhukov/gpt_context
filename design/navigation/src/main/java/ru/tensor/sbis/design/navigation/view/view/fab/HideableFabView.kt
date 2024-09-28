package ru.tensor.sbis.design.navigation.view.view.fab

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView
import ru.tensor.sbis.design.navigation.view.view.fab.behavior.FabHideOnScrollBehavior
import ru.tensor.sbis.design.navigation.view.view.fab.behavior.SlideableChildBehavior
import ru.tensor.sbis.design.utils.checkNotNullSafe
import timber.log.Timber

/**
 * Класс для управления кнопкой создания (плавающей кнопкой).
 * Делегирует методы [hide] и [show] в объект класса [FabHideOnScrollBehavior],
 * который реализует анимацию скрытия и возникновения кнопки.
 * Так как класс реализует интерфейс [HideableNavigationView],
 * его можно использовать совместно с ru.tensor.sbis.common.navigation.TabNavScrollHelper.
 *
 * @property fabProvider кнопка создания (плавающая кнопка)
 * @property fabBottomSpacing расстояние от кнопки до нижней границы экрана
 *
 * @author ma.kolpakov
 */
@Suppress("unused")
class HideableFabView(
    private val fabProvider: () -> View?,
    private val fabBottomSpacing: Int
) : HideableNavigationView {

    @Deprecated("Используйте primary конструктор")
    constructor(
        fab: View,
        fabBottomSpacing: Int
    ) : this({ fab }, fabBottomSpacing)

    private var behavior: SlideableChildBehavior<View>? = null

    override var pinned = false

    override fun hide(animated: Boolean) {
        if (!pinned) {
            fabProvider()?.let {
                getBehavior()?.slideDown(it, animated)
                    ?: hideForced()
            }
        }
    }

    override fun show(animated: Boolean) {
        if (!pinned) {
            fabProvider()?.let {
                getBehavior()?.slideUp(it, animated)
                    ?: showForced()
            }
        }
    }

    private fun hideForced() {
        val fab = fabProvider() ?: return
        fab.translationY = (fab.measuredHeight + fabBottomSpacing).toFloat()
    }

    private fun showForced() {
        val fab = fabProvider() ?: return
        fab.translationY = 0F
    }

    private fun getBehavior(): SlideableChildBehavior<View>? {
        return behavior ?: tryToGetBehavior(fabProvider)
            ?.apply { spacing = fabBottomSpacing }
            ?.also { behavior = it }
    }

    private fun tryToGetBehavior(fabProvider: () -> View?): SlideableChildBehavior<View>? = try {
        val fab = fabProvider()
        checkNotNullSafe(fab) {
            "Плавающая кнопка null"
        }
        check(fab?.parent is CoordinatorLayout) {
            "Плавающая кнопка должна располагаться внутри CoordinatorLayout-а для осуществления анимации"
        }
        val behavior = checkNotNull((fab?.layoutParams as CoordinatorLayout.LayoutParams).behavior) {
            "Необходимо задать для плавающей кнопки Behavior для осуществления анимации"
        }
        @Suppress("UNCHECKED_CAST")
        checkNotNull(behavior as? SlideableChildBehavior<View>) {
            "Behavior должен быть экземпляром ${SlideableChildBehavior<View>::javaClass.name}"
        }
    } catch (e: IllegalStateException) {
        Timber.w(e)
        null
    }
}