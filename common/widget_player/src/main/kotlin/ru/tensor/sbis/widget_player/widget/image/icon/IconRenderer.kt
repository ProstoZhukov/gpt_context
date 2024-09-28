package ru.tensor.sbis.widget_player.widget.image.icon

import com.facebook.drawee.drawable.ScalingUtils
import ru.tensor.sbis.widget_player.layout.widget.WidgetContext
import ru.tensor.sbis.widget_player.layout.widget.WidgetRenderer
import ru.tensor.sbis.widget_player.util.setDefaultWidgetLayoutParams
import ru.tensor.sbis.widget_player.widget.image.ImageSizeConstraint
import ru.tensor.sbis.widget_player.widget.image.ImageView

/**
 * @author am.boldinov
 */
internal class IconRenderer(
    context: WidgetContext
) : WidgetRenderer<IconElement> {

    override val view = ImageView(context).apply {
        setDefaultWidgetLayoutParams()
    }

    override fun render(element: IconElement) {
        with(view) {
            val size = element.style.fontSize.getValuePx(context)
            setImageRequest(element.request.copy(
                constraint = ImageSizeConstraint.Pixel(
                    width = size,
                    height = size,
                    scaleType = ScalingUtils.ScaleType.CENTER_CROP
                )
            ))
        }
    }
}