package ru.tensor.sbis.list.base.data

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
class CollectionObserverCallback<ITEM>(private val callback: (List<ITEM>) -> Unit) {

    private val list = ArrayList<ITEM>()

    /**
     * Очистить данные списка.
     */
    fun onReset(p0: List<ITEM>) {
        list.clear()
        list.addAll(p0)
        callback(list)
    }

    /**
     * Удалить элементы по индексу.
     */
    fun onRemove(p0: List<Long>) {
        p0.forEach {
            list.removeAt(it.toInt())
        }
        callback(list)
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
        callback(list)
    }

    fun onAdd(p0: List<Pair<Long, ITEM>>) {
        p0.forEach {
            list.add(it.first.toInt(), it.second)
        }
        callback(list)
    }

    fun onReplace(p0: List<Pair<Long, ITEM>>) {
        p0.forEach {
            list[it.first.toInt()] = it.second
        }
        callback(list)
    }
}