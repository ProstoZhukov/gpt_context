@file:Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")

package ru.tensor.sbis.base_components.adapter.sectioned

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.ContentChangedObserver
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import ru.tensor.sbis.base_components.adapter.sectioned.visibility.VisibleRangeObserver
import ru.tensor.sbis.common.R as RCommon

private const val NO_INDEX = -1

/**
 * Адаптер для отображения списков, состоящих из нескольких секций.
 *
 * @param <T> - тип элементов в списке
 *
 * @author am.boldinov
 */
@Suppress("unused")
class MultiSectionAdapter<T : ListItem> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), VisibleRangeObserver {

    /**
     * Набор секций списочного компонента.
     */
    private val sections =
        mutableListOf<ListSection<T, out ListController, out RecyclerView.Adapter<out RecyclerView.ViewHolder>>>()

    /**
     * Наблюдатели за адаптерами-делегатами.
     */
    private val observers = mutableListOf<RecyclerView.AdapterDataObserver>()

    /**
     * Индекс первой активной секции.
     */
    private var headSectionIndex = NO_INDEX

    /**
     * Индекс последней активной секции.
     */
    private var tailSectionIndex = NO_INDEX

    /**
     * Текущий набор элементов списка.
     */
    private val snapshot = MultiSectionSnapshot<T>()

    /**
     * Обработчик для выполнения действий на ui потоке.
     */
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Версия содержимого.
     */
    private var version = 0

    /**
     * Задать набор секций списка.
     */
    fun setSections(newContent: List<ListSection<T, *, *>>) {
        if (sections != newContent) {
            sections.forEachIndexed { index, section ->
                section.adapter.unregisterAdapterDataObserver(observers[index])
            }
            observers.clear()
            ++version
            sections.clear()
            sections.addAll(newContent)
            for (section in sections) {
                val observer = SectionDataObserver(section)
                observers.add(observer)
                section.adapter.registerAdapterDataObserver(observer)
            }
            // Перестраиваем снимок
            rebuildContent()
        }
    }

    /**
     * Построить новый снимок списка.
     */
    private fun rebuildContent() {
        // Обновляем индексы
        calculateActiveSections()
        // Строим новый снимок
        processNewSnapshot()
        // Актуализируем политику сохранения состояния секций
        updateStateRestorationPolicy()
    }

    /**
     * Построить новую секцию списка.
     */
    private fun rebuildSection(section: ListSection<T, *, *>) {
        if (calculateActiveSections()) {
            // Набор активные секции изменились, строим новый снимок
            processNewSnapshot()
        } else {
            // Набор активных секций не изменился, обновляем целевую секцию
            snapshot.update(section)
        }
        // Актуализируем политику сохранения состояния секций
        updateStateRestorationPolicy()
    }

    /**
     * Обновить индексы активных секций и их смещения.
     *
     * @return true - если перечень активных секций изменился, false - иначе
     */
    @UiThread
    private fun calculateActiveSections(): Boolean {
        val oldHead = headSectionIndex
        val oldTail = tailSectionIndex
        val requiredIsEmpty = sections.find { it.isRequired && it.isEmpty() } != null
        if (sections.isEmpty() || requiredIsEmpty) {
            headSectionIndex = NO_INDEX
            tailSectionIndex = NO_INDEX
        } else {
            val size = sections.size

            var head = 0
            // Если есть элемент с неизвестным началом - он является началом всего списка
            for (i in 1 until size) {
                if (!sections[i].controller.knownHead()) {
                    head = i
                }
            }
            headSectionIndex = head

            var tail = size - 1
            // Если есть элемент с неизвестным концом - он является концом всего списка
            for (i in size - 2 downTo 0) {
                if (!sections[i].controller.knownTail()) {
                    tail = i
                }
            }
            tailSectionIndex = tail
        }

        // Пересчитываем смещение секций
        calculateOffsets()

        return headSectionIndex != oldHead || tailSectionIndex != oldTail
    }

    /**
     * Пересчитать смещения секций.
     */
    @UiThread
    private fun calculateOffsets() {
        var offset = 0
        for (i in headSectionIndex..tailSectionIndex) {
            if (i != NO_INDEX) {
                val section = sections[i]
                section.sectionOffset = offset
                offset += section.getItemCount()
            }
        }
    }

    /**
     * Обработать событие изменения содержимого.
     */
    @UiThread
    private fun processNewSnapshot() {
        val oldSnapshot = snapshot.items()

        // Формируем новый снепшот
        val trimmer = snapshot.reset()
        for (i in headSectionIndex..tailSectionIndex) {
            if (i != NO_INDEX) {
                snapshot.append(sections[i])
            }
        }

        // Обрабатываем изменения набора данных
        dispatchSnapshotChanges(oldSnapshot, snapshot.items())

        // Высвобождаем лишние ресурсы
        trimmer.trim()
    }

    override fun getItemCount(): Int {
        if (sections.isEmpty()) {
            return 0
        }
        var sum = 0
        // Подсчитываем количество элементов в активных секциях
        for (i in headSectionIndex..tailSectionIndex) {
            if (i != NO_INDEX) {
                sum += sections[i].getItemCount()
            }
        }
        return sum
    }

    override fun getItemViewType(position: Int): Int {
        return findSection(position) { _, section ->
            section.adapter.getItemViewType(position - section.getSectionOffset())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        for (i in headSectionIndex..tailSectionIndex) {
            try {
                val section = sections[i]
                val holder = section.adapter.createViewHolder(parent, viewType)
                associateHolder(holder, i, section.getSectionOffset())
                return holder
            } catch (ex: Throwable) {
                // Timber.w(ex) TODO
            }
        }
        throw IllegalArgumentException("Failed to create holder for view type $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        findSection(position) { index, section ->
            associateHolder(holder, index, section.getSectionOffset())
            section.getRecyclerAdapter().onBindViewHolder(holder, position - section.getSectionOffset())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        findSection(position) { index, section ->
            associateHolder(holder, index, section.getSectionOffset())
            section.getRecyclerAdapter().onBindViewHolder(holder, position - section.getSectionOffset(), payloads)
        }
    }

    override fun findRelativeAdapterPositionIn(
        adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
        viewHolder: RecyclerView.ViewHolder,
        localPosition: Int
    ): Int {
        val offset = viewHolder.itemView.getTag(RCommon.id.multi_section_adapter_section_offset) as Int? ?: 0
        return super.findRelativeAdapterPositionIn(adapter, viewHolder, localPosition - offset)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        getAssociatedAdapter(holder)?.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        getAssociatedAdapter(holder)?.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        getAssociatedAdapter(holder)?.onViewRecycled(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        sections.forEach {
            it.adapter.onAttachedToRecyclerView(recyclerView)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        sections.forEach {
            it.adapter.onDetachedFromRecyclerView(recyclerView)
        }
    }

    override fun onVisibleRangeChanged(first: Int, last: Int, direction: Int) {
        // todo reuse runnable instance
        handler.post { onVisibleRangeChangedInternal(first, last, direction) }
    }

    private fun onVisibleRangeChangedInternal(firstVisible: Int, lastVisible: Int, direction: Int) {
        val active = headSectionIndex..tailSectionIndex
        val visible = lastVisible - firstVisible + 1
        var offset = 0
        var consumed = 0
        for (i in sections.indices) {
            val section = sections[i]
            if (i in active) { // Секция активная
                if (visible > consumed) { // Есть нераспределенные видимые элементы
                    val size = section.getItemCount()
                    // Вычисляем видимые элементы текущей секции
                    val localFirst = Math.max(firstVisible - offset, 0)
                    val localLast = Math.min(lastVisible - offset, size - 1)
                    consumed += localLast - localFirst + 1
                    offset += size
                    section.controller.onVisibleRangeChanged(localFirst, localLast, direction)
                    continue
                }
            }
            section.controller.onVisibleRangeChanged(-1, -1, 0)
        }
    }

    private fun getItem(position: Int): T? {
        return findSection(position) { _, section ->
            section.getSectionItem(position - section.getSectionOffset())
        }
    }

    private inline fun <R> findSection(
        position: Int,
        handler: (Int, ListSection<T, out ListController, out RecyclerView.Adapter<out RecyclerView.ViewHolder>>) -> R
    ): R {
        // Ищем активную секцию, которой принадлежит элемент
        for (i in headSectionIndex..tailSectionIndex) {
            if (i != NO_INDEX) {
                val section = sections[i]
                val size = section.getItemCount()
                if (position < size + section.getSectionOffset()) {
                    return handler(i, section)
                }
            }
        }
        throw IndexOutOfBoundsException()
    }

    private fun dispatchSnapshotChanges(oldSnapshot: List<T?>, newSnapshot: List<T?>) {
        val callback = DiffCallback(oldSnapshot, newSnapshot)
        val result = DiffUtil.calculateDiff(callback, false)
        result.dispatchUpdatesTo(this)
    }

    private fun associateHolder(holder: RecyclerView.ViewHolder, index: Int, sectionOffset: Int) {
        holder.itemView.apply {
            setTag(RCommon.id.multi_section_adapter_content_version, version)
            setTag(RCommon.id.multi_section_adapter_section_index, index)
            setTag(RCommon.id.multi_section_adapter_section_offset, sectionOffset)
        }
    }

    private fun getAssociatedSection(holder: RecyclerView.ViewHolder): ListSection<T, *, *>? {
        val view = holder.itemView
        if (view.getTag(RCommon.id.multi_section_adapter_content_version) == version) {
            val section = view.getTag(RCommon.id.multi_section_adapter_section_index)
            if (section is Int) {
                return sections[section]
            }
        }
        return null
    }

    private fun getAssociatedAdapter(holder: RecyclerView.ViewHolder): RecyclerView.Adapter<RecyclerView.ViewHolder>? {
        return getAssociatedSection(holder)?.getRecyclerAdapter()
    }

    private inner class SectionDataObserver(
        private val section: ListSection<T, *, *>
    ) : RecyclerView.AdapterDataObserver(), ContentChangedObserver {

        override fun onContentChanged(adapter: RecyclerView.Adapter<*>) {
            rebuildSection(section)
        }

        override fun onChanged() {
            if (!skip()) {
                notifyDataSetChanged() // todo более точечное обновление
            }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onItemRangeChanged(positionStart, itemCount, null)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            if (!skip()) {
                for (i in 0 until itemCount) {
                    val sectionItemPosition = positionStart + i // позиция элемента в адаптере секции
                    val adapterPosition = getAdapterPosition(sectionItemPosition) // позиция элемента в текущем адаптере
                    notifyItemChanged(adapterPosition, payload)
                }
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (!skip()) {
                val adapterPosition = getAdapterPosition(positionStart) // позиция элемента в текущем адаптере
                notifyItemRangeInserted(adapterPosition, itemCount)
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            if (!skip()) {
                val adapterPosition = getAdapterPosition(positionStart) // позиция элемента в текущем адаптере
                notifyItemRangeRemoved(adapterPosition, itemCount)
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if (!skip()) {
                for (i in 0 until itemCount) {
                    val fromAdapterPosition = getAdapterPosition(fromPosition + i)
                    val toAdapterPosition = getAdapterPosition(toPosition + i)
                    notifyItemMoved(fromAdapterPosition, toAdapterPosition)
                }
            }
        }

        override fun onStateRestorationPolicyChanged() {
            super.onStateRestorationPolicyChanged()
            updateStateRestorationPolicy()
        }

        private fun skip(): Boolean {
            return !snapshot.contains(section)
        }

        private fun getAdapterPosition(sectionPosition: Int): Int {
            return sectionPosition + section.getSectionOffset()
        }
    }

    private fun updateStateRestorationPolicy() {
        val policy = computeStateRestorationPolicy()
        if (stateRestorationPolicy != policy) {
            stateRestorationPolicy = policy
        }
    }

    private fun computeStateRestorationPolicy(): StateRestorationPolicy {
        sections.forEach {
            if (it.adapter.stateRestorationPolicy == StateRestorationPolicy.PREVENT ||
                it.adapter.stateRestorationPolicy == StateRestorationPolicy.PREVENT_WHEN_EMPTY && it.isEmpty()) {
                return StateRestorationPolicy.PREVENT
            }
        }
        return StateRestorationPolicy.ALLOW
    }

    @Suppress("UNCHECKED_CAST")
    private fun ListSection<*, *, *>.getRecyclerAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
    }

    private class DiffCallback<T : ListItem>(
        private val oldContent: List<T?>,
        private val newContent: List<T?>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldContent.size
        }

        override fun getNewListSize(): Int {
            return newContent.size
        }

        override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
            val oldItem = oldContent[oldItemPos]
            val newItem = newContent[newItemPos]
            if (oldItem != null && oldItem.itemTypeId == newItem?.itemTypeId) {
                return true
            }
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
            val oldItem = oldContent[oldItemPos]
            val newItem = newContent[newItemPos]
            oldItem?.areContentsTheSame(newItem)?.let { return it }
            newItem?.areContentsTheSame(oldItem)?.let { return it }
            return oldItem == newItem
        }

    }

}