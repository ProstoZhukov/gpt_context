package ru.tensor.sbis.richtext.converter.cfg

/**
 * Конфигурация таблиц
 *
 * @author am.boldinov
 */
interface TableConfiguration {

    /**
     * Возвращает размер таблицы для рендера.
     */
    fun getTableSize(): TableSize

}

/**
 * Размер таблицы.
 * В случае использования ограниченного размера количество колонок/строк может быть увеличено автоматически
 * если одно из значений в таблице является меньше переданного.
 *
 * @property columnLimit максимально-желаемое количество колонок для отображения
 * @property rowLimit максимально-желаемое количество строк для отображения
 */
sealed class TableSize(val columnLimit: Int, val rowLimit: Int) {

    companion object {

        const val NO_LIMIT = 0
    }

    /**
     * Размер таблицы без ограничений.
     */
    object FullSize : TableSize(NO_LIMIT, NO_LIMIT)

    /**
     * Ограниченный размер таблицы.
     */
    object ShrinkSize : TableSize(5, 3)
}

/**
 * Конфигурация таблиц по умолчанию.
 * Отрисовывает таблицу без ограничений по размеру.
 */
open class DefaultTableConfiguration : TableConfiguration {
    override fun getTableSize(): TableSize {
        return TableSize.FullSize
    }
}

/**
 * Конфигурация таблиц с ограничением по размеру.
 * Необходимо использовать для увеличения производительности при отрисовке внутри списков.
 */
class ShrinkTableConfiguration : DefaultTableConfiguration() {

    override fun getTableSize(): TableSize {
        return TableSize.ShrinkSize
    }
}