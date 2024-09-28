package ru.tensor.sbis.list.base.domain.boundary

import io.reactivex.Observable
import ru.tensor.sbis.list.base.data.CrudRepository
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

/**
 * Реализация репозитория должна возвращать готовую "бизнес модель"(БМ) экрана списка. Так как, списки работают в режиме
 * пагинации, то к инициализирующим данным подгруженной страницы могут быть добавлены еще страницы в начало или в конец.
 */
interface Repository<ENTITY : ListScreenEntity, FILTER> {

    /**
     * Создать [Observable], результатом выполнения которого будет добавление данных в [entity] полученных с
     * использованием [filterProvider] от микросервиса.
     * Метод вызывается каждый раз при подгрузке новой страницы из источника данных. При обновлении [entity], нужно
     * обеспечить синхронизацию по объекту.
     *
     * @see EntityFactory.updateEntityWithData
     *
     * @sample CrudRepository.update
     */
    fun update(entity: ENTITY, filterProvider: FilterAndPageProvider<FILTER>): Observable<ENTITY>

    /**
     * Почистить все ресурсы репозитория, отписать подписки. После вызова этого метода никакие другие методы
     * этого объекта уже не должны вызывться
     */
    fun destroy()
}