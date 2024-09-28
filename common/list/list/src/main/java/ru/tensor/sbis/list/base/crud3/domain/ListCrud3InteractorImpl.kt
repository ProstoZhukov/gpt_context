package ru.tensor.sbis.list.base.crud3.domain

import androidx.lifecycle.LifecycleObserver
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.list.base.crud3.data.*
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity
import timber.log.Timber

/**
 * Реализация с использованием вспомогательного класса [fetcher] для создания подписок на получение данных.
 */
@Deprecated("Используй модуль crud3")
class ListCrud3InteractorImpl<ENTITY, ITEM, FILTER>(
    private val repository: CollectionRepository<ENTITY, ITEM, FILTER>
) : ListInteractor<ENTITY>, LifecycleObserver
        where ENTITY : ListScreenEntity, ENTITY : FilterProvider<FILTER>, ENTITY : PagingListScreenEntity<FILTER> {

    override fun firstPage(entity: ENTITY, view: View<ENTITY>): Disposable {
        view.cleanState()
        entity.cleanPagesData()
        return repository.create(entity.provideFilterForNextPage()).subscribe {
            showState(it, entity, view)
        }
    }

    override fun nextPage(entity: ENTITY, view: View<ENTITY>): Disposable {
        view.showLoadNext()
        return repository.next().subscribe {
            entity.increasePage()
            showState(it, entity, view)
        }
    }

    override fun previousPage(entity: ENTITY, view: View<ENTITY>): Disposable {
        view.showPrevious()
        return repository.prev().subscribe {
            entity.decreasePage()
            showState(it, entity, view)
        }
    }

    private fun showState(
        it: Result<ITEM>?,
        entity: ENTITY,
        view: View<ENTITY>
    ) {
        when (it) {
            is Items -> {
                repository.update(entity, it.value)
                view.showData(entity)
            }
            is AddProgress -> view.showLoading()
            is Stub -> {
                view.showStub(entity)
            }
            is RemoveProgress -> {
                if (entity.isData()) view.showData(entity)
                else view.showStub(entity)
            }
            is RemoveStub -> view.showData(entity)
            null -> Timber.e("State result was null")
        }
    }

    override fun refresh(entity: ENTITY, view: View<ENTITY>): Disposable {
        return firstPage(entity, view)
    }

    override fun dispose() {
        repository.destroy()
    }
}