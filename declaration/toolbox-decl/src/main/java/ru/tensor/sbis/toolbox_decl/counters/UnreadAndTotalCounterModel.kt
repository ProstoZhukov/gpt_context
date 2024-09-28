package ru.tensor.sbis.toolbox_decl.counters

/**
 * Модель счётчика, который предоставляет информацию о новых событиях и их общее количество
 *
 * @author da.zolotarev
 * Создан 12/19/2018
 */
data class UnreadAndTotalCounterModel(val unreadCount: Int, val totalCount: Int, val unseenCount: Int = 0)