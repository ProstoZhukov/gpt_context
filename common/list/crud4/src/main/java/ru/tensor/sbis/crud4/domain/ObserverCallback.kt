package ru.tensor.sbis.crud4.domain

import androidx.annotation.AnyThread
import ru.tensor.sbis.service.generated.DirectionStatus
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.Mark
import ru.tensor.sbis.service.generated.Selection
import ru.tensor.sbis.service.generated.SelectionCounter
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition
import java.util.ArrayList

/**
 * Колбек для crud4 коллекции контроллера.
 * Пример использования внизу файла.
 */
@AnyThread
interface ObserverCallback<ITEM_WITH_INDEX, ITEM, PATH_MODEL> {
    /**
     * Очистить данные списка.
     */
    fun onReset(p0: List<ITEM>)

    /**
     * Удалить элементы по индексу.
     */
    fun onRemove(p0: List<Long>)

    /**
     * Переместить элементы с позций по индекам на другие позиции по индекам.
     */
    fun onMove(p0: List<IndexPair>)

    /**
     * Добавить элементы по индексам.
     */
    fun onAdd(p0: List<ITEM_WITH_INDEX>)

    /**
     * Заменить элементы по индексам.
     */
    fun onReplace(p0: List<ITEM_WITH_INDEX>)

    /**
     * Добавить индикатор прогресса.
     */
    fun onAddThrobber(position: ViewPosition)

    /**
     * Скрыть индикаторы прогресса.
     */
    fun onRemoveThrobber()

    /**
     * Показать заглушку.
     */
    fun onAddStub(stubType: StubType, position: ViewPosition, message: String?)

    /**
     * Убрать заглушку.
     */
    fun onRemoveStub()

    /**
     * Путь изменился.
     */
    fun onPath(path: List<PATH_MODEL>)

    /**
     * Обновить доступность пагинации.
     */
    fun onEndUpdate(haveMore: DirectionStatus)

    /**
     * Пометить запись.
     */
    fun onMark(marked: Mark)

    /**
     * Выделить запись.
     */
    fun onSelect(selected: ArrayList<Selection>, counter :SelectionCounter)

    @Deprecated("Используй перегрузку с количеством выделенных элементов")
    fun onSelect(selected: ArrayList<Selection>)

    /**
     * Восстановить скролл при возврате в папку
     */
    fun onRestorePosition(pos: Long)
}

/**
 * Пример:
 * class HierarchyClientsServiceWrapper @Inject constructor(
 *     private val collectionProvider: HClientCollectionProvider,
 *     private val filter: ClientCollectionFilter,
 *     private val anhor: HierarchyPaginationOfHClientListViewModelHClientCollectionAnchor
 * ) : Wrapper<HierarchyCollectionOfHClientListViewModel, HierarchyCollectionObserverOfHClientListViewModel, ClientCollectionFilter,
 *     HierarchyPaginationOfHClientListViewModelHClientCollectionAnchor, ItemWithIndexOfDecoratedOfHClientListViewModel, DecoratedOfHClientListViewModel, PathModelOfHClientListViewModelMapOfStringString> {
 *
 *     override fun createEmptyFilter() = filter
 *
 *     override fun createPaginationAnchor(
 *         itemsOnPage: Long,
 *         directionType: DirectionType
 *     ): HierarchyPaginationOfHClientListViewModelHClientCollectionAnchor {
 *         with(anhor.pagination.first().pagination) {
 *             pageSize = itemsOnPage
 *             direction = directionType
 *         }
 *         return anhor
 *     }
 *
 *     override fun changeRoot(
 *         collection: HierarchyCollectionOfHClientListViewModel,
 *         pathModel: PathModelOfHClientListViewModelMapOfStringString?
 *     ) {
 *         collection.changeRoot(pathModel?.ident)
 *     }
 *
 *     override fun createCollectionObserver(observer: ObserverCallback<ItemWithIndexOfDecoratedOfHClientListViewModel, DecoratedOfHClientListViewModel, PathModelOfHClientListViewModelMapOfStringString>): HierarchyCollectionObserverOfHClientListViewModel {
 *         return HierarchyClientsCollectionObserver(observer)
 *     }
 *
 *
 *     override fun getIndex(itemWithIndex: ItemWithIndexOfDecoratedOfHClientListViewModel): Long {
 *         return itemWithIndex.index
 *     }
 *
 *     override fun createCollection(
 *         filter: ClientCollectionFilter,
 *         anchor: HierarchyPaginationOfHClientListViewModelHClientCollectionAnchor
 *     ): HierarchyCollectionOfHClientListViewModel {
 *         return collectionProvider.get(filter, anchor)
 *     }
 *
 *     override fun setObserver(
 *         observer: HierarchyCollectionObserverOfHClientListViewModel,
 *         toCollection: HierarchyCollectionOfHClientListViewModel
 *     ) {
 *         toCollection.setObserver(observer)
 *     }
 *
 *     override fun goNext(collection: HierarchyCollectionOfHClientListViewModel, var1: Long) {
 *         collection.next(var1)
 *     }
 *
 *     override fun goPrev(collection: HierarchyCollectionOfHClientListViewModel, var1: Long) {
 *         collection.prev(var1)
 *     }
 *
 *     override fun refresh(collection: HierarchyCollectionOfHClientListViewModel) {
 *         collection.refresh()
 *     }
 *
 *     override fun dispose(collection: HierarchyCollectionOfHClientListViewModel) {
 *         collection.dispose()
 *     }
 *
 *     override fun getItem(itemWithIndex: ItemWithIndexOfDecoratedOfHClientListViewModel): DecoratedOfHClientListViewModel {
 *         return itemWithIndex.item
 *     }
 *
 *     override fun expand(collection: HierarchyCollectionOfHClientListViewModel, pos: Long) {
 *         collection.expand(pos)
 *     }
 *
 *     override fun collapse(collection: HierarchyCollectionOfHClientListViewModel, pos: Long) {
 *         collection.collapse(pos)
 *     }
 *
 *     override fun mark(collection: HierarchyCollectionOfHClientListViewModel, pos: Long) {
 *         collection.mark(pos)
 *     }
 *
 *     override fun select(collection: HierarchyCollectionOfHClientListViewModel, pos: Long) {
 *         collection.select(pos)
 *     }
 * }
 */