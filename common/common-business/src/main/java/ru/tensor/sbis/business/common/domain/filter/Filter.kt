package ru.tensor.sbis.business.common.domain.filter

import ru.tensor.sbis.business.common.domain.filter.base.RefreshCallback

/**
 * Базовый интерфейс фильтра
 */
interface Filter<CPP_FILTER> {

    /**
     * Проверяет связь коллбэка синхронизации и текущего состояния фильтра
     *
     * @param callback информация о завершенном запросе на синхронизацию
     * @return Возвращает true если синхронизация завершена для текущего состояния фильтра
     */
    fun equalCallback(callback: RefreshCallback): Boolean

    /**
     * Запрос на строительство фильтра для репозитория фасада
     *
     * @param hash хэш фильтра полученный из коллбэка синхронизации, если передан то возвращается
     * фильтр [CPP_FILTER] из числа раннее синхронизируемых, иначе строится новый
     * @param markAsUsed true если фильтр действительно будет передан в репозиторий!!!
     * Иначе [CPP_FILTER] не будет добавлен в [usedFilters]. По-умолчанию true
     */
    fun build(
        hash: String = "",
        markAsUsed: Boolean = true
    ): CPP_FILTER
}