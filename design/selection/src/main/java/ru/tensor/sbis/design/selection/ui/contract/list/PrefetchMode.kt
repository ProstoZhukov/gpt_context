package ru.tensor.sbis.design.selection.ui.contract.list

import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity

/**
 * Режим подгрузки элементов списка
 *
 * @author ma.kolpakov
 */
enum class PrefetchMode {

    /**
     * Загрузка элементов в конец и начало в зависимости от [PagingEntity.hasNext] и [PagingEntity.hasPrevious]
     */
    PREFETCH,

    /**
     * Полное обновление списка
     */
    RELOAD
}