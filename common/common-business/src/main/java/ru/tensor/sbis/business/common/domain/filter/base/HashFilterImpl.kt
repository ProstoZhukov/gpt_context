package ru.tensor.sbis.business.common.domain.filter.base

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PROTECTED
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.HashFilter

/**
 * Реализация фильтра с валидацией по хэш
 *
 * @param hashProvider поставщик хэшей фильтров контроллера
 * @property CPP_FILTER тип фильтра контроллера
 * @property lastCppFilter ПОСЛЕДНИЙ используемый/синхронизируемый фильтр [CPP_FILTER] для проверки хэшей
 */
abstract class HashFilterImpl<CPP_FILTER : Any>(
    private val hashProvider: HashFilterProvider
) : HashFilter {

    @VisibleForTesting(otherwise = PROTECTED)
    abstract val lastCppFilter: CPP_FILTER?

    /** [HashFilter.equalCallback] */
    override fun equalCallback(callback: RefreshCallback): Boolean {
        val filterHash = getLastHash()
        val filterName = hashProvider.getName(lastCppFilter)
        callback.reportLog(filterHash, filterName)
        // Проверяем эквивалентность фильтров по хэшу получаемому с фасада контроллера (НЕ ошибка)
        return callback.syncHash == filterHash
    }

    /**
     * @return хэш для фильтра [CPP_FILTER]
     */
    protected fun getFilterHash(filter: CPP_FILTER) = filter.toHash()

    private fun getLastHash(): String =
        lastCppFilter.takeIf { it != null }?.toHash().orEmpty()

    private fun CPP_FILTER.toHash(): String {
        return "${hashProvider.getHash(this)}"
    }
}