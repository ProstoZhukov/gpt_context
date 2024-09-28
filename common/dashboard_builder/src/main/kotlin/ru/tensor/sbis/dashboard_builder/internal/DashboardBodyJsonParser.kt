package ru.tensor.sbis.dashboard_builder.internal

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.tensor.sbis.dashboard_service.generated.DashboardBuilder
import ru.tensor.sbis.dashboard_service.generated.DashboardRenderer
import ru.tensor.sbis.dashboard_service.generated.DashboardService
import ru.tensor.sbis.jsonconverter.generated.RichTextHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFormattedTextAttributes
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFormattedTextAttributesHandler
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributes
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributesHandler
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardSize
import ru.tensor.sbis.widget_player.converter.WidgetBodyEvent
import ru.tensor.sbis.widget_player.converter.WidgetBodySaxParser
import ru.tensor.sbis.widget_player.converter.WidgetDataError
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.attributes.store.MapAttributesStore
import ru.tensor.sbis.widget_player.converter.beginFrame
import ru.tensor.sbis.widget_player.converter.beginText
import ru.tensor.sbis.widget_player.converter.beginTextWidget
import ru.tensor.sbis.widget_player.converter.beginWidget
import ru.tensor.sbis.widget_player.converter.endFrame
import ru.tensor.sbis.widget_player.converter.endWidget
import timber.log.Timber
import java.util.UUID

/**
 * @author am.boldinov
 */
internal class DashboardBodyJsonParser(
    private val dashboardId: String,
    private val size: DashboardSize,
    private val throbberVisibility: MutableSharedFlow<Boolean>,
    private val dataChangedStream: SendChannel<WidgetBodyEvent>
) : WidgetBodySaxParser {

    private val dataChanges = mutableListOf<WidgetBodyEvent.DataChanged>()

    fun getDataChanges(): List<WidgetBodyEvent.DataChanged> {
        return dataChanges.toList()
    }

    private var builder: DashboardBuilder? = null

    override fun parse(
        handler: RichTextHandler,
        formattedTextAttributesHandler: SabyDocMteFormattedTextAttributesHandler,
        frameHeaderAttributesHandler: SabyDocMteFrameHeaderAttributesHandler,
        aggregateAttributes: Boolean
    ) {
        dataChanges.clear()
        val renderer = DashboardFrameRenderer(
            handler,
            formattedTextAttributesHandler,
            frameHeaderAttributesHandler,
            throbberVisibility,
            dataChangedStream,
            dataChanges
        )
        DashboardService.instance()
            .getDashboardBuilder(dashboardId, renderer, size.toCppSize()).also {
                builder = it
            }.build()
    }

    /**
     * Вручную очищаем ссылку для остановки рендерера (приостанавливается в деструкторе билдера).
     */
    fun dispose() {
        builder = null
    }

    private fun DashboardSize.toCppSize() = when (this) {
        DashboardSize.XS -> ru.tensor.sbis.dashboard_service.generated.DashboardSize.XS
        DashboardSize.S -> ru.tensor.sbis.dashboard_service.generated.DashboardSize.S
        DashboardSize.M -> ru.tensor.sbis.dashboard_service.generated.DashboardSize.M
        DashboardSize.L -> ru.tensor.sbis.dashboard_service.generated.DashboardSize.L
    }
}

private class DashboardFrameRenderer(
    private val handler: RichTextHandler,
    private val formattedTextAttributesHandler: SabyDocMteFormattedTextAttributesHandler,
    private val frameHeaderAttributesHandler: SabyDocMteFrameHeaderAttributesHandler,
    private val throbberVisibility: MutableSharedFlow<Boolean>,
    private val dataChangedStream: SendChannel<WidgetBodyEvent>,
    private val dataChanges: MutableList<WidgetBodyEvent.DataChanged>
) : DashboardRenderer() {

    private val tagIds = mutableMapOf<String, String>()
    private val frameRootId = UUID.randomUUID().toString()

    override fun onBeginBuildDashboard(header: SabyDocMteFrameHeaderAttributes) {
        frameHeaderAttributesHandler.onFrameHeaderAttributes(header)
        // контроллер не присылает frame в отличии от дефолтного парсера
        handler.beginFrame(id = frameRootId)
    }

    override fun onBeginTextWidget(id: String, data: SabyDocMteFormattedTextAttributes, text: String) {
        // добавляем в дерево текстовый виджет
        handler.beginTextWidget(id)
        // заполняем текст у добавленного виджета
        handler.beginText(text)
        // заполняем атрибуты добавленного виджета
        formattedTextAttributesHandler.onFormattedTextAttributes(data)
    }

    override fun onBeginWidget(
        type: String,
        id: String,
        settings: HashMap<String, String>,
        data: HashMap<String, String>?
    ) {
        handler.beginWidget(
            tag = type,
            attributes = settings.also {
                it[WidgetAttributes.ID] = id
            }
        )
        tagIds[id] = type
        if (data != null) {
            dataChanges.add(
                WidgetBodyEvent.DataChanged(
                    WidgetID(id), MapAttributesStore(data)
                )
            )
        }
    }

    override fun onEndBuildDashboard() {
        handler.endFrame()
        tagIds.clear()
    }

    override fun onEndTextWidget(id: String) {
        onEndWidget(id)
    }

    override fun onEndWidget(id: String) {
        handler.endWidget(tagIds[id] ?: id)
    }

    override fun onRemoveThrobber() {
        throbberVisibility.tryEmit(false)
    }

    override fun onShowThrobber() {
        throbberVisibility.tryEmit(true)
    }

    override fun onWidgetDataAvailable(id: String, data: HashMap<String, String>) {
        dataChangedStream.trySendBlocking(
            WidgetBodyEvent.DataChanged(
                WidgetID(id), MapAttributesStore(data)
            )
        ).apply {
            if (isClosed) {
                Timber.d("onWidgetDataAvailable: DashboardRenderer channel is CLOSED, widget data with id $id will not be sent")
            } else if (isFailure) {
                Timber.e("onWidgetDataAvailable: widget data with id $id was not sent, an error occurred while sending to the channel")
            } else if (isSuccess) {
                Timber.d("onWidgetDataAvailable: widget data with id $id was sent successfully")
            }
        }
    }

    override fun onWidgetDataError(id: String, errorText: String) {
        // прикладник кинул ошибку во время получения данных (непредвиденная)
        dataChangedStream.trySendBlocking(
            WidgetBodyEvent.DataError(
                WidgetID(id), WidgetDataError(errorText)
            )
        )
    }

}