package ru.tensor.sbis.design.container.locator.watcher

/**
 * Этот интерфейс необходимо реализовать в прикладных ViewHolder для поддержки отображения контейнера относительно вызывающего элемента внутри списка.
 * он должен предоставлять уникальный id элемента списка. Например UUID из модели. Для некликабельных элементов реализация интерфейса не требуется.
 * @author ma.kolpakov
 */
interface ItemIdProvider {
    fun getId(): String
}