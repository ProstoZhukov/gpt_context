package ru.tensor.sbis.design.toolbar.appbar.background

import android.graphics.drawable.Animatable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.view.DraweeView
import com.facebook.imagepipeline.image.ImageInfo
import ru.tensor.sbis.design.toolbar.appbar.model.ImageBackground

/**
 * Стратегия установки фона в [DraweeView]
 *
 * @param view реализация [DraweeView] с иерархией [GenericDraweeHierarchy]. Это важно для установки заглушки из модели
 *
 * @author ma.kolpakov
 * Создан 9/23/2019
 */
internal class DraweeViewBackgroundStrategy(
    view: DraweeView<GenericDraweeHierarchy>,
    private val frescoFactory: () -> AbstractDraweeControllerBuilder<*, *, *, ImageInfo> =
        Fresco::newDraweeControllerBuilder,
    private val backgroundAspectRatioChangedCallback: AspectRatioChangeListener?
) : AbstractBackgroundStrategy<DraweeView<GenericDraweeHierarchy>>(view) {

    override fun clearBackground() {
        view.controller = null
    }

    override fun setImageBackground(model: ImageBackground) {
        model.placeholderRes?.let {
            view.hierarchy.setPlaceholderImage(it, ScalingUtils.ScaleType.CENTER_CROP)
        }

        view.controller = with(frescoFactory.invoke()) {
            oldController = view.controller
            setUri(model.imageUrl)
            controllerListener = object : BaseControllerListener<ImageInfo>() {
                override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                    imageInfo ?: return
                    val width = imageInfo.width.toFloat()
                    val height = imageInfo.height
                    backgroundAspectRatioChangedCallback?.onAspectRatioChanged(width / height)
                }
            }
            build()
        }
    }
}