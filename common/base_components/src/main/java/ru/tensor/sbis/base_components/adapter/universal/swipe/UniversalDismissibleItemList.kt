package ru.tensor.sbis.base_components.adapter.universal.swipe

import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import java.util.*

/**
 * Реализация списка, поддерживающая записи, которые могут быть временно удалены и возвращены назад.
 * Временно удаленные записи кешируются в отдельном хранилище, при попытке снова добавить/обновить удаленный элемент
 * операция будет проигнорирована.
 *
 * @author am.boldinov
 */
@Suppress("unused")
class UniversalDismissibleItemList : ArrayList<UniversalBindingItem> {

    private var pendingItemHolder: DismissiblePendingItemHolder? = null
    private var pendingPageItemPosition = -1

    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor() : super()
    constructor(c: MutableCollection<out UniversalBindingItem>) : super(c)

    /**
     * Вызывать при первоначальном удалении элемента
     *
     * @return объект, содержащий позицию, на котором находился элемент, и сам элемент
     */
    fun considerDeleted(itemId: String): Pair<Int, UniversalBindingItem>? {
        val index = indexOfFirst { it.itemTypeId == itemId }
            .takeIf { it >= 0 }
            ?: return null

        val result = Pair(index, get(index))
        pendingItemHolder?.apply {
            pendingItemId?.let {
                unconfirmedDeletedIds.add(it)
            }

            pendingItemId = itemId
            pendingItem = result.second
            restoredItemPosition = result.first
        }
        removeAtInternal(index)

        return result
    }

    /**
     * Вызывать при отмене удаления элемента
     *
     * @return позиция, на которую будет добавлен восстановленый элемент
     */
    fun restoreConsideredDeletedItem(): Int {
        pendingItemHolder?.apply {
            pendingItem
                ?.takeIf { restoredItemPosition in 0..size }
                ?.let {
                    addInternal(restoredItemPosition, it)
                    return restoredItemPosition
                        .also { reset() }
                }
            reset()
        }
        return -1
    }

    /**
     * Вызывать после подтверждения удаления элемента
     *
     * @return элемент, который был подтвержден и удалён окончательно
     */
    fun confirmItemDeletion(itemId: String): UniversalBindingItem? {
        var confirmedItem: UniversalBindingItem? = null
        pendingItemHolder?.apply {
            if (pendingItemId == itemId) {
                confirmedItem = pendingItem
                reset()
            }
            unconfirmedDeletedIds.remove(itemId)
        }
        return confirmedItem
    }

    override fun set(index: Int, element: UniversalBindingItem): UniversalBindingItem {
        pendingItemHolder?.process {
            if (element.itemTypeId == pendingItemId) {
                pendingItem = element
            }
        }
        return super.set(index, element)
    }

    override fun add(element: UniversalBindingItem): Boolean {
        return if (pendingItemHolder != null) {
            add(size, element)
            true
        } else {
            super.add(element)
        }
    }

    override fun add(index: Int, element: UniversalBindingItem) {
        pendingItemHolder?.process {
            val id = element.itemTypeId
            if (id == pendingItemId) {
                pendingItem = element
                return
            }
            if (unconfirmedDeletedIds.contains(id)) {
                return
            }
            if (index <= restoredItemPosition) {
                restoredItemPosition++
            }
        }
        super.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<UniversalBindingItem>): Boolean {
        val result = super.addAll(index, elements)
        pendingItemHolder?.process {
            // если происходит попытка добавить страницу, из которой уже произошло удаление лишних элементов
            // то необходимо изменить позицию для восстановления
            if (elements is UniversalDismissibleItemList && elements.pendingPageItemPosition >= 0) {
                restoredItemPosition = index + elements.pendingPageItemPosition
                elements.pendingPageItemPosition = -1
            } else {
                reflowPage(true)
            }
        }
        return result
    }

    override fun addAll(elements: Collection<UniversalBindingItem>): Boolean {
        return if (pendingItemHolder != null) {
            addAll(size, elements)
        } else {
            super.addAll(elements)
        }
    }

    override fun remove(element: UniversalBindingItem): Boolean {
        confirmItemDeletion(element.itemTypeId)
        indexOf(element)
            .takeIf { it >= 0 }
            ?.let {
                removeAt(it)
                return true
            }
        return false
    }

    override fun removeAll(elements: Collection<UniversalBindingItem>): Boolean {
        pendingItemHolder?.process {
            elements.forEach { confirmItemDeletion(it.itemTypeId) }
            restoredItemPosition -= elements.count { indexOf(it) < restoredItemPosition }
        }
        return super.removeAll(elements.toSet())
    }

    override fun removeAt(index: Int): UniversalBindingItem {
        pendingItemHolder?.process {
            if (index < restoredItemPosition) {
                restoredItemPosition--
            }
        }
        return removeAtInternal(index)
    }

    override fun retainAll(elements: Collection<UniversalBindingItem>): Boolean {
        val itemsToRemove = filter { !elements.contains(it) }
        return removeAll(itemsToRemove)
    }

    internal fun attachPendingItemHolder(pendingItemHolder: DismissiblePendingItemHolder) {
        this.pendingItemHolder = pendingItemHolder
    }

    internal fun reflowAll() {
        // если кандидата на удаление нет в списке то считаем, что он уже удалился
        pendingItemHolder?.apply {
            unconfirmedDeletedIds.forEach { id ->
                if (none { it.itemTypeId == id }) {
                    unconfirmedDeletedIds.remove(id)
                }
            }
            if (pendingItemId != null && none { it.itemTypeId == pendingItemId }) {
                reset()
            }
        }
        reflowPage(true)
    }

    internal fun reflowPage() {
        reflowPage(false)
    }

    private fun reflowPage(restorePosition: Boolean) {
        // если кандидат на удаление пришел в списке - необходимо его почистить
        if (isNotEmpty()) {
            pendingItemHolder?.process {
                var i = 0
                while (i < size) {
                    val item = get(i)
                    if (unconfirmedDeletedIds.contains(item.itemTypeId)) {
                        removeAtInternal(i) // вычищаем unconfirmed элементы
                        continue
                    }
                    if (pendingItemId == item.itemTypeId) {
                        pendingItem = item
                        if (restorePosition) {
                            restoredItemPosition = i // запоминаем обновленную позицию pendingItem
                            pendingPageItemPosition = -1
                        } else {
                            pendingPageItemPosition = i // временно запоминаем обновлённую позицию, чтоб в дальнейшем применить ее в при добавлении элементов
                        }
                        removeAtInternal(i) // вычищаем pendingItem
                        continue
                    }
                    i++
                }
            }
        }
    }

    private fun removeAtInternal(index: Int): UniversalBindingItem {
        return super.removeAt(index)
    }

    private fun addInternal(index: Int, element: UniversalBindingItem) {
        super.add(index, element)
    }

    private inline fun DismissiblePendingItemHolder.process(block: DismissiblePendingItemHolder.(DismissiblePendingItemHolder) -> Unit) {
        if (pendingItemId != null && pendingItem != null || unconfirmedDeletedIds.isNotEmpty()) {
            block(this)
        }
    }
}