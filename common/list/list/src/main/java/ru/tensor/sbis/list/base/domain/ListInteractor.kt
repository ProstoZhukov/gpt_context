package ru.tensor.sbis.list.base.domain

import io.reactivex.disposables.Disposable
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

/**
 * Набор команд для получения данных экрана списка и изменения его отображения..
 *
 * @param ENTITY тип "бизнес модели"(БМ) экрана списка.
 */
interface ListInteractor<ENTITY : ListScreenEntity> {

    /**
     * Сбросить состояние пагинации(если были данные), загрузить данные с первой страницы [entity]
     * и отобразить ее через [view] показав индикатор прогресса.
     * Метод вызывается сразу при показе экрана списка, и должен быть вызнан при обновление БМ,
     * например изменение фильтра или строки для поиска.
     */
    fun firstPage(entity: ENTITY, view: View<ENTITY>): Disposable

    /**
     * Загрузить данные для следующей страницы [entity] и отобразить ее через [view] показав индикатор подгрузки.
     */
    fun nextPage(entity: ENTITY, view: View<ENTITY>): Disposable

    /**
     * Загрузить данные для предыдущей страницы [entity] и отобразить ее через [view] показав индикатор подгрузки.
     */
    fun previousPage(entity: ENTITY, view: View<ENTITY>): Disposable

    /**
     * Сбросить состояние пагинации и загрузить данные с первой страницы [entity], и отобразить ее через [view] без показа индикатор прогресса.
     */
    fun refresh(entity: ENTITY, view: View<ENTITY>): Disposable

    /**
     * Освободить ресурсы, удалить подписки
     */
    fun dispose()
}