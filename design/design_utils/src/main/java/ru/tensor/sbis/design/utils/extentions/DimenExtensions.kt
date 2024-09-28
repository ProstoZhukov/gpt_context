package ru.tensor.sbis.design.utils.extentions

import android.view.View
import androidx.annotation.DimenRes

/**
 * Расширение для получения Dimen из ресурсов с помощью resources
 */
fun View.getDimenFrom(@DimenRes dimenResId: Int): Int = resources.getDimensionPixelSize(dimenResId)
