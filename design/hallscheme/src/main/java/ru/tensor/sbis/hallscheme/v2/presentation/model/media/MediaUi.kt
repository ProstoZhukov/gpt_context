package ru.tensor.sbis.hallscheme.v2.presentation.model.media

import android.graphics.BitmapShader
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor.Media
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.util.evaluateLayoutParams
import ru.tensor.sbis.hallscheme.v2.util.rotateItem
import timber.log.Timber

/**
 * Произвольная картинка на схеме зала.
 */
internal class MediaUi(private val media: Media) : HallSchemeItemUi(media) {

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw(viewGroup, null)
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw3D(viewGroup, pressedShader, unpressedShader, null)
    }

    override fun getView(viewGroup: ViewGroup): View =
        SimpleDraweeView(viewGroup.context).apply {
            evaluateLayoutParams(media)
            rotateItem(media)

            val url = media.url

            if (url != null) {
                alpha = media.opacity

                hierarchy = GenericDraweeHierarchyBuilder.newInstance(resources)
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                    .build()

                val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).build()

                controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .build()
            } else {
                Timber.w("Media url is null")
            }
        }

    override fun get3dView(viewGroup: ViewGroup): View = getView(viewGroup)
}