package ru.tensor.sbis.communicator.base.conversation.presentation.crud

import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.generated.HierarchyCollectionOfMessage

/**
 * Компонент коллекции сообщений.
 *
 * @author vv.chekurda
 */
class ConversationListComponent<MESSAGE : BaseConversationMessage>(
    private val listComponent: ConversationComponentVM<MESSAGE>,
    private val storage: ConversationCollectionStorage
) : ConversationComponentVM<MESSAGE> by listComponent {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var initFilter: MessageCollectionFilter

    private val changeFilterObserver = Observer<MessageCollectionFilter?> { filter ->
        scope.launch {
            val collection = storage.changeFilter(filter ?: initFilter)
            changeCollection(collection)
        }
    }

    init {
        listComponent.onChangeFilter.observeForever(changeFilterObserver)
    }

    /**
     * Инициализировать коллекцию с фильтром [filter].
     */
    fun initCollection(filter: MessageCollectionFilter) {
        initFilter = filter
        scope.launch {
            storage.init(filter)
            val collection = storage.get()
            changeCollection(collection)
        }
    }

    /**
     * Завершить работу с подписками.
     */
    fun dispose() {
        listComponent.onChangeFilter.removeObserver(changeFilterObserver)
        storage.dispose()
        scope.cancel()
    }

    private suspend fun changeCollection(newCollection: HierarchyCollectionOfMessage) {
        withContext(Dispatchers.Main) {
            listComponent.setCollection(newCollection)
        }
    }
}