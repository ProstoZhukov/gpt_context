package ru.tensor.sbis.list.base.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import io.reactivex.disposables.SerialDisposable
import ru.tensor.sbis.list.base.domain.ListInteractor
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

class ListScreenVMFactory<ENTITY : ListScreenEntity>(
    private val interactor: ListInteractor<ENTITY>,
    private val entity: ENTITY,
    private val plainListVM: PlainListVM<ENTITY> = PlainListVM(),
    private val disposable: SerialDisposable = SerialDisposable()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListScreenVMImpl(
            interactor,
            entity,
            plainListVM,
            disposable
        ) as T
    }
}

fun <ENTITY : ListScreenEntity> ViewModelStoreOwner.getViewModel(
    interactor: ListInteractor<ENTITY>,
    entity: ENTITY,
    plainListVM: PlainListVM<ENTITY> = PlainListVM(),
    disposable: SerialDisposable = SerialDisposable()
): ListScreenVMImpl<ENTITY> {
    return ViewModelProvider(
        this,
        ListScreenVMFactory(
            interactor,
            entity,
            plainListVM,
            disposable
        )
    ).get()
}