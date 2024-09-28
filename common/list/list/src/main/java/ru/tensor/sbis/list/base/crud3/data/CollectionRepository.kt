package ru.tensor.sbis.list.base.crud3.data

import io.reactivex.Observable
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider

@Deprecated("Используй модуль crud3")
interface CollectionRepository<ENTITY, ITEM, FILTER> {

    fun create(filterProvider: FilterAndPageProvider<FILTER>): Observable<Result<ITEM>>
    fun next(): Observable<Result<ITEM>>
    fun prev(): Observable<Result<ITEM>>
    fun destroy()
    fun update(entity: ENTITY, value: List<ITEM>)
}