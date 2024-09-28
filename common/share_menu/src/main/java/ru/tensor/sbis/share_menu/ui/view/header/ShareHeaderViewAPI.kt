package ru.tensor.sbis.share_menu.ui.view.header

import android.view.View.OnClickListener

/**
 * Описание API для управления шапкой шаринга.
 *
 * @author dv.baranov
 */
internal interface ShareHeaderViewAPI {

    /**
     * Изменить отображение шапки для нового состояния.
     */
    fun onStateChanged(state: ShareHeaderViewState, countOfFiles: Int = 0)

    /**
     * Установить слушатель клика на кнопку закрытия.
     */
    fun setOnCloseListener(listener: OnClickListener)

    /**
     * Установить видимость кнопки закрытия.
     */
    fun setCloseButtonVisibility(isVisible: Boolean)

    /**
     * Установить слушатель клика на кнопку назад.
     */
    fun setOnBackListener(listener: OnClickListener)

    /**
     * Установить видимость кнопки назад.
     */
    fun setBackButtonVisibility(isVisible: Boolean)
}