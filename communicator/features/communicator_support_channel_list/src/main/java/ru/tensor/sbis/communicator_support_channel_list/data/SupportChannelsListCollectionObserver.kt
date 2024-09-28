package ru.tensor.sbis.communicator_support_channel_list.data

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.tensor.sbis.consultations.generated.CollectionObserverOfSupportChatsViewModel
import ru.tensor.sbis.consultations.generated.ItemWithIndexOfSupportChatsViewModel
import ru.tensor.sbis.consultations.generated.SupportChatsViewModel
import ru.tensor.sbis.crud3.domain.ObserverCallback
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Фабрика для SupportChatsListCollectionObserver
 */
@AssistedFactory
internal interface SupportChatsListCollectionObserverFactory {
    fun create(
        observer: ObserverCallback<ItemWithIndexOfSupportChatsViewModel, SupportChatsViewModel>,
        onStubListener: (stubType: StubType?) -> Unit
    ): SupportChannelsListCollectionObserver
}

/**
 * Реализция проксирует событие из колбека контроллера в колбек списочного компонента.
 * @param observer
 * @param onStubListener Listener для определения отсутствия сети
 */

internal class SupportChannelsListCollectionObserver @AssistedInject constructor(
    @Assisted private val observer: ObserverCallback<ItemWithIndexOfSupportChatsViewModel, SupportChatsViewModel>,
    @Assisted private val onStubListener: (stubType: StubType?) -> Unit
) :
    CollectionObserverOfSupportChatsViewModel() {


    override fun onAdd(param: ArrayList<ItemWithIndexOfSupportChatsViewModel>) {
        observer.onAdd(param)
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition) {
        observer.onAddStub(stubType, position)
        onStubListener(stubType)
    }

    override fun onAddThrobber(position: ViewPosition) {
        observer.onAddThrobber(position)
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        observer.onMove(param)
    }

    override fun onRemove(index: ArrayList<Long>) {
        observer.onRemove(index)
        onStubListener(null)
    }

    override fun onRemoveStub() {
        observer.onRemoveStub()
    }

    override fun onRemoveThrobber() {
        observer.onRemoveThrobber()
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfSupportChatsViewModel>) {
        observer.onReplace(param)
    }

    override fun onReset(items: ArrayList<SupportChatsViewModel>) {
        observer.onReset(items)
    }

    override fun onBeginUpdate() = Unit

    override fun onEndUpdate() = Unit
}