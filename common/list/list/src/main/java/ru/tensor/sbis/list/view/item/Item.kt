package ru.tensor.sbis.list.view.item

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.list.view.decorator.ItemDecoration
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.list.view.item.comparator.DefaultComparable
import ru.tensor.sbis.list.view.item.merge.DefaultMergeable
import ru.tensor.sbis.list.view.item.merge.MergeableItem
import timber.log.Timber

/**
 * Реализация элемент списка.
 *
 * @param DATA тип данных ячейки.
 * @param VIEW_HOLDER тип вью холдера, который будет использоваться с для данной ячейки.
 * @property data сами данные ячейки.
 * @property viewHolderHelper объект, которому будет делегированы методы создания вью холдера и биндинга данных в нее.
 * @property comparable методы сравнения инстанса и контента ячеек. !!!Необходимо сравнивать только данные ячейки,
 * которые туда придут из поля data,
 * @property options дополнительные опции ячейки.
 * @property itemDecoration пользовательская декорация ячейки.
 */
open class Item<DATA, VIEW_HOLDER : ViewHolder>(
    val data: DATA,
    private val viewHolderHelper: ViewHolderHelper<DATA, VIEW_HOLDER>,
    private val comparable: ComparableItem<DATA> = DefaultComparable(data),
    private val options: ItemOptions = DEFAULT_OPTIONS,
    private val mergeable: MergeableItem<DATA> = DefaultMergeable(),
    internal val itemDecoration: ItemDecoration? = null,
) : ViewHolderHelper<DATA, VIEW_HOLDER> by viewHolderHelper, ItemOptions by options {

    /**
     * Подставляют ли обе ячейки логически один и тот же элемент. Гарантированно, что элемент для сравнения
     * будет того же типа.
     *
     * @param otherItem ячейка, с которой будет осуществлено сравнение.
     * @return результат сравнения.
     */
    internal fun areTheSame(otherItem: AnyItem): Boolean {
        @Suppress("UNCHECKED_CAST")
        return (comparable.areTheSame(otherItem.data as DATA))
    }

    /**
     * Имеют ли обе ячейки одинаковые данные. Гарантированно, что элемент для сравнения будет того же типа.
     * @param otherItem ячейка, с которой будет осуществлено сравнение.
     * @return результат сравнения.
     */
    internal fun hasTheSameContent(otherItem: AnyItem): Boolean {
        @Suppress("UNCHECKED_CAST")
        return (comparable.hasTheSameContent(otherItem.data as DATA) && options == otherItem.options)
    }

    /**
     * Элемент можно обновить данными из другого элемента.
     */
    internal fun isMergeable() = mergeable !is DefaultMergeable
}

/**
 * Пустой набор опций.
 */
internal val DEFAULT_OPTIONS = Options()

/**
 * Короткая форма записи для типа элемента списка - любой элемент списка с любым типом [ViewHolder].
 */
typealias AnyItem = Item<out Any, out ViewHolder>
typealias AnyViewHolderHelper = ViewHolderHelper<out Any, out ViewHolder>