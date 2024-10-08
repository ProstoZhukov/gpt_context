package ru.tensor.sbis.design.custom_view_tools.utils

import android.view.View.MeasureSpec
import androidx.annotation.Px

/**
 * Утилиты для создания [MeasureSpec].
 *
 * @author vv.chekurda
 */
object MeasureSpecUtils {

    private val unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

    /**
     * Создать [MeasureSpec] с модом [MeasureSpec.AT_MOST] для измерения view c ограничением в [size].
     */
    fun makeAtMostSpec(@Px size: Int): Int =
        MeasureSpec.makeMeasureSpec(size, MeasureSpec.AT_MOST)

    /**
     * Создать [MeasureSpec] с модом [MeasureSpec.UNSPECIFIED] для измерения view в размерах, которые ей необходимы.
     */
    fun makeUnspecifiedSpec(): Int = unspecifiedSpec

    /**
     * Создать [MeasureSpec] с модом [MeasureSpec.UNSPECIFIED] для измерения view в размерах, которые ей необходимы.
     */
    fun makeUnspecifiedSpec(size: Int): Int =  MeasureSpec.makeMeasureSpec(size, MeasureSpec.UNSPECIFIED)

    /**
     * Создать [MeasureSpec] с модом [MeasureSpec.EXACTLY] для измерения view в размере [size].
     */
    fun makeExactlySpec(@Px size: Int): Int =
        MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)

    /**
     * Получить размер стороны View согласно спецификации [MeasureSpec].
     *
     * @param measureSpec спецификация размера [MeasureSpec].
     * @param unspecifiedSize геттер размера, которое хочет принять View без ограничений со стороны родителя.
     * @return размер стороны View в px.
     */
    @Px
    inline fun measureDirection(measureSpec: Int, unspecifiedSize: (Int?) -> Int): Int =
        when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.EXACTLY -> {
                MeasureSpec.getSize(measureSpec)
            }
            MeasureSpec.AT_MOST -> {
                val maxWidth = MeasureSpec.getSize(measureSpec)
                minOf(unspecifiedSize(maxWidth), maxWidth)
            }
            else -> {
                unspecifiedSize(null)
            }
        }
}