package ru.tensor.sbis.widget_player.widget.image

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import ru.tensor.sbis.mvi_extension.attachTo
import ru.tensor.sbis.viewer.decl.slider.ThumbnailListDisplayArgs
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory
import ru.tensor.sbis.viewer.decl.viewer.ImageUri
import ru.tensor.sbis.viewer.decl.viewer.ImageViewerArgs
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import ru.tensor.sbis.widget_player.converter.WidgetBody
import ru.tensor.sbis.widget_player.layout.internal.HostAccess
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.controller.WidgetController
import ru.tensor.sbis.widget_player.widget.image.ImageEventProvider.Event

/**
 * @author am.boldinov
 */
internal class ImageWidgetController(
    private val context: WidgetContext,
    private val viewerSliderIntentFactory: ViewerSliderIntentFactory?,
    private val eventProvider: ImageEventProvider
) : WidgetController<ImageElement>() {

    override fun onCreate() {
        bind {
            eventProvider.events.bindTo {
                handleImageEvents(it)
            }
        }.attachTo(lifecycle, BinderLifecycleMode.CREATE_DESTROY)
    }

    private fun handleImageEvents(event: Event) {
        when (event) {
            Event.ImageClicked -> {
                viewerSliderIntentFactory?.let { intentFactory ->
                    val clicked = elementFlow.value
                    hostAccessor.accessTo(HostAccess.Body(action = { body ->
                        body.toViewerArgs(clicked)?.let { args ->
                            context.startActivity(
                                intentFactory.createViewerSliderIntent(
                                    context = context,
                                    args = args
                                )
                            )
                        }
                    }))
                }
            }
        }
    }

    private fun WidgetBody.toViewerArgs(current: ImageElement): ViewerSliderArgs? {
        val viewerArgsList = arrayListOf<ViewerArgs>()
        var position = 0
        elements.findElementsByType(ImageElement::class.java).forEachIndexed { index, image ->
            if (image === current) {
                position = index
            }
            viewerArgsList.add(
                ImageViewerArgs(ImageUri(image.previewUrl ?: ""), "")
            )
        }
        return viewerArgsList.takeIf { it.isNotEmpty() }?.let {
            ViewerSliderArgs(
                it,
                position,
                ThumbnailListDisplayArgs(true)
            )
        }
    }
}