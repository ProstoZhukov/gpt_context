package ru.tensor.sbis.crud4.view.viewmodel

import ru.tensor.sbis.service.DecoratedProtocol

/**
 * Делегат для выполнения основных операций с ячейкой.
 */
interface ItemActionDelegate<SOURCE_ITEM : DecoratedProtocol<IDENTIFIER>, IDENTIFIER> {

    /**
     * Прикладное действие вызов отправит соответствующие событие во вью-модель коллекции где его можно будет обработь.
     */
    fun itemClick(item: SOURCE_ITEM)

    /**
     * Вызов этого метода развернет/свернет папку.
     */
    fun expandFolderClick(item: SOURCE_ITEM)

    /**
     * Вызов этого метода развернет папку.
     */
    fun expandFolderClick(item: Long)

    /**
     * Вызов этого метода осуществит переход в папку (проваливание).
     */
    fun openFolderClick(item: SOURCE_ITEM)

    /**
     * Вызов этого метода выделит элемент или папку.
     */
    fun selectClick(item: SOURCE_ITEM)
}