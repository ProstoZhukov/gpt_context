package ru.tensor.sbis.communicator.base.conversation.presentation.crud

import android.util.Log
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.generated.CollectionStorageOfMessageMessageFilter
import ru.tensor.sbis.communicator.generated.HierarchyCollectionOfMessage
import ru.tensor.sbis.communicator.generated.MessageCollectionStorageProvider
import ru.tensor.sbis.communicator.generated.PathModelOfMessageMapOfStringString
import ru.tensor.sbis.communicator.generated.TupleOfUuidOptionalOfBool
import ru.tensor.sbis.service.CollectionStorageProtocol
import timber.log.Timber

/**
 * Вспомогательная реализация для работы с
 * [MessageCollectionStorageProvider] и [CollectionStorageOfMessageMessageFilter].
 *
 * @author da.zhukov
 */
class ConversationCollectionStorage(
    private val storageProvider: DependencyProvider<MessageCollectionStorageProvider>
) : CollectionStorageProtocol<
    HierarchyCollectionOfMessage,
    PathModelOfMessageMapOfStringString,
    TupleOfUuidOptionalOfBool,
        MessageCollectionFilter
> {
    @get:Synchronized
    private var storage: CollectionStorageOfMessageMessageFilter? = null
    private val requiredStorage: CollectionStorageOfMessageMessageFilter
        get() = requireNotNull(storage)
    private var collection: HierarchyCollectionOfMessage? = null

    fun init(filter: MessageCollectionFilter) {
        val messageFilter = filter.getMessageFilter()
        val pagination = filter.getPaginationAnchor()
        val storageProvider = storageProvider.get()

        storage?.dispose()
        disposeCurrentCollection()

        storage = storageProvider.get(messageFilter, pagination)!!.also {
            Log.d("ConversationCollectionStorage", "initStorage $messageFilter, pagination $pagination")
        }
    }

    override fun get(): HierarchyCollectionOfMessage =
        requiredStorage.get().also {
            collection?.connect(null)
            collection?.dispose()
            collection = it
            Log.d("ConversationCollectionStorage", "get")
        }

    override fun prev() = requiredStorage.prev()

    override fun changeFilter(filter: MessageCollectionFilter): HierarchyCollectionOfMessage {
        val messageFilter = filter.getMessageFilter()
        return if (storage != null) {
            requiredStorage.changeFilter(messageFilter).also {
                disposeCurrentCollection()
                collection = it
                Log.d("ConversationCollectionStorage", "changeFilter: $messageFilter")
            }
        } else {
            Timber.e(
                IllegalStateException("Storage is not initialized on changeFilter: pagination = ${filter.getPaginationAnchor().pagination.firstOrNull()}, isGroupConv = ${filter.isGroupConversation}"),
                "ConversationCollectionStorage"
            )
            init(filter)
            get()
        }
    }

    override fun move(path: PathModelOfMessageMapOfStringString): HierarchyCollectionOfMessage =
        requiredStorage.move(path)

    override fun next(
        view: TupleOfUuidOptionalOfBool,
        folder: TupleOfUuidOptionalOfBool?
    ): HierarchyCollectionOfMessage =
        requiredStorage.next(view, folder).also {
            Log.d("ConversationCollectionStorage", "next: view = $view, folder = $folder")
        }

    override fun prev(folder: TupleOfUuidOptionalOfBool?): HierarchyCollectionOfMessage =
        requiredStorage.prev(folder).also {
            Log.d("ConversationCollectionStorage", "prev: folder = $folder")
        }

    override fun createPrev(folder: TupleOfUuidOptionalOfBool?): HierarchyCollectionOfMessage =
        requiredStorage.createPrev(folder)

    override fun dispose() {
        Log.d("ConversationCollectionStorage", "dispose")
        storage?.dispose()
        disposeCurrentCollection()
    }

    override fun commitPrev() =
        requiredStorage.commitPrev()

    override fun rollbackPrev() =
        requiredStorage.rollbackPrev()

    private fun disposeCurrentCollection() {
        collection?.dispose()
        collection?.connect(null)
    }
}