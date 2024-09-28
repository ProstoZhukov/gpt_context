package ru.tensor.sbis.business.common.domain.filter

import ru.tensor.sbis.business.common.domain.filter.navigation.NavigationType


/**
 * Списочный фильтр. Универсальный интерфейс фильтра для использования с фасадами имеющими списочные методы
 *
 * Использование фильтра:
 * 1. при постраничной навигации:
 * - вызвать [incPosition] для перевода фильтра на получение следующей страницы
 * - вызвать [reset] для сброса пагинации и состояния  фильтра
 * 2. при курсорной навигации:
 * - вызвать [incPage] указав новый [CPP_CURSOR] для перевода фильтра на получение записей относительно
 * нового "курсора" (фиксированной записи списка)
 * - вызвать [reset] для сброса пагинации и состояния  фильтра
 */
interface ListFilter<CPP_FILTER, CPP_CURSOR> : Filter<CPP_FILTER> {

    /** Тип навигации для фильтра */
    val navigationType: NavigationType

    /** Размерность страницы/разворота/выборки в зависимости от типа навигации, т.е. количество запрашиваемых записей */
    var limit: Int

    /** Была ли текущая страница в числе ранее полученных для данного фильтра */
    val shownPage: Boolean

    /** true если текущая страница фильтра является первой. */
    val isFirstPageToSync: Boolean

    /** true если только первая страница фильтра была или в процессе синхронизации. */
    val isFirstPageOnlySynced: Boolean

    /**
     * Индикатор специфичного фильтра, true если фильтр действительно настроен специфично
     * помимо базовых атрибутов вроде (идентификатора выборки и тд.). По-умолчанию всегда false
     */
    val hasCertainFilter: Boolean

    /** true если тип навигации по курсору */
    val isPositionType: Boolean

    /** true если тип навигации постраничный */
    val isPageType: Boolean

    /**
     * Сбросить состояние фильтра
     * Т.е. навигацию в фильтре, маркер ранее полученных и синхронизированных страниц
     */
    fun reset()

    /**
     * Сдвинуть навигацию фильтра на следующую страницу если текущая уже использовалась/синхронизировалась.
     * Только для фильтра с типом навигации [NavigationType.PAGE].
     */
    fun incPage()

    /**
     * Обновить курсор навигации для получения следующих записей
     * Только для фильтра с [NavigationType.POSITION]
     *
     * @param newPosition билдер новой позиции
     * @param offset размерность последней выборки
     * @param forceInc "принудительное" обновление/применение позиции курсора [newPosition]
     * Например, если это выборка после синхронизации
     */
    fun incPosition(
        newPosition: CursorBuilder<CPP_CURSOR>,
        offset: Int = 0,
        forceInc: Boolean = false
    )
}

/**
 * Билдер курсора списочного фильтра [ListFilter] с навигацией по позиции [NavigationType.POSITION]
 */
interface CursorBuilder<CPP_CURSOR> {
    fun createCursor(): CPP_CURSOR
}