package ru.tensor.sbis.list.base.data

/**
 * Реализация интерфейса должна быть простой оберткой над микросервисом контроллером, реализующим обозреваемую коллекцию.
 */
interface ObservableCollectionWrapper<COLLECTION_VIEW_MODEL, ITEM, FILTER> {

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
    fun setObserver(collection: COLLECTION_VIEW_MODEL, observer: CollectionObserverCallback<ITEM>)

    /**
     * Очистить ссылку на наблюдатель коллекции в микросервисе [collection].
     * Пример:
     * collection.setObserver(null)
     */
    fun removeObserver(collection: COLLECTION_VIEW_MODEL)

    /**
     * Установить фильтр [filter] коллекции в микросервис [collection].
     *
     * collection.setFilter(filter)
     */
    fun setFilter(collection: COLLECTION_VIEW_MODEL, filter: FILTER)
}