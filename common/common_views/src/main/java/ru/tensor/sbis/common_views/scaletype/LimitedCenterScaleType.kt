package ru.tensor.sbis.common_views.scaletype

import android.graphics.Matrix
import android.graphics.Rect
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.drawable.ScalingUtils.ScaleType
import kotlin.math.max
import kotlin.math.min

internal const val MAX_SCALE_FACTOR = 5.0f

/**
 * Базовый [ScaleType] с масштабированием не более, чем [MAX_SCALE_FACTOR]. Выравнивание по центру контейнера
 *
 * @author sa.nikitin
 */
abstract class LimitedCenterScaleType : ScalingUtils.AbstractScaleType() {

    override fun getTransformImpl(
        outTransform: Matrix,
        parentRect: Rect,
        childWidth: Int,
        childHeight: Int,
        focusX: Float,
        focusY: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        val scale = min(selectScale(scaleX, scaleY), MAX_SCALE_FACTOR)
        val dx = parentRect.left + (parentRect.width() - childWidth * scale) * 0.5f
        val dy = parentRect.top + (parentRect.height() - childHeight * scale) * 0.5f
        outTransform.setScale(scale, scale)
        outTransform.postTranslate(dx + 0.5f, dy + 0.5f)
    }

    /**
     * Выбрать масштабирование: по X или по Y
     */
    abstract fun selectScale(scaleX: Float, scaleY: Float): Float
}

/**
 * Реализация [ScaleType] как [ScalingUtils.ScaleTypeCenterCrop] но масштабирование не более, чем [MAX_SCALE_FACTOR]
 *
 * @author sa.nikitin
 */
object LimitedCenterCropScaleType : LimitedCenterScaleType() {

    override fun selectScale(scaleX: Float, scaleY: Float): Float = max(scaleX, scaleY)
}

/**
 * Реализация [ScaleType] как [ScalingUtils.ScaleTypeFitCenter] но масштабирование не более, чем [MAX_SCALE_FACTOR]
 *
 * @author sa.nikitin
 */
object LimitedFitCenterScaleType : LimitedCenterScaleType() {

    override fun selectScale(scaleX: Float, scaleY: Float): Float = min(scaleX, scaleY)
}