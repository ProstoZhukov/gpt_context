package ru.tensor.sbis.list.view.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.AnyViewHolderHelper
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.list.view.utils.ListData
import java.util.Collections

/**
 * Класс инкапсулирует доступ к контенту адаптера.
 */
internal class SbisAdapterDelegate(
    private val itemsAndTypeIndexes: ArrayList<ItemAndTypeIndex> = ArrayList(),
    private val typeAndViewHolderHelper: LinkedHashMap<Any, AnyViewHolderHelper> = LinkedHashMap()
) {
    private var listData: ListData? = null

    /**
     * Позиция ближайшего стики-заголовка для указанной позиции ячейки.
     * @param forPosition Int позиция ячейки, для которой нужно выполнить поиск заголовка.
     * @return Int позиция ячейки, содержащей стики-заголовок или [NO_HEADER] если он отсутствует.
     */
    @Synchronized
    fun getStickyHeaderPos(forPosition: Int): Int {
        for (position in forPosition downTo 0) {
            if (isSticky(position)) return position
        }

        return NO_HEADER
    }

    /**
     * Заменить данные адаптера на переданные использую DiffUtil.
     * @param data новые данные, которые заменят старые.
     * @param adapter SbisAdapter адаптер в котором будет выполнена замена данных.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Synchronized
    fun setItemsAndNotify(
        data: ListData,
        adapter: SbisAdapter
    ) {
        listData = data
        val list = data.getItems()
        when {
            list.isEmpty() -> {
                removeItems()
                adapter.notifyDataSetChanged()
            }
            getItemCount() == 0 -> {
                list.forEach(::addLast)
                adapter.notifyDataSetChanged()
            }
            else -> {
                val productDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(getItems(), list))
                removeItems()
                list.forEach(::addLast)
                productDiffResult.dispatchUpdatesTo(adapter)
            }
        }
    }

    /**
     * Fдаптер справится сам с перестановкой элементов через вызов метода adapter.notifyItemMoved(),
     * а этот метод поддержит изменение порядка элементов в коллекции, из которой будет происходить биндинг элементов
     * к viewHolder'ам.
     */
    @Synchronized
    fun swap(firstElementIndex: Int, secondElementIndex: Int) {
        Collections.swap(itemsAndTypeIndexes, firstElementIndex, secondElementIndex)
    }

    @Synchronized
    fun replaceItems(list: List<AnyItem>) {
        removeItems()
        list.forEach(::addLast)
    }

    /**
     * Удалить элемент, если он последний в списке.
     * @param item Item<out Any, out ViewHolder>
     */
    @Synchronized
    fun removeIfLast(item: AnyItem, onRemove: (indexOfRemoved: Int) -> Unit) {
        if (checkIfLastItemIs(item)) {
            val index = itemsAndTypeIndexes.size - 1
            itemsAndTypeIndexes.removeAt(index)
            onRemove(index)
        }
    }

    /**
     * Создать [RecyclerView.ViewHolder] для указанного типа ячейки.
     * @param parent ViewGroup
     * @param viewType Int
     * @return ViewHolder
     */
    @Synchronized
    fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = getViewHolderHelperByTypeIndex(viewType).createViewHolder(parent)

    /**
     * Получить тип ячейки для работы с [RecyclerView.ViewHolder].
     * @param position Int индекс ячейки.
     * @return Int тип ячейки.
     */
    @Synchronized
    fun getItemViewType(position: Int) = getViewTypeIndex(position)

    /**
     * Привязать данные ячейки в View в [RecyclerView.ViewHolder].
     * @param holder ViewHolder вью холдер ячейки.
     * @param position Int индекс ячейки.
     */
    @Synchronized
    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsAndTypeIndexes[position].item.data
        getViewHolderHelper(holder).bindToViewHolder(item, holder)
    }

    /**
     * "Освободить" [holder] ячейки
     *
     * @see RecyclerView.Adapter.onViewRecycled
     */
    @Synchronized
    fun onViewRecycled(holder: ViewHolder) {
        getViewHolderHelper(holder).recycleViewHolder(holder)
    }

    /**
     * Добавить элемент в конец списка.
     *
     * @param item Item<out Any, out ViewHolder>
     */
    @Synchronized
    fun addLast(item: AnyItem) {
        val type = item.getViewHolderType()
        typeAndViewHolderHelper[type] = item
        itemsAndTypeIndexes.add(
            ItemAndTypeIndex(
                item,
                typeAndViewHolderHelper.keys.indexOf(type)
            )
        )
    }

    @Synchronized
    fun addLastWithCheck(item: AnyItem): Boolean {
        if (itemsAndTypeIndexes.isEmpty() || itemsAndTypeIndexes[itemsAndTypeIndexes.size - 1].item == item)
            return false

        val type = item.getViewHolderType()
        typeAndViewHolderHelper[type] = item
        itemsAndTypeIndexes.add(
            ItemAndTypeIndex(
                item,
                typeAndViewHolderHelper.keys.indexOf(type)
            )
        )
        return true
    }

    /**
     * Добавить элемент [item] в начало списка.
     */
    @Synchronized
    fun addFirst(item: AnyItem) {
        val type = item.getViewHolderType()
        typeAndViewHolderHelper[type] = item
        itemsAndTypeIndexes.add(
            0,
            ItemAndTypeIndex(
                item,
                typeAndViewHolderHelper.keys.indexOf(type)
            )
        )
    }

    /**
     * Удалить элемент из начала списка, если соответствует переданному.
     *
     * @param item Item<out Any, out ViewHolder> элемент для сравнения.
     */
    @Synchronized
    fun removeFirst(item: AnyItem, onRemove: () -> Unit) {
        if (checkIfFirstItemIs(item)) {
            itemsAndTypeIndexes.removeAt(0)
            onRemove()
        }
    }

    /**
     * Количество элементов.
     * @return Int количество.
     */
    @Synchronized
    fun getItemCount() = itemsAndTypeIndexes.size

    /**
     * Индекс последнего элемента.
     * @return Int количество.
     */
    @Synchronized
    fun getLastIndex() = getItemCount() - 1

    /**
     * Является ли элемент по указанной позиции стики-заголовком.
     * @param position Int позиция элемента.
     * @return Boolean элемент имеет соответствующий признак. Для отрицательных индексов, или превышающих фактическое
     * число элементов, всегда возвращает false.
     */
    @Synchronized
    fun isSticky(position: Int): Boolean {
        if (position > itemsAndTypeIndexes.size - 1 || position < 0) return false

        return itemsAndTypeIndexes[position].item.isSticky
    }

    /**
     * Уровень в дереве элемента по указанной позиции.
     * @param position Int позиция элемента.
     * @return Int уровень в дереве.
     */
    @Synchronized
    fun getLevel(position: Int) = itemsAndTypeIndexes[position].item.level

    /**
     * Является ли элемент по указанной позиции кликабельным.
     * @param position Int позиция элемента.
     * @return Boolean элемент имеет соответствующий признак.
     */
    @Synchronized
    fun isClickable(position: Int) = itemsAndTypeIndexes[position].item.isClickable()

    /**
     *  Использует ли элемент по указанной позиции прикладные клик листенеры (Например назначенные напрямую у View) или берет их из настроек.
     *  Если вернет true, то листенры из опций игнорируются
     */
    @Synchronized
    fun getUseCustomListeners(position: Int) = itemsAndTypeIndexes[position].item.useCustomListeners

    /**
     * Имеет ли элемент по указанной позиции [position] отступы не по стандарту.
     */
    @Synchronized
    fun hasCustomSidePadding(position: Int) = itemsAndTypeIndexes[position].item.customSidePadding

    /**
     * Событие нажатия на элемент по указанной позиции.
     * @param position Int позиция элемента.
     * @return функцию обработки нажатий на элементы
     */
    @Synchronized
    fun clickAction(position: Int) = itemsAndTypeIndexes[position].item.clickAction

    /**
     * Событие долгого нажатия на элемент по указанной позиции.
     * @param position Int позиция элемента.
     * @return функцию обработки нажатий на элементы
     */
    @Synchronized
    fun longClickAction(position: Int) = itemsAndTypeIndexes[position].item.longClickAction

    /**
     * Получить элемент по указанной позиции.
     * @param position Int позиция элемента.
     * @return Item<out Any, out ViewHolder> элемент.
     */
    @Synchronized
    fun getItem(position: Int) = itemsAndTypeIndexes[position].item

    /**
     * Получить позицию элемента в списке.
     * @param item Item<out Any, out ViewHolder> элемент.
     * @return Int позиция.
     */
    @Synchronized
    fun getItemPosition(item: AnyItem) = itemsAndTypeIndexes.indexOfFirst {
        DiffUtilCallback.areItemsTheSame(it.item, item)
    }

    /**
     * Адаптер не содержит данных.
     */
    @Synchronized
    fun isEmpty() = itemsAndTypeIndexes.isEmpty()

    /**
     * Должен ли элемент по указанной позиции оставаться выделенным после нажатия, когда список отображается в
     * мастер части на планшете.
     */
    fun isHighlightable(position: Int) = itemsAndTypeIndexes[position].item.isHighlightable

    /**
     * Выполнить действие [actionWithPosition] с позицией элемента, найденному по предикату [predicate]
     */
    fun doWithItemPosition(predicate: (AnyItem) -> Boolean, actionWithPosition: (Int) -> Unit) {
        val index = getItems().indexOfFirst {
            predicate(it)
        }
        if (index > -1) {
            actionWithPosition(index)
        }
    }

    /**
     * Смержить данные из элемента переданного в [itemToMergeFrom] с помощью ViewHolderHelper.
     */
    fun merge(viewHolder: ViewHolder, itemToMergeFrom: AnyItem) {
        getViewHolderHelper(viewHolder).update(itemToMergeFrom.data, viewHolder)
    }

    private fun getItems() = itemsAndTypeIndexes.map { it.item }

    private fun removeItems() {
        /**
         * Нельзя удалять типы ViewHolder([typeAndViewHolderHelper]), потому что по ним кешируются сами ViewHolder в адаптере
         */
        itemsAndTypeIndexes.clear()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewHolderHelper(viewHolder: ViewHolder) =
        getViewHolderHelperByTypeIndex(viewHolder.itemViewType) as ViewHolderHelper<Any, ViewHolder>

    private fun getViewHolderHelperByTypeIndex(type: Int): AnyViewHolderHelper {
        val iterator = typeAndViewHolderHelper.iterator()

        var typeIndex = 0
        while (iterator.hasNext()) {
            val value = iterator.next()
            if (typeIndex == type) return value.value
            typeIndex++
        }
        @Suppress("UNREACHABLE_CODE")
        return null!!
    }

    private fun checkIfLastItemIs(item: AnyItem): Boolean {
        val lastItemIndex = itemsAndTypeIndexes.size - 1
        return itemsAndTypeIndexes.isNotEmpty()
            && itemsAndTypeIndexes[lastItemIndex].item.data == item.data
            && itemsAndTypeIndexes[lastItemIndex].item.getViewHolderType() == item.getViewHolderType()
    }

    private fun checkIfFirstItemIs(item: AnyItem): Boolean {
        return itemsAndTypeIndexes.isNotEmpty()
            && itemsAndTypeIndexes[0].item.data == item.data
            && itemsAndTypeIndexes[0].item.getViewHolderType() == item.getViewHolderType()
    }

    private fun getViewTypeIndex(position: Int) = itemsAndTypeIndexes[position].typeIndex
}

const val NO_HEADER = -1