package ru.tensor.sbis.list.base.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import ru.tensor.sbis.list.view.calback.ListViewListener

/**
 * Вью модель экрана списка c основной логикой взаимодействия с интерактором для пагинации и обновления.
 * @param ENTITY : ListScreenEntity
 * @property interactor ListInteractor<ENTITY>
 * @property entity ENTITY
 * @property plainListVM PlainListVM<ENTITY>
 * @constructor
 */
class ListScreenVMImpl<ENTITY : ListScreenEntity> constructor(
    private val interactor: ListInteractor<ENTITY>,
    private val entity: ENTITY,
    private val plainListVM: PlainListVM<ENTITY> = PlainListVM(),
    private val disposable: SerialDisposable = SerialDisposable()
) : ViewModel(), ListScreenVM by plainListVM,
    View<ENTITY> by plainListVM,
    ListViewListener {

    init {
        disposable.set(interactor.firstPage(entity, plainListVM))
    }

    override fun loadPrevious() {
        if (!entity.hasPrevious()) return
        disposable.set(interactor.previousPage(entity, plainListVM))
    }

    override fun loadNext() {
        if (!entity.hasNext()) return
        disposable.set(interactor.nextPage(entity, plainListVM))
    }

    override fun showRefresh() {
        disposable.set(interactor.refresh(entity, plainListVM))
    }

    override fun onCleared() {
        disposable.dispose()
    }
}