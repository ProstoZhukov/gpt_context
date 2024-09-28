package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store.CrmReassignCommentStore.Intent
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store.CrmReassignCommentStore.Label
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.comment.store.CrmReassignCommentStore.State
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.mvi_extension.rx.RxJavaExecutor

/**
 * Фабрика стора, основная бизнес логика экрана.
 *
 * @author da.zhukov
 */
internal class CrmReassignCommentStoreFactory(
    private val storeFactory: StoreFactory,
    private val params: CRMAnotherOperatorParams
) {

    /** @SelfDocumented */
    fun create(): CrmReassignCommentStore =
        object : CrmReassignCommentStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "CrmReassignCommentStore",
                initialState = State(StringUtils.EMPTY),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = { ExecutorImpl(params) },
                reducer = ReducerImpl()
            ) {}

    private class ExecutorImpl(
        val params: CRMAnotherOperatorParams
    ) : RxJavaExecutor<Intent, Action, State, Message, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ReassignClick -> {
                    publish(Label.ReassignClick(params.copy(message = intent.comment.toString())))
                }
            }
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = State("")
    }

    private sealed interface Action

    private sealed interface Message
}