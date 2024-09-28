package ru.tensor.sbis.widget_player.widget.image

import android.graphics.drawable.ColorDrawable
import com.arkivanov.mvikotlin.core.view.BaseMviView
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.image.ImageEventProvider.Event

/**
 * @author am.boldinov
 */
internal class ImageRenderer(
    context: WidgetContext,
    private val options: ImageOptions
) : WidgetRenderer<ImageElement>, BaseMviView<Any, Event>(), ImageEventProvider {

    override val view = ImageView(context).apply {
        val verticalMargin = options.verticalMargin.getValuePx(context)
        setDefaultWidgetLayoutParams().apply {
            topMargin = verticalMargin
            bottomMargin = verticalMargin
        }
        with(hierarchy) {
            setPlaceholderImage(
                ColorDrawable(
                    options.placeholderColor.getValue(context)
                )
            )
            fadeDuration = options.fadeDurationMillis
        }
        setOnClickListener {
            dispatch(Event.ImageClicked)
        }
    }

    override fun render(element: ImageElement) {
        view.setImageRequest(element.request)
    }
}