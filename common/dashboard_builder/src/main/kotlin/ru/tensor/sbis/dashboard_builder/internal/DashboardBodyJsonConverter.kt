package ru.tensor.sbis.dashboard_builder.internal

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import ru.tensor.sbis.dashboard_builder.DashboardPlugin
import ru.tensor.sbis.dashboard_builder.config.DashboardConfiguration
import ru.tensor.sbis.dashboard_service.generated.DashboardChangedCallback
import ru.tensor.sbis.dashboard_service.generated.DashboardService
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardSize
import ru.tensor.sbis.widget_player.converter.WidgetBodyEvent
import ru.tensor.sbis.widget_player.converter.WidgetBodyStream
import ru.tensor.sbis.widget_player.converter.WidgetBodyJsonConverter
import timber.log.Timber

/**
 * @author am.boldinov
 */
internal class DashboardBodyJsonConverter(
    configuration: DashboardConfiguration
) : DashboardBodyConverter {

    private val bodyConverter = WidgetBodyJsonConverter(configuration.widgets)

    override fun convert(request: DashboardRequest): WidgetBodyStream {
        return when (request) {
            is DashboardRequest.NavxId -> {
                loadBodyStream(
                    size = request.size,
                    pageId = flow {
                        DashboardPlugin.navigationService.getPageData(request.id)
                            ?.navigationPageContentConfig?.frameId?.let { frameId ->
                                emit(
                                    Result.success(
                                        frameId.toString()
                                    )
                                )
                            } ?: emit(
                            Result.failure(
                                NoSuchElementException("PageId not found for the passed NavxId ${request.id}")
                            )
                        )
                    }
                )
            }

            is DashboardRequest.PageId -> {
                loadBodyStream(
                    size = request.size,
                    pageId = flowOf(Result.success(request.id))
                )
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun loadBodyStream(size: DashboardSize, pageId: Flow<Result<String>>): WidgetBodyStream {
        val throbberVisibility = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
        return WidgetBodyStream(
            body = pageId.flatMapConcat { pageIdResult ->
                if (pageIdResult.isSuccess) {
                    val dashboardPageId = pageIdResult.getOrThrow()
                    callbackFlow<WidgetBodyEvent> {
                        val parser = DashboardBodyJsonParser(
                            dashboardId = dashboardPageId,
                            size = size,
                            throbberVisibility = throbberVisibility,
                            dataChangedStream = this
                        )
                        var subscription: Subscription? = DashboardService.instance().dashboardChanged()
                            .subscribe(object : DashboardChangedCallback() {
                                override fun onEvent(dashboardId: String) {
                                    if (dashboardPageId == dashboardId) {
                                        trySendBlocking(
                                            WidgetBodyEvent.BodyLoaded(
                                                body = bodyConverter.convert(parser),
                                                changes = parser.getDataChanges()
                                            )
                                        )
                                    }
                                }
                            })
                        send(
                            WidgetBodyEvent.BodyLoaded(
                                body = bodyConverter.convert(parser),
                                changes = parser.getDataChanges()
                            )
                        )
                        awaitClose {
                            subscription?.disable()
                            subscription = null
                            parser.dispose()
                        }
                    }
                } else {
                    Timber.e(pageIdResult.exceptionOrNull())
                    flowOf(
                        WidgetBodyEvent.BodyLoaded(
                            body = bodyConverter.buildErrorBody()
                        )
                    )
                }
            },
            throbberVisibility = throbberVisibility
        )
    }

}