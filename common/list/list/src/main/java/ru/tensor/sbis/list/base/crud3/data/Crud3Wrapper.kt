package ru.tensor.sbis.list.base.crud3.data

/**
 * Реализация интерфейса должна быть простой оберткой над микросервисом контроллером, реализующим обозреваемую коллекцию.
 */
@Deprecated("Используй модуль crud3")
interface Crud3Wrapper<COLLECTION_VIEW_MODEL, ITEM, FILTER> {

    /**
     * Создать коллекцию через микросервис с фильтром [filter].
     *
     * Пример:
     * controllerCollection.get().get(filter)
     */
    fun createCollection(filter: FILTER): COLLECTION_VIEW_MODEL

    /**
     * Установить наблюдатель [observer] коллекции в микросервис [collection].
     *Пример:
     * collection.setObserver(CollectionObserver(observer))
     */
    fun setObserver(collection: COLLECTION_VIEW_MODEL, observer: Crud3ObserverCallback<ITEM>)

    /**
     * Очистить ссылку на наблюдатель коллекции в микросервисе [collection].
     * Пример:
     * collection.setObserver(null)
     */
    fun removeObserver(collection: COLLECTION_VIEW_MODEL)

    fun goNext(collection: COLLECTION_VIEW_MODEL)

    fun goPrev(collection: COLLECTION_VIEW_MODEL)

    /**
     * Очистить ресурсы коллекции на контроллере
     */
    fun dispose(collection: COLLECTION_VIEW_MODEL)
}