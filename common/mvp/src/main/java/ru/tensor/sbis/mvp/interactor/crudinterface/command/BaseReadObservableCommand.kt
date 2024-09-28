package ru.tensor.sbis.mvp.interactor.crudinterface.command

import io.reactivex.Observable

/**
 * Команда чтения.
 *
 * @param ENTITY тип сущности
 * @param UUID уникальный идентификатор для чтения сущности
 *
 * @author am.boldinov
 */
@Deprecated(message = "Устаревший подход, переходим на mvi")
interface BaseReadObservableCommand<ENTITY, UUID> {

    fun read(uuid: UUID): Observable<ENTITY>

    fun refresh(uuid: UUID): Observable<ENTITY>
}