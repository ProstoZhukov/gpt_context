package ru.tensor.sbis.communicator.communicator_files.utils

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.floor

/**
 * Расширение для расчета количества вьюшек, которые могут полностью поместиться по ширине экрана.
 *
 * @param viewWidthDp ширина вьюшки в dp с учетом паддинга.
 * @param parentMarginDp отступы слева и справа родительского элемента в dp.
 * @return количество вьюшек, которые могут полностью поместиться по ширине экрана.
 */
internal fun Context.calculateQuantityOfViews(
    viewWidthDp: Float = COMMUNICATOR_FILES_VIEW_DP,
    parentMarginDp: Float = COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN
): Int {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    val screenWidthPx = displayMetrics.widthPixels

    val density = displayMetrics.density
    val viewWidthPx = (viewWidthDp * density).toInt()
    val parentMarginPx = (parentMarginDp * density * 2).toInt() // учитываем отступы с обеих сторон

    return floor((screenWidthPx - parentMarginPx) / viewWidthPx.toDouble()).toInt()
}

/**
 * Расширение для расчета необходимой ширины каждой вьюшки для полного заполнения экрана с учетом отступов родительского элемента и самой вьюшки.
 *
 * @param numberOfViews количество вьюшек, которые должны поместиться на экране.
 * @param parentMarginDp отступы слева и справа родительского элемента в dp.
 * @param viewPaddingDp отступы слева и справа самой вьюшки в dp.
 * @return необходимая ширина каждой вьюшки в пикселях для полного заполнения экрана.
 */
fun Context.calculateViewWidthForFullScreen(
    numberOfViews: Int = calculateQuantityOfViews(),
    parentMarginDp: Pair<Float, Float> = COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN to COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN,
    viewPaddingDp: Pair<Float, Float> = 0f to COMMUNICATOR_FILES_VIEW_RIGHT_PADDING_DP
): Int {
    val displayMetrics: DisplayMetrics = resources.displayMetrics
    val screenWidthPx = displayMetrics.widthPixels

    val density = displayMetrics.density
    val parentMarginPx = ((parentMarginDp.first + parentMarginDp.second) * density).toInt() // учитываем отступы с обеих сторон
    val viewPaddingPx = ((viewPaddingDp.first + viewPaddingDp.second) * density).toInt() // учитываем отступы с обеих сторон
    val totalMarginPx = parentMarginPx + numberOfViews * viewPaddingPx

    return (screenWidthPx - totalMarginPx) / numberOfViews
}


// Вычисление количества представлений, которые могут поместиться по ширине и их размера
private const val COMMUNICATOR_FILES_VIEW_RIGHT_AND_LEFT_PARENT_MARGIN = 6f
private const val COMMUNICATOR_FILES_VIEW_RIGHT_PADDING_DP = 4f
private const val COMMUNICATOR_FILES_VIEW_DP = 118F +  COMMUNICATOR_FILES_VIEW_RIGHT_PADDING_DP
