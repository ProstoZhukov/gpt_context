package ru.tensor.sbis.list.base.domain.entity.paging

import ru.tensor.sbis.list.base.data.ResultHelper

/**
 *  Логика определения наличиня возможности подгрузки следующей порции данных.
 */
class CheckHasNext<DATA>(private val helper: ResultHelper<*, DATA>, private val getPage: () -> Int) :
        (Map<Int, DATA>) -> Boolean {

    override fun invoke(mapToCheck: Map<Int, DATA>): Boolean {
        //Еще не грузили данные, заначит, считаем, что нужно грузить.
        if (getPage() < 0) return true

        if (mapToCheck.isEmpty()) return true

        //Был запрос в кеш, если начата синхронищзация с облаком, то микросервис возвращает true для hasNext.
        val lastEntry = mapToCheck.values.last()

        if (mapToCheck.size > 1 && helper.isEmpty(lastEntry)) return false

        return helper.hasNext(lastEntry)
    }
}