package ru.tensor.sbis.list.base.domain.boundary

import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.presentation.StubEntity

/**
 * Реализация этого интерфейса позволит отображать данные "бизнес модели"(БМ) на экране списка.
 */
interface View<ENTITY : ListScreenEntity> {

    /**
     * Показать данные списка.
     *
     * @param entity БМ содержащая данные списка для отображения.
     */
    fun showData(entity: ENTITY)

    /**
     * Показывать заглушку.
     *
     * @param stubEntity БМ содержащая данные заглушки для отображения.
     * @param immediate должна ли заглушка отображаться без какой-либо задержки.
     */
    fun showStub(stubEntity: StubEntity, immediate: Boolean = false)

    /**
     * Отобразить состояние загрузки данных - индикатор прогресса.
     */
    fun showLoading()

    /**
     * Отобразить состояние подгрузки следующей страницы данных.
     */
    fun showLoadNext()

    /**
     * Отобразить состояние подгрузки предыдущей страницы данных.
     */
    fun showPrevious()

    /**
     * Сбросить состояние вью элементов до начального.
     */
    fun cleanState()
}