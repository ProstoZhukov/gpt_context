package ru.tensor.sbis.swipeablelayout.viewpool

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.view_factory.SimpleXmlViewFactory
import ru.tensor.sbis.objectpool.impl.ViewFactoryConcurrentObjectPool
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import java.util.LinkedList

/**
 * Пул элементов списка со свайп-меню.
 * Обеспечивает возможность предварительного создания и переиспользования как непосредственно [View] элемента, так и
 * [View] пунктов свайп-меню
 *
 * @author us.bessonov
 */
class ItemViewPoolWithSwipeableLayout(
    context: Context,
    @LayoutRes private val layoutId: Int,
    @IdRes private val swipeableLayoutId: Int,
    private val swipeMenuViewPool: SwipeMenuViewPool,
    capacity: Int
) {

    private val viewPool = ViewFactoryConcurrentObjectPool(SimpleXmlViewFactory(layoutId, context), capacity)

    private val usedItems = LinkedList<View>()

    private lateinit var defaultLayoutParams: ViewGroup.LayoutParams

    /**
     * Возвращает готовый [View] из пула. В случае отсутствия готового, создаёт новый
     */
    fun take(): View {
        return viewPool.take()!!.also {
            usedItems.add(it)
            if (!::defaultLayoutParams.isInitialized) {
                defaultLayoutParams = ViewGroup.LayoutParams(it.layoutParams)
            }
            it.findViewById<SwipeableLayout>(swipeableLayoutId).setMenuItemViewPool(swipeMenuViewPool)
        }
    }

    /**
     * Добавляет в пулы новые элементы до максимальной заполненности
     */
    fun ensureRequiredViewCountInflated() {
        viewPool.inflate(viewPool.capacity)
        swipeMenuViewPool.inflate()
    }

    /**
     * Удаляет все [View] из пула элементов списка и пула пунктов меню
     */
    fun flush() {
        viewPool.flush()
        swipeMenuViewPool.flush()
        usedItems.clear()
    }

    /**
     * Возвращает в пул все использованные ранее элементы
     */
    fun releaseUsedItems() {
        usedItems.forEach {
            (it.parent as ViewGroup?)?.removeView(it)
            it.layoutParams = ViewGroup.LayoutParams(defaultLayoutParams)
            viewPool.put(it)
        }
        usedItems.clear()
    }

}
