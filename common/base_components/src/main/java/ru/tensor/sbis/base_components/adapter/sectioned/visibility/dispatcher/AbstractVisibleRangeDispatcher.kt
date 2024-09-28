package ru.tensor.sbis.base_components.adapter.sectioned.visibility.dispatcher

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import ru.tensor.sbis.base_components.adapter.sectioned.visibility.VisibleRangeObserver
import timber.log.Timber

/**
 * Абстрактный класс для отслеживания области видимости элементов присоединенного [RecyclerView].
 *
 * @author am.boldinov
 */
abstract class AbstractVisibleRangeDispatcher(
        recyclerView: RecyclerView? = null
) : RecyclerView.OnScrollListener(), View.OnLayoutChangeListener {

    /**
     * Коллекция наблюдателей за изменениями области видимости.
     */
    private val observers = HashSet<VisibleRangeObserver>(3)

    /**
     * Экземпляр [RecyclerView], к которому присоединен диспетчер.
     */
    private var recyclerView: RecyclerView? = null

    /**
     * Кешированная позиция первого видимого элемента.
     */
    private var first = RecyclerView.NO_POSITION

    /**
     * Кешированная позиция последнего видимого элемента.
     */
    private var last = RecyclerView.NO_POSITION

    init {
        if (recyclerView != null) {
            attach(recyclerView)
        }
    }

    // region Public interface

    /**
     * Присоединить диспетчер к экземпляру [RecyclerView].
     *
     * @param recyclerView - экземпляр RecyclerView за областью видимости элементов которого необходимо следить
     */
    @UiThread
    fun attach(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.addOnLayoutChangeListener(this)
        recyclerView.addOnScrollListener(this)
        // Обновляем область видимости для того, чтобы узнать текущую область видимости
        updateVisibleRange(0, true)
    }

    /**
     * Отсоединить диспетчер от [RecyclerView]. Если жизненный цикл
     * экземпляра диспетчера больше жизненного цикла [RecyclerView],
     * к которому он присоединен, необходимо вызывать данный метод
     * для предотвращения утечки памяти.
     */
    @UiThread
    fun detach() {
        recyclerView?.let {
            it.removeOnLayoutChangeListener(this)
            it.removeOnScrollListener(this)
        }
        recyclerView = null
        // Сбрасываем кешированные значения области видимости
        updateVisibleRange(0, true)
    }

    /**
     * Добавить наблюдателя за областью видимости.
     */
    @UiThread
    fun addObserver(observer: VisibleRangeObserver) {
        if (observers.add(observer)) {
            // Уведомляем наблюдателя о текущей области видимости
            observer.onVisibleRangeChanged(first, last, 0)
        }
    }

    /**
     * Удалить наблюдателя за областью видимости.
     */
    fun removeObserver(observer: VisibleRangeObserver) {
        if (!observers.remove(observer)) {
            Timber.w("Observer $observer not found.")
        }
    }

    // endregion

    // region View.OnLayoutChangeListener impl

    @CallSuper
    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
                                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        // Изменился макет, обновляем область видимости
        updateVisibleRange(0, true)
    }

    // endregion

    // region RecyclerView.OnScrollListener() impl

    @CallSuper
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // Изменилось значение скролла, обновляем область видимости
        updateVisibleRange(dy, false)
    }

    // endregion

    /**
     * Получить позицию первого видимого элемента.
     */
    protected abstract fun getFirstVisible(layoutManager: RecyclerView.LayoutManager): Int

    /**
     * Получить позицию последнего видимого элемента.
     */
    protected abstract fun getLastVisible(layoutManager: RecyclerView.LayoutManager): Int

    /**
     * Обновить границы области видимости на основании текущего состояния [RecyclerView].
     *
     * @param direction - направление в котором изменяется область видимости
     */
    @UiThread
    private fun updateVisibleRange(direction: Int, force: Boolean) {
        var newFirst = RecyclerView.NO_POSITION
        var newLast = RecyclerView.NO_POSITION
        recyclerView?.layoutManager?.let { manager ->
            // Получаем новые рамки из layout manager
            newFirst = getFirstVisible(manager)
            newLast = getLastVisible(manager)
        }
        if (force || newFirst != first || newLast != last) {
            first = newFirst
            last = newLast
            // Уведомляем об изменениях
            notifyVisibleRangeChanged(direction)
        }
    }

    /**
     * Уведомить наблюдателей об изменении региона видимости.
     *
     * @param direction - направление в котором изменяется область видимости
     */
    @UiThread
    private fun notifyVisibleRangeChanged(direction: Int) {
        for (observer in observers) {
            observer.onVisibleRangeChanged(first, last, direction)
        }
    }

}

/**
 * Вспомогательный метод для возможности указать лямбду в качестве наблюдателя.
 */
inline fun AbstractVisibleRangeDispatcher.addObserver(crossinline observer: (Int, Int) -> Unit): VisibleRangeObserver {
    val instance = object : VisibleRangeObserver {
        override fun onVisibleRangeChanged(first: Int, last: Int, direction: Int) {
            observer(first, last)
        }
    }
    addObserver(instance)
    return instance
}