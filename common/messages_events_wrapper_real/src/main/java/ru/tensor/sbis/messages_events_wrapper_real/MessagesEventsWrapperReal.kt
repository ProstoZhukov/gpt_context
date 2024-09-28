package ru.tensor.sbis.messages_events_wrapper_real

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.messages_events_wrapper.MessagesEvents
import ru.tensor.sbis.messages_events_wrapper.MessagesEventsWrapper
import java.util.*
import kotlin.collections.HashMap

/**
 * Реализация [MessagesEventsWrapper] использующая контроллер коммуникатра для получения событий.
 */
class MessagesEventsWrapperReal(
    private val messageController: DependencyProvider<MessageController>,
    override var expectedMessageUuid: UUID? = null
) : MessagesEventsWrapper {
    private var messageControllerGetAsync: Disposable = Disposables.disposed()
    private var subscription: Subscription? = null

    override fun start() {
        if (!messageControllerGetAsync.isDisposed) return

        messageControllerGetAsync = messageController.async.subscribe {
            if (subscription != null) return@subscribe

            subscription = it.dataRefreshed().subscribe(object : DataRefreshedMessageControllerCallback() {

                override fun onEvent(params: HashMap<String, String>) {
                    val messageUuid = params[MessagesEvents.EVENT_KEY_MESSAGE_ID] ?: return
                    val error = params[MessagesEvents.EVENT_KEY_ERROR] ?: return

                    // ожидается совпадение диалога, иначе не наше событие
                    if (messageUuid != expectedMessageUuid?.toString() ?: "") return
                    expectedMessageUuid = null

                    when (error) {
                        MessagesEvents.EVENT_UNATTACHED_PHONE_NUMBER_ERROR ->
                            unattachedPhoneNumberErrorSubject.onNext(Unit)
                    }
                }
            })
        }
    }

    override fun stop() {
        messageControllerGetAsync.dispose()
        subscription = null
    }

    private var unattachedPhoneNumberErrorSubject = PublishSubject.create<Unit>()
    override val unattachedPhoneNumberError: Observable<Unit>
        get() = unattachedPhoneNumberErrorSubject
}
