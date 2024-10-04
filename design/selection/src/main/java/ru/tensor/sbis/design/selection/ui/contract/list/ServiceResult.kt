package ru.tensor.sbis.design.selection.ui.contract.list

import ru.tensor.sbis.list.base.data.ResultHelper

/**
 * Вспомогательный класс для передачи метаданных из _DataRefreshCallback_ в анализатор результата [ResultHelper], где на
 * их основе будет принято решение об отображении данных или заглушки
 *
 * @param SERVICE_RESULT тип результата, который возвращает микросервис
 *
 * @see ResultHelper.isStub
 *
 * @author ma.kolpakov
 */
data class ServiceResult<out SERVICE_RESULT>(
    val data: SERVICE_RESULT? = null,
    val meta: Map<String, String> = emptyMap()
)