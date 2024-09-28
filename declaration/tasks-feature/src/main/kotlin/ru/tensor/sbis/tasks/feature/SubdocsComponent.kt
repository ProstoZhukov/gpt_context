package ru.tensor.sbis.tasks.feature

import io.reactivex.Observable
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Общий компонент списка поддокументов.
 *
 * @author aa.sviridov
 */
interface SubdocsComponent : Feature {

    /**
     * Событие обновления поддокументов.
     */
    val items: Observable<List<AnyItem>>

    /**
     * Другие события, см. [SubdocsEvent].
     */
    val events: Observable<SubdocsEvent>

    /**
     * Ничаниет пагинацию заново. Вызывать каждый раз, когда количество поддокументов изменилось.
     * @param subtasksTotal новое общее количество поддокументов.
     */
    fun restartPaginationNow(subtasksTotal: Int)
}