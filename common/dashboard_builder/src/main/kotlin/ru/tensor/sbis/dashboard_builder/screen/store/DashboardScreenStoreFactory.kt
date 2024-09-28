package ru.tensor.sbis.dashboard_builder.screen.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.dashboard_builder.DashboardBuilder
import ru.tensor.sbis.dashboard_builder.config.DashboardConfiguration
import ru.tensor.sbis.dashboard_builder.screen.store.DashboardScreenStore.Intent
import ru.tensor.sbis.dashboard_builder.screen.store.DashboardScreenStore.State
import ru.tensor.sbis.dashboard_builder.screen.store.DashboardScreenStore.Label
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.widget_player.api.WidgetSource
import ru.tensor.sbis.widget_player.converter.WidgetBodyStream
import javax.inject.Inject

/**
 * @author am.boldinov
 */
internal class DashboardScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory
) {

    fun create(request: DashboardRequest, configuration: DashboardConfiguration): DashboardScreenStore =
        object : DashboardScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = DashboardScreenStore::class.java.name,
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.LoadBodyStream),
            executorFactory = {
                ExecutorImpl(request, configuration)
            },
            reducer = ReducerImpl()
        ) {}

    private sealed interface Action {
        object LoadBodyStream : Action
    }

    private sealed interface Message {
        class StreamLoaded(val stream: WidgetBodyStream) : Message
        class TopNavigationLoaded(val content: SbisTopNavigationContent) : Message
    }

    private class ExecutorImpl(
        private val request: DashboardRequest,
        private val configuration: DashboardConfiguration
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {
        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                Action.LoadBodyStream -> {
                    dispatch(
                        Message.StreamLoaded(
                            stream = DashboardBuilder.build(request, configuration)
                        )
                    )
                    if (request is DashboardRequest.NavxId) {
                        dispatch(
                            Message.TopNavigationLoaded(
                                content = SbisTopNavigationContent.LargeTitle(
                                    title = PlatformSbisString.Value(StringUtils.EMPTY),
                                    navxId = request.id
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message) = when (msg) {
            is Message.StreamLoaded -> copy(
                source = WidgetSource.BodyStream(msg.stream)
            )

            is Message.TopNavigationLoaded -> copy(
                topNavigationContent = msg.content
            )
        }
    }
}