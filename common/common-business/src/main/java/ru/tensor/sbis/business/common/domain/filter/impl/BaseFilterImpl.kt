package ru.tensor.sbis.business.common.domain.filter.impl

import androidx.annotation.CallSuper
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.Filter
import ru.tensor.sbis.business.common.domain.filter.base.HashFilterImpl
import java.util.*

/**
 * Базовая реализация фильтра
 *
 * @property CPP_FILTER тип фильтра контроллера
 *
 * @property usedFilters ПОСЛЕДНИЕ используемые/синхронизируемые фильтры, пара ключ(хэш)/значение(фильтр)
 * @property lastHash хэш ПОСЛЕДНЕГО используемого/синхронизируемого фильтра.
 * Может использоваться для Пакеровщика CRUD команд.
 */
abstract class BaseFilterImpl<CPP_FILTER : Any>(hashProvider: HashFilterProvider) :
    HashFilterImpl<CPP_FILTER>(hashProvider),
    Filter<CPP_FILTER> {

    var lastHash: String = ""
        private set

    /**
     * Строит фильтр контроллера [CPP_FILTER]
     */
    protected abstract fun innerBuild(): CPP_FILTER

    override val lastCppFilter: CPP_FILTER?
        get() = usedFilters[lastHash]

    /**
     * Построить фильтра контроллера [CPP_FILTER] для репозитория фасада
     *
     * @param hash хэш фильтра полученный из коллбэка синхронизации, если передан то возвращается
     * фильтр [CPP_FILTER] из числа раннее синхронизируемых, иначе строится новый
     * @param markAsUsed true если фильтр действительно будет передан в репозиторий!!!
     * Иначе [CPP_FILTER] не будет добавлен в [usedFilters]. По-умолчанию true
     */
    @CallSuper
    override fun build(
        hash: String,
        markAsUsed: Boolean
    ): CPP_FILTER =
        if (hash.isNotBlank() && usedFilters.contains(hash)) {
            lastHash = hash
            /** возвращается фильтр [CPP_FILTER] из числа раннее синхронизируемых*/
            usedFilters.getOrElse(hash) { innerBuild() }
        } else {
            /** строится новый фильтр [CPP_FILTER] и добавляется в [usedFilters] если [markAsUsed] = true*/
            val cppFilter = innerBuild()
            if (markAsUsed) {
                addToUsed(cppFilter)
            }
            cppFilter
        }

    /**
     * Сбросить состояния предыдущего использования [usedFilters], [lastHash]
     */
    protected fun resetSyncedState() {
        lastHash = ""
        usedFilters.clear()
    }

    private fun addToUsed(filter: CPP_FILTER) {
        val hash = getFilterHash(filter)
        val used = usedFilters.containsKey(hash)
        lastHash = hash
        if (used.not()) {
            usedFilters[hash] = filter
        }
    }

    private val usedFilters: HashMap<String, CPP_FILTER> = LinkedHashMap()
}