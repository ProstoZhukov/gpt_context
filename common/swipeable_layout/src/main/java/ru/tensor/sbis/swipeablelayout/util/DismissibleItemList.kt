package ru.tensor.sbis.swipeablelayout.util

/**
 * Класс-обертка над списком элементов, позволяющий указывать элемент, который был удалён, но может быть восстановлен.
 *
 * Фактически удалённый элемент не представлен в списке и игнорируется при добавлении. В зависимости от производимых
 * изменений списка, изменяется позиция, на которую будет добавлен элемент в случае восстановления. Восстановлен может
 * быть только последний удалённый элемент, но при добавлении игнорируются все считающиеся удалёнными  элементы, для
 * которых удаление не было подтверждено вызовом [confirmItemDeletion].
 * При вызове [resetData] подтверждается удаление всех считавшихся удалёнными элементов, отсутствующих в новом списке.
 *
 * @param ITEM тип элемента списка
 * @param ID тип идентификатора элемента
 *
 * @author us.bessonov
 */
class DismissibleItemList<ITEM, ID> private constructor(
    private val getItemId: (ITEM) -> ID, private val list: MutableList<ITEM>
) : MutableList<ITEM> by list {

    /**
     * @param sourceList исходный список
     * @param getItemId лямбда для получения идентификатора элемента
     */
    @JvmOverloads
    constructor(
        sourceList: List<ITEM> = emptyList(), getItemId: (ITEM) -> ID
    ) : this(getItemId, sourceList.toMutableList())

    private var pendingItemId: ID? = null
    private var pendingItem: ITEM? = null
    private var restoredItemPosition = -1
    private val unconfirmedDeletedIds = mutableSetOf<ID>()

    /**
     * Задаёт id элемента, считающегося удалённым. Если он представлен в списке, то будет из него удалён, и при
     * изменении списка будет отслеживаться позиция для его восстановления
     *
     * @param itemId id удалённого элемента, коорый может быть восстановлен
     * @return индекс удалённого элемента или -1, если он отсутствует в списке
     */
    fun considerDeleted(itemId: ID): Int {
        val index = indexOfFirst(itemId).takeIf { it >= 0 } ?: return -1

        pendingItemId?.let {
            unconfirmedDeletedIds.add(it)
        }

        pendingItemId = itemId
        pendingItem = list[index]
        restoredItemPosition = index
        list.removeAt(index)

        return index
    }

    /**
     * Восстанавливает последний элемент, считающийся удалённым
     *
     * @return индекс успешно восстановленного элемента или -1, если он не был задан
     */
    fun restoreConsideredDeletedItem(): Int {
        pendingItem?.takeIf { restoredItemPosition in 0..size }?.let {
                list.add(restoredItemPosition, it)
                return restoredItemPosition.also { resetPendingItem() }
            }

        resetPendingItem()
        return -1
    }

    /**
     * Подтверждает удаление элемента
     *
     * @param itemId id удалённого элемента, присутствие которого в списке больше не требуется контролировать
     */
    fun confirmItemDeletion(itemId: ID) {
        if (pendingItemId == itemId) {
            resetPendingItem()
        }
        unconfirmedDeletedIds.remove(itemId)
    }

    /**
     * Заменяет текущий список на новый. При этом, подтверждается удаление элементов, отсутствующих в новом списке
     *
     * @param newList новый список
     */
    fun resetData(newList: List<ITEM>) {
        if (newList == this) return

        unconfirmedDeletedIds.toList().forEach { id ->
            if (newList.none { getItemId(it) == id }) {
                unconfirmedDeletedIds.remove(id)
            }
        }
        if (pendingItemId != null && newList.none { getItemId(it) == pendingItemId }) {
            resetPendingItem()
        }

        clear()
        addAll(newList)
    }

    private fun resetPendingItem() {
        pendingItemId = null
        pendingItem = null
        restoredItemPosition = -1
    }

    override fun add(element: ITEM): Boolean {
        add(size, element)
        return true
    }

    override fun add(index: Int, element: ITEM) {
        val id = getItemId(element)
        if (id == pendingItemId) {
            pendingItem = element
            return
        }
        if (unconfirmedDeletedIds.contains(id)) return

        if (index <= restoredItemPosition) {
            restoredItemPosition++
        }

        list.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<ITEM>): Boolean {
        val newItems = elements.filterNot {
            unconfirmedDeletedIds.contains(getItemId(it))
        }
        val pendingItem = newItems.firstOrNull { getItemId(it) == pendingItemId }

        if (pendingItem != null) {
            this.pendingItem = pendingItem
            restoredItemPosition = size + newItems.indexOf(pendingItem)
            return list.addAll(index, newItems.minus(pendingItem))
        }

        if (pendingItemId != null && index < restoredItemPosition) {
            restoredItemPosition += newItems.size
        }
        return list.addAll(index, newItems)
    }

    override fun addAll(elements: Collection<ITEM>): Boolean {
        return addAll(size, elements)
    }

    override fun remove(element: ITEM): Boolean {
        confirmItemDeletion(getItemId(element))
        indexOf(element).takeIf { it >= 0 }?.let {
                removeAt(it)
                return true
            }
        return false
    }

    override fun removeAll(elements: Collection<ITEM>): Boolean {
        confirmDeletion(elements)
        restoredItemPosition -= elements.count { indexOf(it) < restoredItemPosition }
        return list.removeAll(elements.toSet())
    }

    override fun removeAt(index: Int): ITEM {
        if (index < restoredItemPosition) {
            restoredItemPosition--
        }
        return list.removeAt(index)
    }

    override fun retainAll(elements: Collection<ITEM>): Boolean {
        val itemsToRemove = list.filter { !elements.contains(it) }
        return list.removeAll(itemsToRemove.toSet())
    }

    private fun indexOfFirst(itemId: ID): Int {
        return list.indexOfFirst { getItemId(it) == itemId }
    }

    private fun confirmDeletion(items: Collection<ITEM>) {
        items.forEach { confirmItemDeletion(getItemId(it)) }
    }
}
