package ru.tensor.sbis.widget_player.widget.image

/**
 * @author am.boldinov
 */
internal data class ImageRequest(
    val previewUrl: String?,
    val naturalWidth: Int,
    val naturalHeight: Int,
    val constraint: ImageSizeConstraint,
    val roundingParams: ImageRoundingParams? = null
)