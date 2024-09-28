package ru.tensor.sbis.design.utils.image_loading

import android.graphics.Bitmap

/**
 * [View] изображения для отрисовки в компонентах фото сотрудника
 *
 * @author us.bessonov
 */
interface DrawableImageView {

    /** @SelfDocumented */
    fun getPlaceholderBitmap(width: Int, height: Int, withTint: Boolean): Bitmap?

    /** @SelfDocumented */
    fun setBitmap(bitmap: Bitmap?): Boolean

    /**
     * Задаёт обработанное изображение для непосредственного отображения
     */
    fun setPreparedBitmap(bitmap: Bitmap)

    /** @SelfDocumented */
    fun isBitmapRecycled(): Boolean

    /**
     * Содержит ли view [Bitmap], пригодный для отображения.
     */
    fun hasValidBitmap(): Boolean

}