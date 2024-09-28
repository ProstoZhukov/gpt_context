package ru.tensor.sbis.widget_player.widget.image

import com.facebook.drawee.drawable.ScalingUtils
import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
import ru.tensor.sbis.widget_player.converter.WidgetElementFactory
import ru.tensor.sbis.widget_player.converter.WidgetEnvironment
import ru.tensor.sbis.widget_player.converter.attributes.resource.getAsPreviewUrl
import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore
import ru.tensor.sbis.widget_player.converter.attributes.store.getNotEmpty
import timber.log.Timber

/**
 * @author am.boldinov
 */
internal class ImageElementFactory(
    private val options: ImageOptions
) : WidgetElementFactory<ImageElement> {

    override fun create(tag: String, attributes: WidgetAttributes, environment: WidgetEnvironment): ImageElement {
        val resourceValue = attributes.resource?.value
        val resourceAttrs = attributes.resource?.attributes ?: attributes
        return ImageElement(
            tag,
            attributes,
            environment.resources,
            request = ImageRequest(
                previewUrl = resourceValue?.getAsPreviewUrl(environment.context),
                naturalWidth = resourceAttrs.getImageSize("naturalWidth"),
                naturalHeight = resourceAttrs.getImageSize("naturalHeight"),
                constraint = attributes.getSizeConstraint(),
                roundingParams = ImageRoundingParams(
                    cornerRadius = options.borderRadius.getValue(environment.context)
                )
            )
        )
    }

    private fun AttributesStore.getSizeConstraint(): ImageSizeConstraint {
        fun formatSize(value: String?): Int {
            return value?.replace("[^0-9]".toRegex(), "")?.toIntOrNull() ?: 0
        }
        return getNotEmpty("width").let {
            val width = formatSize(it)
            val height = formatSize(getNotEmpty("height"))
            val scaleType = getScaleType()
            if (it?.contains("%") == true) {
                ImageSizeConstraint.Percent(width, height, scaleType)
            } else if (width > 0 || height > 0) {
                ImageSizeConstraint.Pixel(width, height, scaleType)
            } else {
                ImageSizeConstraint.Cover(scaleType)
            }
        }
    }

    private fun AttributesStore.getScaleType(): ScalingUtils.ScaleType {
        return when (get("imageFit")) {
            "cover" -> ScalingUtils.ScaleType.CENTER_CROP
            "contain" -> ScalingUtils.ScaleType.FIT_CENTER
            "fill" -> ScalingUtils.ScaleType.FIT_XY
            else -> ScalingUtils.ScaleType.FIT_CENTER
        }
    }

    private fun AttributesStore.getImageSize(key: String): Int {
        val rawSize = get(key)
        if (rawSize != null) {
            try {
                return rawSize.toInt()
            } catch (e: NumberFormatException) {
                Timber.d(e)
            }
        }
        return 0
    }
}