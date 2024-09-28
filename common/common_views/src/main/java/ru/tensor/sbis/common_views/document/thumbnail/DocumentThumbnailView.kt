package ru.tensor.sbis.common_views.document.thumbnail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.common_views.document.LoadImageListener

/**
 * Класс view, отвечающей за загрузку и отображение миниатюры документа
 *
 * @author sa.nikitin
 */
open class DocumentThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : SimpleDraweeView(context, attrs, defStyleAttr, defStyleRes), ControllerListener<ImageInfo?> {

    var loadThumbnailListener: LoadImageListener<ImageInfo>? = null
    var currentUri: Uri? = null

    //region size setters
    /**
     * Установить размер данной view через dimen ресурс
     *
     * @param sizeResId Ссылка на dimen ресурс, определяющий размер данной view
     */
    @SuppressLint("ResourceType")
    fun setSizeRes(@DimenRes sizeResId: Int) {
        if (sizeResId > 0) {
            setSizePx(resources.getDimensionPixelSize(sizeResId))
        }
    }

    /**
     * Установить размер данной view в пикселях
     *
     * @param size Размер данной view в пикселях
     */
    fun setSizePx(size: Int) {
        val layoutParams = layoutParams
        if (size != layoutParams.width || size != layoutParams.height) {
            layoutParams.width = size
            layoutParams.height = size
            setLayoutParams(layoutParams)
        }
    }
    //endregion

    //region width setters
    /**
     * Установить ширину данной view через dimen ресурс
     *
     * @param widthResId Ссылка на dimen ресурс, определяющий ширину данной view
     */
    @SuppressLint("ResourceType")
    fun setWidthRes(@DimenRes widthResId: Int) {
        if (widthResId > 0) {
            setWidthPx(resources.getDimensionPixelSize(widthResId))
        }
    }

    /**
     * Установить ширину данной view в пикселях
     *
     * @param width Ширина данной view в пикселях
     */
    fun setWidthPx(width: Int) {
        val layoutParams = layoutParams
        if (width != layoutParams.width) {
            layoutParams.width = width
            setLayoutParams(layoutParams)
        }
    }
    //endregion

    //region height setters
    /**
     * Установить высоту данной view через dimen ресурс
     *
     * @param heightResId Ссылка на dimen ресурс, определяющий высоту данной view
     */
    @SuppressLint("ResourceType")
    fun setHeightRes(@DimenRes heightResId: Int) {
        if (heightResId > 0) {
            setWidthPx(resources.getDimensionPixelSize(heightResId))
        }
    }

    /**
     * Установить высоту данной view в пикселях
     *
     * @param height Высота данной view в пикселях
     */
    fun setHeightPx(height: Int) {
        val layoutParams = layoutParams
        if (height != layoutParams.height) {
            layoutParams.height = height
            setLayoutParams(layoutParams)
        }
    }
    //endregion

    //region corner radius setters
    /**
     * Установить радиус скруглённости углов данной view через dimen ресурс
     *
     * @param cornerRadiusResId Ссылка на dimen ресурс, определяющий радиус скруглённости углов данной view
     */
    @SuppressLint("ResourceType")
    fun setCornerRadiusRes(@DimenRes cornerRadiusResId: Int) {
        if (cornerRadiusResId > 0) {
            setCornerRadiusPx(resources.getDimensionPixelSize(cornerRadiusResId))
        }
    }

    /**
     * Установить радиус скруглённости углов данной view в пикселях
     *
     * @param cornerRadius Радиус скруглённости углов данной view в пикселях
     */
    fun setCornerRadiusPx(cornerRadius: Int) {
        val newRoundingParams = RoundingParams.fromCornersRadius(cornerRadius.toFloat())
        if (newRoundingParams != hierarchy.roundingParams) {
            hierarchy.roundingParams = newRoundingParams
        }
    }
    //endregion

    /**
     * Метод установки параметров миниатюры для дальнейших загрузки и отображения
     *
     * @param thumbnailParams   Параметры миниатюры
     * @param autoPlayGif       Следует ли автоматически воспроизводить GIF
     */
    @JvmOverloads
    fun setParams(thumbnailParams: ThumbnailParams, autoPlayGif: Boolean = false) {
        if (currentUri != thumbnailParams.uri) {
            updateImageUri(thumbnailParams.uri, autoPlayGif)
        }
        updatePlaceholderIcon(thumbnailParams.icon)
    }

    /**
     * Метод установки параметров миниатюры для дальнейших загрузки и отображения
     *
     * @param thumbnailParams           Параметры миниатюры
     * @param autoPlayGif               Следует ли автоматически воспроизводить GIF
     * @param loadThumbnailListener     Слушатель событий загрузки миниатюры
     */
    @Deprecated("Использовать setParams с 2-мя аргументами")
    @JvmOverloads
    fun setParams(
        thumbnailParams: DocumentThumbnailParams,
        autoPlayGif: Boolean = false,
        loadThumbnailListener: LoadImageListener<ImageInfo>?
    ) {
        this.loadThumbnailListener = loadThumbnailListener
        setParams(thumbnailParams, autoPlayGif)
    }

    private fun updateImageUri(uri: Uri?, autoPlayGif: Boolean) {
        val controllerBuilder = Fresco.newDraweeControllerBuilder().apply {
            if (uri != null) {
                setImageRequest(
                    ImageRequestBuilder.newBuilderWithSource(uri)
                        .setProgressiveRenderingEnabled(true)
                        .build()
                )
            } else {
                setUri(null as Uri?)
            }
            this.retainImageOnFailure = true
            this.autoPlayAnimations = autoPlayGif
            this.oldController = controller
            this.controllerListener = this@DocumentThumbnailView
        }
        controllerBuilder.controllerListener = this
        controller = controllerBuilder.build()
        currentUri = uri
    }

    private fun updatePlaceholderIcon(placeholderDrawable: Drawable) {
        hierarchy.setPlaceholderImage(placeholderDrawable, ScalingUtils.ScaleType.CENTER_INSIDE)
    }

    //region ControllerListener
    override fun onFailure(id: String?, throwable: Throwable?) {
    }

    override fun onRelease(id: String?) {
    }

    override fun onSubmit(id: String?, callerContext: Any?) {
    }

    override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
    }

    override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
    }

    override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
        loadThumbnailListener?.onImageSuccessLoad(imageInfo)
    }
    //endregion
}
