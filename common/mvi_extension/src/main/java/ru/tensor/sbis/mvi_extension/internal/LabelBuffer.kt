package ru.tensor.sbis.mvi_extension.internal

import ru.tensor.sbis.mvi_extension.LabelBufferStrategy
import java.util.LinkedList

/**
 * Интерфейс, описывающий поведение буфера для хранения лейблов.
 *
 * Created by Aleksey Boldinov on 31.05.2023.
 */
internal interface LabelBuffer<Label> {

    companion object {

        /**
         * Создает необходимую реализацию [LabelBuffer] на основе стратегии.
         */
        fun <Label> from(strategy: LabelBufferStrategy): LabelBuffer<Label> {
            return when (strategy) {
                LabelBufferStrategy.BeforeInit -> DefaultLabelBuffer()
                is LabelBufferStrategy.Buffer -> DefaultLabelBuffer(strategy.capacity)
                LabelBufferStrategy.NoBuffer -> EmptyLabelBuffer()
            }
        }
    }

    /**@SelfDocumented */
    fun add(label: Label)

    /**@SelfDocumented */
    fun clear()

    /**@SelfDocumented */
    fun poll(): Label?

    /**@SelfDocumented */
    fun size(): Int

    /**@SelfDocumented */
    fun extractAll(callback: (label: Label) -> Unit)

    /**@SelfDocumented */
    fun toSerialized(): LabelBuffer<Label> {
        if (this is SerializedLabelBuffer<Label>) {
            return this
        }
        return SerializedLabelBuffer(this)
    }
}

/**@SelfDocumented */
internal class EmptyLabelBuffer<Label> : LabelBuffer<Label> {

    override fun add(label: Label) = Unit

    override fun clear() = Unit

    override fun poll(): Label? = null

    override fun size(): Int = 0

    override fun extractAll(callback: (label: Label) -> Unit) = Unit
}

/**
 * Реализация буфера по умолчанию.
 * Не является потокобезопасной
 *
 * @param maxSize максимальный размер буфера.
 * При переполнении происходит удаления элементов из начала буфера.
 */
internal class DefaultLabelBuffer<Label>(
    private val maxSize: Int = Int.MAX_VALUE
) : LabelBuffer<Label> {

    private val source = LinkedList<Label>()

    override fun add(label: Label) {
        source.add(label)
        source.trimToSize(maxSize)
    }

    override fun clear() = source.clear()

    override fun poll(): Label? = source.poll()

    override fun extractAll(callback: (label: Label) -> Unit) {
        while (source.isNotEmpty()) {
            source.poll()?.let(callback)
        }
    }

    override fun size(): Int = source.size

    private fun LinkedList<*>.trimToSize(maxSize: Int) {
        repeat(size - maxSize) {
            poll()
        }
    }
}

/**
 * Потокобезопасная обертка над исходным [LabelBuffer].
 */
internal class SerializedLabelBuffer<Label>(
    private val source: LabelBuffer<Label>
) : LabelBuffer<Label> {

    @Synchronized
    override fun add(label: Label) = source.add(label)

    @Synchronized
    override fun clear() = source.clear()

    @Synchronized
    override fun poll(): Label? = source.poll()

    @Synchronized
    override fun size(): Int = source.size()

    @Synchronized
    override fun extractAll(callback: (label: Label) -> Unit) = source.extractAll(callback)

}