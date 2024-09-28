package ru.tensor.sbis.widget_player.renderer.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.mvi_extension.create
import ru.tensor.sbis.widget_player.WidgetConverterProvider
import ru.tensor.sbis.widget_player.api.WidgetSource
import ru.tensor.sbis.widget_player.config.WidgetBodyDecoration
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.converter.WidgetBodyEvent
import ru.tensor.sbis.widget_player.converter.WidgetBodyStream
import ru.tensor.sbis.widget_player.converter.WidgetElementUpdater
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.element.TextHighlightElement
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore.Intent
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore.State
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore.Label
import ru.tensor.sbis.widget_player.renderer.store.WidgetPlayerStore.PlayingState
import timber.log.Timber

/**
 * @author am.boldinov
 */
internal class WidgetPlayerStoreFactory(
    private val storeFactory: StoreFactory,
    private val frameConverterProvider: WidgetConverterProvider
) {

    fun create(): WidgetPlayerStore = object : WidgetPlayerStore, Store<Intent, State, Label> by storeFactory.create(
        name = "WidgetPlayerStore",
        initialState = State(),
        executorFactory = {
            ExecutorImpl(frameConverterProvider)
        },
        reducer = ReducerImpl()
    ) {}

    private sealed interface Message {
        class SourceInstalled(val source: WidgetSource) : Message
        object Played : Message
        class Paused(val hasDeferredJob: Boolean) : Message
        class BodyLoaded(val body: WidgetBody) : Message
        class ElementStartChanged(val element: WidgetElement) : Message
        object ElementFinishChanged : Message
        class LoadingProgress(val visibility: Boolean) : Message
        object BodyCleared : Message
        class DecorationChanged(val decoration: WidgetBodyDecoration?) : Message
    }

    private class ExecutorImpl(
        private val frameConverterProvider: WidgetConverterProvider
    ) : CoroutineExecutor<Intent, Nothing, State, Message, Label>() {

        private val asyncDispatcher = Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            Timber.e(exception)
            scope.launch {
                dispatch(Message.BodyCleared)
            }
        }
        private val mainDispatcher get() = scope.coroutineContext

        private var playAsyncJob: Job? = null

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.Install -> {
                    if (getState().source !== intent.source) {
                        if (getState().playingState == PlayingState.PLAY) {
                            loadSource(intent.source, getState)
                        } else {
                            dispatch(Message.Paused(hasDeferredJob = true))
                        }
                        dispatch(Message.SourceInstalled(intent.source))
                    }
                }

                Intent.Pause -> {
                    val hasDeferred = (playAsyncJob?.isActive == true).also {
                        playAsyncJob?.cancel()
                        playAsyncJob = null
                    }
                    dispatch(Message.Paused(hasDeferredJob = hasDeferred))
                }

                Intent.Play -> {
                    getState().playingState.let { state ->
                        dispatch(Message.Played)
                        if (state == PlayingState.PAUSE_DEFERRED) {
                            getState().source?.let {
                                loadSource(it, getState)
                            }
                        }
                    }
                }

                is Intent.Decorate -> {
                    val current = getState().decoration
                    if ((current == null && intent.decoration.isEmpty()).not() && current != intent.decoration) {
                        dispatch(Message.DecorationChanged(intent.decoration))
                        getState().body?.applyDecoration(decoration = intent.decoration, dispatchChanges = true)
                    }
                }

                is Intent.BodyAccess -> {
                    getState().body?.let {
                        intent.action.invoke(it)
                    }
                }
            }
        }

        private fun loadSource(source: WidgetSource, getState: () -> State) {
            when (source) {
                is WidgetSource.Body -> {
                    source.body?.let {
                        it.applyDecoration(getState().decoration)
                        dispatch(Message.BodyLoaded(it))
                    } ?: run {
                        dispatch(Message.BodyCleared)
                    }
                }

                is WidgetSource.BodyStream -> {
                    dispatch(Message.BodyCleared)
                    playAsync {
                        subscribeOnStream(source.body, getState)
                    }
                }

                is WidgetSource.File -> {
                    playBody {
                        frameConverterProvider.provide(source.configuration)
                            .convertFromFile(source.filePath).also {
                                it.applyDecoration(getState().decoration)
                            }
                    }
                }

                is WidgetSource.Frame -> {
                    playBody {
                        frameConverterProvider.provide(source.configuration)
                            .convert(source.frame).also {
                                it.applyDecoration(getState().decoration)
                            }
                    }
                }
            }
        }

        private fun CoroutineScope.subscribeOnStream(stream: WidgetBodyStream, getState: () -> State) {
            launch {
                stream.body.collect { event ->
                    when (event) {
                        is WidgetBodyEvent.BodyLoaded -> {
                            event.changes.forEach { changes ->
                                event.body.applyElementChanges(changes.id) {
                                    try {
                                        onDataRefreshed(it, changes.data)
                                    } catch (e: Exception) {
                                        Timber.e(e)
                                        false
                                    }
                                }
                            }
                            event.body.applyDecoration(getState().decoration)
                            withContext(mainDispatcher) {
                                dispatch(Message.BodyLoaded(event.body))
                            }
                        }

                        is WidgetBodyEvent.DataChanged -> {
                            getState().body?.applyElementChanges(event.id) {
                                onDataRefreshed(it, event.data)
                            }?.let { updated ->
                                withContext(mainDispatcher) {
                                    dispatch(Message.ElementStartChanged(updated))
                                    dispatch(Message.ElementFinishChanged)
                                }
                            }
                        }

                        is WidgetBodyEvent.DataError -> {
                            getState().body?.applyElementChanges(event.id) {
                                onDataError(it, event.error)
                            }?.let { updated ->
                                withContext(mainDispatcher) {
                                    dispatch(Message.ElementStartChanged(updated))
                                    dispatch(Message.ElementFinishChanged)
                                }
                            }
                        }
                    }
                }
            }
            launch {
                stream.throbberVisibility.collect { visibility ->
                    withContext(mainDispatcher) {
                        dispatch(Message.LoadingProgress(visibility))
                    }
                }
            }
        }

        private fun WidgetBody.applyDecoration(decoration: WidgetBodyDecoration?, dispatchChanges: Boolean = false) {
            decoration?.apply {
                elements.findElementsByType(TextHighlightElement::class.java).forEach { textElement ->
                    if (textElement.textHighlight != textHighlight) {
                        textElement.textHighlight = textHighlight
                        if (dispatchChanges) {
                            dispatch(Message.ElementStartChanged(textElement as WidgetElement))
                            dispatch(Message.ElementFinishChanged)
                        }
                    }
                }
            }
        }

        private inline fun WidgetBody.applyElementChanges(
            id: WidgetID,
            block: WidgetElementUpdater<WidgetElement>.(element: WidgetElement) -> Boolean
        ) = elements.findElementById(id)?.takeIf { store.get(it.tag)?.dataUpdater?.block(it) ?: false }

        private fun playBody(action: suspend CoroutineScope.() -> WidgetBody) {
            dispatch(Message.BodyCleared) // TODO dispatch Loading Progress
            playAsync {
                val body = action.invoke(this)
                withContext(mainDispatcher) {
                    dispatch(Message.BodyLoaded(body))
                }
            }
        }

        private fun playAsync(action: suspend CoroutineScope.() -> Unit) {
            playAsyncJob?.cancel()
            playAsyncJob = scope.launch(asyncDispatcher, block = action)
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message) = when (msg) {
            is Message.SourceInstalled -> copy(
                source = msg.source
            )

            Message.BodyCleared -> copy(
                body = null
            )

            is Message.BodyLoaded -> copy(
                body = msg.body
            )

            is Message.LoadingProgress -> copy(
                isLoadingProcess = msg.visibility
            )

            is Message.ElementStartChanged -> copy(
                changed = msg.element
            )

            Message.ElementFinishChanged -> copy(
                changed = null
            )

            is Message.Paused -> copy(
                playingState = if (msg.hasDeferredJob) {
                    PlayingState.PAUSE_DEFERRED
                } else {
                    PlayingState.PAUSE
                }
            )

            Message.Played -> copy(
                playingState = PlayingState.PLAY
            )

            is Message.DecorationChanged -> copy(
                decoration = msg.decoration
            )
        }
    }
}