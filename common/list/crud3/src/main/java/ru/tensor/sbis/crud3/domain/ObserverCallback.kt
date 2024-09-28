package ru.tensor.sbis.crud3.domain

import androidx.annotation.AnyThread
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Колбек для CRUD3 коллекции контроллера.
 * Пример использования внизу файла.
 */
@AnyThread
interface ObserverCallback<ITEM_WITH_INDEX, ITEM> {
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
    fun onAddStub(stubType: StubType, position: ViewPosition)

    /**
     * Убрать заглушку.
     */
    fun onRemoveStub()
}

/**
 * Пример:
 * class Crud3CollectionObserver(private val callback: Crud3ObserverCallback<ItemWithIndexOfLogPackageViewModel, LogPackageViewModel>) :
CollectionObserverOfLogPackageViewModel() {

override fun onReset(p0: ArrayList<LogPackageViewModel>) {
callback.onReset(p0)
}

override fun onRemove(p0: ArrayList<Long>) {
callback.onRemove(p0)
}

override fun onMove(p0: ArrayList<IndexPair>) {
callback.onMove(p0)
}

override fun onAdd(p0: ArrayList<ItemWithIndexOfLogPackageViewModel>) {
callback.onAdd(p0)
}

override fun onReplace(p0: ArrayList<ItemWithIndexOfLogPackageViewModel>) {
callback.onReplace(p0)
}

override fun onBeginUpdate() = Unit

override fun onEndUpdate() = Unit

override fun onAddThrobber(position: ThrobberPosition) {
callback.onAddThrobber(position)
}

override fun onRemoveThrobber() {
callback.onRemoveThrobber()
}

override fun onAddStub(stubType: StubType) {
callback.onAddStub(stubType)
}

override fun onRemoveStub() {
callback.onRemoveStub()
}
}
 *
 */