package ru.tensor.sbis.list.base.domain

import androidx.lifecycle.LifecycleObserver
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.boundary.Repository
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.domain.fetcher.EntityCreationSubscriber
import ru.tensor.sbis.list.base.domain.fetcher.RepositoryFetcher

/**
 * Реализация с использованием вспомогательного класса [fetcher] для создания подписок на получение данных.
 */
class ListInteractorImpl<ENTITY, FILTER> internal constructor(
    private val fetcher: RepositoryFetcher<ENTITY, FILTER>
) : ListInteractor<ENTITY>, LifecycleObserver
        where ENTITY : ListScreenEntity, ENTITY : FilterProvider<FILTER> {

    /**
     * Использует [repository] для выборки данных.
     */
    constructor(
        repository: Repository<ENTITY, FILTER>, showStubsImmediate: Boolean = false
    ) : this(
        RepositoryFetcher<ENTITY, FILTER>(
            repository,
            EntityCreationSubscriber(showStubsImmediate = showStubsImmediate)
        )
    )

    override fun firstPage(entity: ENTITY, view: View<ENTITY>): Disposable {
        view.showLoading()
        view.cleanState()
        clean(entity)
        return fetcher.updateListEntity(entity, entity.provideFilterForNextPage(), view)
    }

    override fun nextPage(entity: ENTITY, view: View<ENTITY>): Disposable {
        view.showLoadNext()
        return fetcher.updateListEntity(entity, entity.provideFilterForNextPage(), view)
    }

    override fun previousPage(entity: ENTITY, view: View<ENTITY>): Disposable {
        view.showPrevious()
        return fetcher.updateListEntity(entity, entity.provideFilterForPreviousPage(), view)
    }

    override fun refresh(entity: ENTITY, view: View<ENTITY>): Disposable {
        clean(entity)
        return fetcher.updateListEntity(entity, entity.provideFilterForNextPage(), view)
    }

    override fun dispose() {
        fetcher.clean()
    }

    private fun clean(entity: ENTITY) {
        entity.cleanPagesData()
    }
}