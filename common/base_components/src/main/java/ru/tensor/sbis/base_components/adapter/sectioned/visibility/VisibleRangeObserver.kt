package ru.tensor.sbis.base_components.adapter.sectioned.visibility

import androidx.annotation.UiThread

/**
 * Интерфейс наблюдателя за видимым регионом.
 *
 * @author am.boldinov
 */
interface VisibleRangeObserver {

    /**
     * Обработать событие изменения видимого региона.
     *
     * @param first     - индекс первого видимого элемента
     * @param last      - индекс последнего видимого элемента
     * @param direction - направление в котором изменяется окно
     */
    @UiThread
    fun onVisibleRangeChanged(first: Int, last: Int, direction: Int)
}