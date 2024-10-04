package ru.tensor.sbis.design.folders.support.listeners

/**
 * Слушатель обновления данных
 *
 * @author ma.kolpakov
 */
fun interface FoldersDataUpdateListener {

    /**
     * Данные обновлены.
     * Вызывается при получении папок и\или дополнительной команды.
     *
     * @param isEmpty пусты ли дынные. true - если нет ни папок, ни дополнительной команды
     */
    fun updated(isEmpty: Boolean)
}
