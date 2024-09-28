package ru.tensor.sbis.design.view_ext

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.drawable.ScalingUtils.ScaleType.CENTER_CROP
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder.DEFAULT_FADE_DURATION
import com.facebook.drawee.view.SimpleDraweeView
import ru.tensor.sbis.design.custom_view_tools.utils.dpF
import ru.tensor.sbis.design.util.IconHelper
import ru.tensor.sbis.design.utils.extentions.getColorDrawableFrom
import ru.tensor.sbis.design.utils.extentions.getColorDrawableFromAttr
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.utils.extentions.getDrawableFrom
import ru.tensor.sbis.design.view_ext.drawable.IconDrawable
import ru.tensor.sbis.fresco_view.ShapedDraweeView
import ru.tensor.sbis.fresco_view.util.extensions.setProgressiveImageURI
import timber.log.Timber
import java.util.regex.Pattern
import ru.tensor.sbis.design.R as RDesign

/**
 * Устанавливает отображение SimpleDraweeView по заданным параметрам
 *
 * @param source отображаемый ресурс
 * @param failure заглушка при ошибки загрузки изображения
 * @param placeholder фон-заглушка
 */
fun SimpleDraweeView.setImage(
    source: Source,
    failure: Failure = Failure.Drawable(RDesign.drawable.design_ic_image_holder_simple),
    placeholder: Drawable = getColorDrawableFromAttr(RDesign.attr.unaccentedBackgroundColor),
    actualImageScaleType: ScalingUtils.ScaleType? = null,
    autoPlayAnimations: Boolean = false,
    fadeDuration: Int = DEFAULT_FADE_DURATION
) {
    val failureDrawable =
        if (source is Source.Color) ColorDrawable(source.colorInt)
        else failure.let {
            when (it) {
                is Failure.Drawable -> getDrawableFrom(it.drawableRes)
                is Failure.Icon -> {
                    val failureIcon = IconHelper.getIcon(it.icon)
                    IconDrawable(context, failureIcon, resources.dpF(it.size), getColorFrom(it.backgroundColorRes))
                }

                is Failure.ColorRes -> getColorDrawableFrom(it.colorRes)
            }
        }
    hierarchy.setFailureImage(failureDrawable, CENTER_CROP)
    actualImageScaleType?.let { hierarchy.actualImageScaleType = it }
    setPlaceholderDrawable(if (source is Source.Color) ColorDrawable(source.colorInt) else placeholder)
    val (url, lowResUrl) = if (source is Source.Image) source.url to source.lowResUrl else "" to null
    setProgressiveImageURI(url, lowResUrl, autoPlayAnimations = autoPlayAnimations, fadeDuration = fadeDuration)
}

/**
 * Установить фон для вью из цвета
 */
fun SimpleDraweeView.setPlaceholderColor(@ColorInt colorInt: Int) {
    hierarchy.setPlaceholderImage(ColorDrawable(colorInt))
}

/**
 * Установить фон для вью из цвета
 */
fun SimpleDraweeView.setPlaceholderDrawable(placeholder: Drawable) {
    hierarchy.setPlaceholderImage(placeholder)
}

/**
 * Устанавливает отображение ShapedDraweeView по заданным параметрам
 *
 * @param source отображаемый ресурс
 * @param failure заглушка при ошибки загрузки изображения
 * @param placeholder фон-заглушка
 */
fun ShapedDraweeView.setImage(
    source: Source,
    failure: Failure = Failure.Drawable(RDesign.drawable.design_ic_image_holder_simple),
    shape: MaskShape = MaskShape.Square,
    placeholder: Drawable = getColorDrawableFromAttr(RDesign.attr.unaccentedBackgroundColor),
    actualImageScaleType: ScalingUtils.ScaleType? = null,
    fadeDuration: Int = DEFAULT_FADE_DURATION
) {
    (this as SimpleDraweeView).setImage(source, failure, placeholder, actualImageScaleType, fadeDuration = fadeDuration)
    setShape(shape)
}

private fun ShapedDraweeView.setShape(shape: MaskShape) {
    setShape(
        when (shape) {
            MaskShape.SuperEllipse -> getDrawableFrom(RDesign.drawable.super_ellipse_mask)
            MaskShape.Circle -> getDrawableFrom(R.drawable.saby_clients_design_circle_mask)
            MaskShape.RoundedSquare -> getDrawableFrom(R.drawable.saby_clients_design_rounded_square_mask)
            MaskShape.Square -> getColorDrawableFrom(RDesign.color.palette_color_white1)
            is MaskShape.Other -> getDrawableFrom(shape.drawableRes)
        }
    )
}

/**
 * Маска изображения и заглушки
 */
sealed class MaskShape {
    object SuperEllipse : MaskShape()
    object Circle : MaskShape()
    object Square : MaskShape()
    object RoundedSquare : MaskShape()
    data class Other(@DrawableRes val drawableRes: Int) : MaskShape()
}

/**
 * Отображаемый ресурс
 */
sealed class Source {

    data class Image(val url: String, val lowResUrl: String? = null) : Source()

    data class Color(@ColorInt val colorInt: Int) : Source()
}

/**
 * Заглушка
 */
sealed class Failure {

    data class Drawable(@DrawableRes val drawableRes: Int) : Failure()

    data class Icon(
        val icon: String,
        val size: Int = 24,
        val backgroundColorRes: Int = RDesign.color.palette_color_gray3
    ) : Failure()

    data class ColorRes(@androidx.annotation.ColorRes val colorRes: Int) : Failure()
}

/**
 * Получить размеры изображения ниже качества из оригинальной ссылки [originalImageUrl]
 * Оригинальные размеры уменьшаем в 2 раза
 */
fun getLowResImageSize(originalImageUrl: String): Pair<Int, Int>? =
    try {
        val urlMatcher = Pattern.compile("/[0-9]*/[0-9]*/").matcher(originalImageUrl)
        val sizeList =
            if (urlMatcher.find()) urlMatcher.group().split('/').filter { it.isNotBlank() }.map { it.toInt() }
            else listOf()
        if (sizeList.size == 2) {
            val width = sizeList[0]
            val height = sizeList[1]
            width to height
        } else null
    } catch (e: Exception) {
        Timber.w(e)
        null
    }
