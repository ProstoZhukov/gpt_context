/**
 * Класс содержит набор функций для упрощения миграции на более новые версии API
 * библиотеки 'com.mikepenz.iconics'
 *
 * @author da.pavlov1
 */

@file:JvmName("IconicUtils")

package ru.tensor.sbis.design.utils.iconics

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.view.IconicsImageView

/** Установка цвета иконки посредством ссылки на @ColorRes */
fun IconicsImageView.setColorRes(@ColorRes color: Int) {
    setColorFilter(ContextCompat.getColor(context, color))
}

/** Установка иконки по ее имени */
fun IconicsImageView.setIcon(icon: String) {
    setIcon(IconicsDrawable(context, icon))
}

/** Установка иконки по ее модели */
fun IconicsImageView.setIcon(icon: IIcon) {
    setIcon(IconicsDrawable(context, icon))
}