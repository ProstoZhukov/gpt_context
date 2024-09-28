package ru.tensor.sbis.common_views

import android.view.View

/**
 * Слушатель одиночного нажатия на View
 *
 * @author sa.nikitin
 */
interface OnSingleTapListener {

    /**
     * Зафиксировано одиночное нажатие на View
     */
    fun onSingleTap(tappedView: View)
}