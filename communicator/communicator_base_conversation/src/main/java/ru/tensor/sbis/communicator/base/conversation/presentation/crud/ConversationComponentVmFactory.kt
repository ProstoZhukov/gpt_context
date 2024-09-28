package ru.tensor.sbis.communicator.base.conversation.presentation.crud

import android.util.Log
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.generated.DecoratedOfMessage
import ru.tensor.sbis.communicator.generated.HierarchyCollectionObserverOfMessage
import ru.tensor.sbis.communicator.generated.HierarchyCollectionOfMessage
import ru.tensor.sbis.communicator.generated.ItemWithIndexOfDecoratedOfMessage
import ru.tensor.sbis.communicator.generated.PathModelOfMessageMapOfStringString
import ru.tensor.sbis.communicator.generated.TupleOfUuidOptionalOfBool
import ru.tensor.sbis.crud4.ComponentViewModel
import ru.tensor.sbis.crud4.createComponentViewModel
import ru.tensor.sbis.crud4.data.DisposableObserver
import ru.tensor.sbis.crud4.data.HierarchyObserverWrapper
import ru.tensor.sbis.crud4.domain.ItemMapper
import ru.tensor.sbis.crud4.domain.ItemWithIndex
import ru.tensor.sbis.crud4.domain.ObserverCallback
import ru.tensor.sbis.crud4.view.StubFactory
import ru.tensor.sbis.service.generated.DirectionStatus
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.Mark
import ru.tensor.sbis.service.generated.Selection
import ru.tensor.sbis.service.generated.SelectionCounter
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

typealias ConversationComponentVM<MESSAGE> = ComponentViewModel<HierarchyCollectionOfMessage, MESSAGE, MessageCollectionFilter, DecoratedOfMessage, PathModelOfMessageMapOfStringString, TupleOfUuidOptionalOfBool>

fun <MESSAGE : BaseConversationMessage> createConversationComponentVM(
    viewModelStoreOwner: ViewModelStoreOwner,
    mapper: ItemMapper<DecoratedOfMessage, MESSAGE, TupleOfUuidOptionalOfBool>,
    stubFactory: StubFactory
): ConversationComponentVM<MESSAGE> =
    createComponentViewModel<
        HierarchyCollectionOfMessage,
            HierarchyCollectionObserverOfMessageImpl,
            MessageCollectionFilter,
        ItemWithIndexOfDecoratedOfMessage,
        DecoratedOfMessage,
        MESSAGE,
        PathModelOfMessageMapOfStringString,
        TupleOfUuidOptionalOfBool
        > (
        viewModelStoreOwner = viewModelStoreOwner,
        itemWithIndexExtractor = lazy {
            object : ItemWithIndex<ItemWithIndexOfDecoratedOfMessage, DecoratedOfMessage> {
                override fun getIndex(itemWithIndex: ItemWithIndexOfDecoratedOfMessage) = itemWithIndex.index

                override fun getItem(itemWithIndex: ItemWithIndexOfDecoratedOfMessage)= itemWithIndex.item
            }
        },
        observerWrapper = object : HierarchyObserverWrapper<
                HierarchyCollectionObserverOfMessageImpl,
            ObserverCallback<ItemWithIndexOfDecoratedOfMessage, DecoratedOfMessage, PathModelOfMessageMapOfStringString>
            > {

            override fun createObserver(
                callback: ObserverCallback<ItemWithIndexOfDecoratedOfMessage, DecoratedOfMessage, PathModelOfMessageMapOfStringString>
            ) = HierarchyCollectionObserverOfMessageImpl(callback)

            override fun asDisposable(observer: HierarchyCollectionObserverOfMessageImpl) = observer
        },
        mapper = lazy { mapper },
        stubFactory = lazy { stubFactory },
        viewPostSize = ConversationListSizeSettings.pageSize,
        pageSize = ConversationListSizeSettings.listSize
    )

private class HierarchyCollectionObserverOfMessageImpl(
    private var componentObserver: ObserverCallback<ItemWithIndexOfDecoratedOfMessage, DecoratedOfMessage, PathModelOfMessageMapOfStringString>?
) : HierarchyCollectionObserverOfMessage(), DisposableObserver {

    override fun onReset(items: ArrayList<DecoratedOfMessage>) {
        Log.d("ConversationCollection", "Observer onReset")
        componentObserver?.onReset(items)
    }

    override fun onAdd(param: ArrayList<ItemWithIndexOfDecoratedOfMessage>) {
        Log.d("ConversationCollection", "Observer onAdd")
        componentObserver?.onAdd(param)
    }

    override fun onReplace(param: ArrayList<ItemWithIndexOfDecoratedOfMessage>) {
        componentObserver?.onReplace(param)
    }

    override fun onMove(param: ArrayList<IndexPair>) {
        componentObserver?.onMove(param)
    }

    override fun onRemove(index: ArrayList<Long>) {
        Log.d("ConversationCollection", "Observer onRemove")
        componentObserver?.onRemove(index)
    }

    override fun onAddStub(stubType: StubType, position: ViewPosition, message: String?) {
        Log.d("ConversationCollection", "Observer onAddStub $stubType, $position")
        componentObserver?.onAddStub(stubType, position, message)
    }

    override fun onRemoveStub() {
        componentObserver?.onRemoveStub()
    }

    override fun onAddThrobber(position: ViewPosition) {
        componentObserver?.onAddThrobber(position)
    }

    override fun onRemoveThrobber() {
        componentObserver?.onRemoveThrobber()
    }

    override fun onPath(path: ArrayList<PathModelOfMessageMapOfStringString>) {
        componentObserver?.onPath(path)
    }

    override fun onEndUpdate(haveMore: DirectionStatus) {
        Log.d("ConversationCollection", "Observer onEndUpdate")
        componentObserver?.onEndUpdate(haveMore)
    }

    override fun onBeginUpdate() {
        if (componentObserver == null) return
        Log.d("ConversationCollection", "Observer onBeginUpdate")
    }

    override fun onMark(marked: Mark) = Unit
    override fun onRestorePosition(pos: Long) = Unit
    override fun onSelect(selected: ArrayList<Selection>, counter: SelectionCounter) = Unit

    override fun dispose() {
        componentObserver = null
    }
}

