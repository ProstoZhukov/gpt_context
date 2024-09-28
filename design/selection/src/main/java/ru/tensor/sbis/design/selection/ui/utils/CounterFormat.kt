package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Реализации форматирования счётчиков [SelectorItemModel.counter]
 *
 * @author ma.kolpakov
 */
enum class CounterFormat {

    /**
     * Без форматирования
     */
    DEFAULT {
        override fun format(counter: Int): String? = if (counter == 0) null else counter.toString()
    },

    /**
     * Разделение пробелом по тысячным разрядам
     */
    THOUSANDS_DECIMAL_FORMAT {

        private val formatter = DecimalFormat(
            "###,###",
            DecimalFormatSymbols(Locale.ENGLISH).apply { groupingSeparator = ' ' }
        )

        override fun format(counter: Int): String? = if (counter == 0) null else formatter.format(
            // приведение к Long для работы с числовым типом, а не с Any
            counter
        )
    };

    /**
     * Применение алгоритма форматирования к счётчику [counter]
     */
    internal abstract fun format(counter: Int): String?
}