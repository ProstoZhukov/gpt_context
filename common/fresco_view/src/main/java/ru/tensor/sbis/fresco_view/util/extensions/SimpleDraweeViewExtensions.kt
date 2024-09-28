package ru.tensor.sbis.fresco_view.util.extensions

import android.graphics.drawable.Animatable
import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder.DEFAULT_FADE_DURATION
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * Расширение для SimpleDraweeView для поддержки progressive JPEG
 *
 * @param imageUrl - ссылка на полную картинку для отображения.
 * @param lowResImageUrl - ссылка на маленькую картинку, которая будет отображаться пока грузится полная
 * @param successHandler - успешная загрузка
 * @param errorHandler - ошибка при загрузке
 * @param fadeDuration - задержка перед показом
 * @param autoPlayAnimations - автопуск анимации (для гиф)
 */
fun SimpleDraweeView.setProgressiveImageURI(
    imageUrl: String,
    lowResImageUrl: String? = null,
    successHandler: (imageInfo: ImageInfo?) -> Unit = {},
    errorHandler: (throwable: Throwable?) -> Unit = {},
    fadeDuration: Int = DEFAULT_FADE_DURATION,
    autoPlayAnimations: Boolean = false
) {
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
        .setProgressiveRenderingEnabled(lowResImageUrl.isNullOrEmpty())
        .build()

    val newController = Fresco.newDraweeControllerBuilder()
        .setImageRequest(request)
        .setLowResImageRequest(lowResImageUrl?.let { ImageRequest.fromUri(Uri.parse(it)) })
        .setAutoPlayAnimations(autoPlayAnimations)
        .setOldController(controller)
        .setControllerListener(object : ControllerListener<ImageInfo> {

            override fun onSubmit(id: String?, callerContext: Any?) = Unit
            override fun onRelease(id: String?) = Unit

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                successHandler(imageInfo)
            }

            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                successHandler(imageInfo)
            }

            override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                errorHandler(throwable)
            }

            override fun onFailure(id: String?, throwable: Throwable?) {
                errorHandler(throwable)
            }
        })
        .build()

    hierarchy.fadeDuration = if (imageUrl.isBlank()) 0 else fadeDuration

    controller = newController
}
