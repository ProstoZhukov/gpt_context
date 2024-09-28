package ru.tensor.sbis.list.base.crud3.data

/**
 * Колбек коллекции микросервиса контроллера, аккумулирующий список элементов полученных из микросервиса
 * и отдающий его в переданный [callback].
 * Провизводит все необходимые операции над порциями данных для поддержания консистентности списка данных.
 *
 * Ипользуется как обертка над колбеком ммикросервиса:
 *  class CollectionObserver(private val callback: CollectionObserverCallback<LogPackageViewModel>) :
 *      ru.tensor.sbis.platform.logdelivery.generated.OldCollectionObserverOfLogPackageViewModel() {
 *
 *   override fun onReset(p0: ArrayList<LogPackageViewModel>) {
 *       callback.onReset(p0)
 *   }
 *
 *   override fun onRemove(p0: ArrayList<Long>) {
 *       callback.onRemove(p0)
 *   }
 *
 *   override fun onMove(p0: ArrayList<IndexPair>) {
 *    callback.onMove(p0.map { Pair(it.firstIndex, it.secondIndex) })
 *   }
 *
 *   override fun onAdd(p0: ArrayList<ItemWithIndexOfLogPackageViewModel>) {
 *       callback.onAdd(p0.map { Pair(it.index, it.item) })
 *   }
 *
 *   override fun onReplace(p0: ArrayList<ItemWithIndexOfLogPackageViewModel>) {
 *      callback.onReplace(p0.map { Pair(it.index, it.item) })
 *   }
 * }
 */
@Deprecated("Используй модуль crud3")
class Crud3ObserverCallback<ITEM>(private val callback: (Result<ITEM>) -> Unit) {

    private val list = ArrayList<ITEM>()

    /**
     * Очистить данные списка.
     */
    fun onReset(p0: List<ITEM>) {
        list.clear()
        list.addAll(p0)
        callback(Items(list))
    }

    /**
     * Удалить элементы по индексу.
     */
    fun onRemove(p0: List<Long>) {
        p0.forEach {
            list.removeAt(it.toInt())
        }
        callback(Items(list))
    }

    /**
     * Переместить элементы по с позций по индеку на другие позиции по индеку.
     */
    fun onMove(p0: List<Pair<Long, Long>>) {
        p0.forEach {
            val toInt = it.first.toInt()
            val item = list[toInt]
            list.removeAt(toInt)
            list.add(it.second.toInt(), item)
        }
        callback(Items(list))
    }

    fun onAdd(p0: List<Pair<Long, ITEM>>) {
        p0.forEach {
            list.add(it.first.toInt(), it.second)
        }
        callback(Items(list))
    }

    fun onReplace(p0: List<Pair<Long, ITEM>>) {
        p0.forEach {
            list[it.first.toInt()] = it.second
        }
        callback(Items(list))
    }


    fun onAddThrobber(position: ProgressPosition) {
        callback(AddProgress(position))
    }

    fun onRemoveThrobber() {
        callback(RemoveProgress())
    }

    fun onAddStub(stubType: StubType, position: ProgressPosition) {
        callback(Stub(stubType, position))
    }

    fun onRemoveStub() {
        callback(RemoveStub())
    }
}

@Deprecated("Используй модуль crud3")
enum class ProgressPosition {
    /** прогресс по месту  */
    IN_PLACE,

    /** прогресс в заголовке  */
    HEADER
}

@Deprecated("Используй модуль crud3")
enum class StubType {
    /** нет данных по дефолтному фильтру  */
    NO_DATA_STUB,

    /** нет данных по недефолтному фильтру  */
    BAD_FILTER_STUB,

    /** нет сети  */
    NO_NETWORK_STUB,

    /** серверная ошибка  */
    SERVER_TROUBLE
}

sealed class Result<ITEM>

data class Items<ITEM>(val value: List<ITEM>) : Result<ITEM>()

data class AddProgress<ITEM>(val position: ProgressPosition) : Result<ITEM>()
class RemoveProgress<ITEM> : Result<ITEM>()

data class Stub<ITEM>(val stub: StubType, val position: ProgressPosition) : Result<ITEM>()
class RemoveStub<ITEM> : Result<ITEM>()
