package ru.tensor.sbis.design.skeleton_view

import android.content.Context
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import ru.tensor.sbis.design.skeleton_view.mask.SkeletonShimmerDirection
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

/**
 * Конфигуратор UI для Skeleton объектов
 *
 * @param maskColor цвет маски для заполнения view
 * @param maskCornerRadius радиус скругления углов у view маски
 * @param showShimmer показывать или нет анимацию мерцания
 * @param shimmerColor цвет анимации мерцания
 * @param shimmerDuration длительность интервала анимации мерцания в миллисекундах
 * @param shimmerDirection направление анимации мерцания
 *
 * @author us.merzlikina
 */
class SkeletonConfig(
    @ColorInt maskColor: Int,
    maskCornerRadius: Float,
    showShimmer: Boolean,
    @ColorInt shimmerColor: Int,
    shimmerDuration: Long,
    shimmerDirection: SkeletonShimmerDirection
) : SkeletonStyle {

    @get:ColorInt
    override var maskColor: Int by observable(maskColor)
    override var maskCornerRadius: Float by observable(maskCornerRadius)
    override var showShimmer: Boolean by observable(showShimmer)
    @get:ColorInt
    override var shimmerColor: Int by observable(shimmerColor)
    override var shimmerDuration: Long by observable(shimmerDuration)
    override var shimmerDirection: SkeletonShimmerDirection by observable(shimmerDirection)

    private val valueObservers = mutableListOf<(() -> Unit)>()

    fun addValueObserver(onValueChanged: () -> Unit) {
        valueObservers.add(onValueChanged)
    }

    private fun onValueChanged() {
        valueObservers.forEach { it.invoke() }
    }

    private fun <T> observable(value: T): ReadWriteProperty<Any?, T> =
        Delegates.observable(value) { _, _, _ -> onValueChanged() }

    companion object {
        /**
         * Создать конфигурацию Skeleton
         *
         * @param context
         * @return конфигурацию для Skeleton в соответствии со стандартом разработки
         */
        @JvmStatic
        fun default(context: Context): SkeletonConfig = SkeletonConfig(
            maskColor = getColorFromTheme(context, R.attr.SkeletonView_mask_color, SkeletonView.DEFAULT_MASK_COLOR),
            maskCornerRadius = SkeletonView.DEFAULT_MASK_CORNER_RADIUS,
            showShimmer = SkeletonView.DEFAULT_SHIMMER_SHOW,
            shimmerColor = getColorFromTheme(
                context,
                R.attr.SkeletonView_shimmer_сolor,
                SkeletonView.DEFAULT_SHIMMER_COLOR
            ),
            shimmerDuration = SkeletonView.DEFAULT_SHIMMER_DURATION_IN_MILLIS,
            shimmerDirection = SkeletonView.DEFAULT_SHIMMER_DIRECTION
        )

        private fun getColorFromTheme(context: Context, attrId: Int, defaultResId: Int): Int {
            val typedValue = TypedValue()
            ContextThemeWrapper(context, getThemeRes(context)).theme.resolveAttribute(attrId, typedValue, true)
            if (typedValue.data == 0) {
                return ContextCompat.getColor(context, defaultResId)
            }

            return typedValue.data
        }

        @StyleRes
        private fun getThemeRes(context: Context): Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.skeletonViewTheme, typedValue, true)
            if (typedValue.data == 0) return R.style.SkeletonViewDefaultTheme

            return typedValue.data
        }
    }
}