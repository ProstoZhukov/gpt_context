package ru.tensor.sbis.base_components.adapter.sectioned

import androidx.annotation.CheckResult
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListItem
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSection
import kotlin.math.min

/**
 * Снимок текущего набор элементов списка.
 *
 * @author am.boldinov
 */
internal class MultiSectionSnapshot<T : ListItem> {

    /**
     * Текущий набор секций в снимке.
     */
    private val sections = ArrayList<ListSection<T, out ListController, out RecyclerView.Adapter<out RecyclerView.ViewHolder>>>()

    /**
     * Счетчики элементов для каждой секции.
     */
    private val counters = ArrayList<Int>()

    /**
     * Очиститель ресурсов снимка.
     */
    private val trimmer = Trimmer()

    /**
     * Текущий набор элементов списка.
     */
    private var snapshot = ArrayList<T?>()

    /**
     * Вспомогательная коллекция для выполнения обновления содержимого списка.
     */
    private var buffer = ArrayList<T?>()

    /**
     * Получить текущий набор элементов списка.
     */
    fun items(): List<T?> {
        return snapshot
    }

    /**
     * Получить количество элементов в снимке.
     */
    fun size(): Int {
        return snapshot.size
    }

    /**
     * Сбросить текущий набор элементов.
     */
    @CheckResult
    fun reset(): Trimmer {
        if (buffer.isNotEmpty()) {
            throw IllegalStateException("Buffer should be empty before processing")
        }
        val temp = snapshot
        snapshot = buffer
        buffer = temp
        sections.clear()
        counters.clear()
        return trimmer
    }

    /**
     * Присоединить секцию к снимку.
     */
    fun append(section: ListSection<T, out ListController, out RecyclerView.Adapter<out RecyclerView.ViewHolder>>) {
        val size = section.getItemCount()
        for (i in 0 until size) {
            val item = section.getSectionItem(i)
            snapshot.add(item)
        }
        sections.add(section)
        counters.add(size)
    }

    /**
     * Обновить секцию в снимке.
     */
    fun update(section: ListSection<T, out ListController, out RecyclerView.Adapter<out RecyclerView.ViewHolder>>) {
        val index = sections.indexOf(section)
        if (index == -1) {
            // Секция не найдена в снимке
            return
        }
        val oldCounter = counters[index]
        val newCounter = section.getItemCount()
        val minCounter = min(oldCounter, newCounter)
        val start = section.getSectionOffset()
        // Заменяем элементы секции, которые уже есть в снепшоте - на новые
        for (i in 0 until minCounter) {
            snapshot[start + i] = section.getSectionItem(i)
        }
        if (newCounter <= oldCounter) {
            // Удаляем лишние элементы
            val redundant = oldCounter - newCounter
            repeat(redundant) { snapshot.removeAt(start + newCounter) }
        } else {
            // Вставляем дополнительные элементы секции
            for (i in oldCounter until newCounter) {
                val item = section.getSectionItem(i)
                snapshot.add(start + i, item)
            }
        }
        counters[index] = newCounter
    }

    /**
     * Проверить, есть ли данная секция в снепшоте.
     */
    fun contains(section: ListSection<T, out ListController, out RecyclerView.Adapter<out RecyclerView.ViewHolder>>): Boolean {
        return sections.indexOf(section) != -1
    }

    /**
     * Очиститель ресурсов снимка.
     */
    inner class Trimmer {
        fun trim() {
            buffer.clear()
        }
    }

}