package ru.tensor.sbis.widget_player.widget.image.icon

import com.facebook.drawee.drawable.ScalingUtils
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.attributes.resource.getAsPreviewUrl
import ru.tensor.sbis.widget_player.widget.image.ImageRequest
import ru.tensor.sbis.widget_player.widget.image.ImageRoundingParams
import ru.tensor.sbis.widget_player.widget.image.ImageSizeConstraint

/**
 * @author am.boldinov
 */
internal class IconElementFactory(
    private val options: IconOptions
) : WidgetElementFactory<IconElement> {

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): IconElement {
        val resourceValue = attributes.resource?.value
        val iconSize = environment.resources.globalStyle.fontSize.getValuePx(environment.context)
        return IconElement(
            tag,
            attributes,
            environment.resources,
            request = ImageRequest(
                previewUrl = resourceValue?.getAsPreviewUrl(
                    environment.context,
                    (iconSize * 1.5).toInt() // запрашиваем с запасом качества
                ),
                naturalWidth = 0,
                naturalHeight = 0,
                constraint = ImageSizeConstraint.Pixel(
                    width = iconSize,
                    height = iconSize,
                    scaleType = ScalingUtils.ScaleType.CENTER_CROP
                ),
                roundingParams = ImageRoundingParams(
                    cornerRadius = options.borderRadius.getValue(environment.context)
                )
            )
        )
    }
}