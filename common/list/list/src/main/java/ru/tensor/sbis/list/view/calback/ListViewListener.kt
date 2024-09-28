package ru.tensor.sbis.list.view.calback

import androidx.annotation.AnyThread

/**
 * Слушатель команд компонента списка о запросе на подгрузку или обновления данных.
 */
interface ListViewListener {

    /**
     * Загрузить предыдущую страницу с данными.
     */
    @AnyThread
    fun loadPrevious()

    /**
     * Загрузить следующую страницу с данными.
     */
    @AnyThread
    fun loadNext()
}
