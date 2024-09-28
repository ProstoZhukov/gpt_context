package ru.tensor.sbis.viper.helper

import android.graphics.drawable.Animatable
import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.fresco_view.util.superellipse.SuperEllipsePostprocessor
import ru.tensor.sbis.design.R as RDesign

/**
 * Расширение для SimpleDraweeView. Отображает устанавливаемую картинку с маской super-ellipse.
 * Заглушка (если необходима) должна быть установлена самостоятельно в обработке ошибки.
 *
 * @param thumbImageUrl ссылка на картинку для отображения.
 * @param successHandler обработчик успешного получения картинки.
 * @param errorHandler обработчик ошибок при получении картинки.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun SimpleDraweeView.setSuperEllipseController(
    thumbImageUrl: String,
    successHandler: () -> Unit = {},
    errorHandler: () -> Unit = {}
) {
    val postprocessor = SuperEllipsePostprocessor(
        context,
        RDesign.drawable.super_ellipse_mask
    )
    val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(thumbImageUrl))
        .setPostprocessor(postprocessor)
        .build()

    val newController = Fresco.newDraweeControllerBuilder()
        .setOldController(controller)
        .setImageRequest(imageRequest)
        .setControllerListener(object : ControllerListener<ImageInfo> {

            override fun onSubmit(id: String?, callerContext: Any?) = Unit
            override fun onRelease(id: String?) = Unit

            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                successHandler()
            }

            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                successHandler()
            }

            override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {
                errorHandler()
            }

            override fun onFailure(id: String?, throwable: Throwable?) {
                errorHandler()
            }
        })
        .build()
    controller = newController
}