package ru.tensor.sbis.widget_player.widget.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.net.Uri
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.widget_player.layout.AttachDetachScreenViewHandler
import ru.tensor.sbis.widget_player.layout.VisibleOnScreenContentView
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @author am.boldinov
 */
internal class ImageView(context: Context) : SimpleDraweeView(context), VisibleOnScreenContentView {

    private var request: ImageRequest? = null

    private val controllerListener = object : BaseControllerListener<Any>() {
        override fun onFinalImageSet(id: String?, imageInfo: Any?, animatable: Animatable?) {
            if (screenPositionHandler.isAttachedToUserScreen()) {
                animatable?.start()
            }
        }
    }

    override val screenPositionHandler = object : AttachDetachScreenViewHandler() {
        override fun onAttachToUserScreen() {
            onAttach()
            controller?.animatable?.start()
            invalidate()
        }

        override fun onDetachFromUserScreen() {
            onDetach()
            controller?.animatable?.stop()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measureWidth = 0
        var measureHeight = 0
        request?.let { request ->
            val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
            when (val constraint = request.constraint) {
                is ImageSizeConstraint.Cover -> {
                    val factor = scaleFactor(maxWidth.toFloat(), request.naturalWidth)
                    measureWidth = maxWidth
                    measureHeight = (request.naturalHeight * factor).roundToInt()
                }

                is ImageSizeConstraint.Percent -> {
                    val desiredWidth = constraint.width.toFloat() / 100 * maxWidth
                    val factor = scaleFactor(desiredWidth, request.naturalWidth)
                    measureWidth = desiredWidth.roundToInt()
                    measureHeight = (request.naturalHeight * factor).roundToInt()
                }

                is ImageSizeConstraint.Pixel -> {
                    measureWidth = if (constraint.width > 0) min(constraint.width, maxWidth) else maxWidth
                    measureHeight = if (constraint.height > 0) constraint.height else measureWidth
                }
            }
        }
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (screenPositionHandler.isAttachedToUserScreen()) {
            super.onDraw(canvas)
        }
    }

    fun setImageRequest(request: ImageRequest?) {
        if (this.request != request) {
            this.request = request
            request?.apply {
                hierarchy.actualImageScaleType = constraint.scaleType
                hierarchy.roundingParams = roundingParams?.cornerRadius?.takeIf { it > 0 }?.let {
                    RoundingParams().setCornersRadius(it / 2f)
                }
            }
            setImageUrl(request?.previewUrl, 0, 0)
            requestLayout()
        }
    }

    private fun setImageUrl(url: String?, resizeWidth: Int, resizeHeight: Int) {
        if (url == null) {
            setImageURI(null as String?)
            return
        }
        val requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
            .setProgressiveRenderingEnabled(true)
        if (resizeWidth > 0 && resizeHeight > 0) {
            requestBuilder.resizeOptions = ResizeOptions(resizeWidth, resizeHeight)
        }
        controller = (controllerBuilder as PipelineDraweeControllerBuilder)
            .setOldController(controller)
            .setImageRequest(requestBuilder.build())
            .setRetainImageOnFailure(true)
            .setControllerListener(controllerListener)
            .build()
    }

    private fun scaleFactor(current: Float, target: Int): Float {
        return if (target > 0) current / target else 1f
    }
}