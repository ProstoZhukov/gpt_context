package ru.tensor.sbis.appdesign.navigation

import ru.tensor.sbis.design.navigation.view.model.FormatterType
import androidx.arch.core.util.Function

/**
 * Форматирование значений счетчиков для денег.
 *
 * @author va.shumilov
 */
class MoneyNavigationCounter(count: Int, totalCount: Int = count) : NumberNavigationCounter(count, totalCount) {
    private val myTotalFormatter = Function<Int, String?> { count -> if (count < 1) null else "$count р." }

    override fun getFormatter(type: FormatterType): Function<Int, String?> {
        return myTotalFormatter
    }
}