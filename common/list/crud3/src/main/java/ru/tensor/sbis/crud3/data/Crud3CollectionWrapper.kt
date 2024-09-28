package ru.tensor.sbis.crud3.data

import ru.tensor.sbis.crud3.ComponentViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.DirectionType

/**
 * Обертка для конкретной реализации CRUD3 коллекции контроллера.
 * Реализация должна быть ровно такая как в примерах к методам, только с нужным микросервисом.
 * Описание аргументов см в [ComponentViewModel].
 */
interface Crud3CollectionWrapper<COLLECTION, COLLECTION_OBSERVER, FILTER, PAGINATION_ANCHOR, ITEM_WITH_INDEX, ITEM> {

    /**
     * Пример реализации конструктора:
     * class Crud3LogServiceWrapper constructor(
     *      private val controllerCollection: DependencyProvider<LogCollectionProvider>
     * ) : Crud3Wrapper<CollectionOfLogPackageViewModel, Crud3CollectionObserver, LogFilter, PaginationOfLogAnchor, ItemWithIndexOfLogPackageViewModel, LogPackageViewModel> {
     */

    /**
     * Создать инициализируюший фильтр для коллекции.
     * Пример:
     * override fun createFilter() = LogFilter()
     */
    fun createEmptyFilter(): FILTER

    /**
     * Создать "якорь" для пагинации.
     * Пример:
     * override fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType) =
     *      PaginationOfLogAnchor(
     *          null,
     *          directionType,
     *          itemsOnPage
     *      )
     */
    fun createPaginationAnchor(itemsOnPage: Long, directionType: DirectionType): PAGINATION_ANCHOR

    /**
     * Создать коллекцию с использованием фильтра и "якоря".
     * Пример:
     *  override fun createCollection(
     *      filter: LogFilter,
     *      anchor: PaginationOfLogAnchor
     *  ): CollectionOfLogPackageViewModel {
     *      return controllerCollection.get().get(
     *          filter,
     *          anchor
     *      )
     *  }
     */
    fun createCollection(
        filter: FILTER,
        anchor: PAGINATION_ANCHOR
    ): COLLECTION

    /**
     * Создать обозревателя коллекции с использованием колбека [observer].
     * Пример:
     *  override fun createCollectionObserver(
     *      observer: Crud3ObserverCallback<ItemWithIndexOfLogPackageViewModel, LogPackageViewModel>
     *  ) = Crud3CollectionObserver(observer)
     *
     *  См. так же [ObserverCallback]
     */
    fun createCollectionObserver(observer: ObserverCallback<ITEM_WITH_INDEX, ITEM>): COLLECTION_OBSERVER

    /**
     * Передать обозреватель коллекции [observer] в коллекцию [toCollection]
     * Пример:
     * override fun setObserver(
     *      observer: Crud3CollectionObserver,
     *      toCollection: CollectionOfLogPackageViewModel
     * ) = toCollection.setObserver(observer)
     */
    fun setObserver(
        observer: COLLECTION_OBSERVER,
        toCollection: COLLECTION
    )

    /**
     * Запросить следующую страницу.
     * Пример:
     * override fun goNext(collection: CollectionOfLogPackageViewModel, var1: Long) {
     *      collection.next(var1)
     * }
     */
    fun goNext(collection: COLLECTION, var1: Long)

    /**
     * Запросить предыдущую страницу.
     * Пример:
     * override fun goPrev(collection: CollectionOfLogPackageViewModel, var1: Long) {
     *     collection.prev(var1)
     * }
     */
    fun goPrev(collection: COLLECTION, var1: Long)

    /**
     * Запросить повторное получение данных с тем же фильтром.
     * Пример:
     * override fun refresh(collection: CollectionOfLogPackageViewModel) {
     *     collection.refresh()
     * }
     */
    fun refresh(collection: COLLECTION)

    /**
     * Освободить ресурсы коллекции.
     * Пример:
     * override fun dispose(collection: CollectionOfLogPackageViewModel) {
     *    collection.dispose()
     * }
     */
    fun dispose(collection: COLLECTION)

    /**
     * Определение нулевой страницы с контроллера. Частный случай, если нужно загрузить список с середины и
     * должен быть доступен скролл вверх
     * Не использовать с FirstFactory
     */
    fun isZeroPage(items: List<ITEM>): Boolean? = null
}