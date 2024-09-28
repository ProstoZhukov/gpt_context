package ru.tensor.sbis.share_menu.ui.view.header

import androidx.annotation.StringRes
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState

/**
 * Состояния шапки меню шаринга.
 * @param textResId - ресурс текста, который отображаем в данном состоянии.
 *
 * @author dv.baranov
 */
internal enum class ShareHeaderViewState(@StringRes val textResId: Int) {

    /**
     * Начальное состояние, когда пользователь выбирает куда пошарить контент.
     * Отображаем *Отправить* и количество файлов, если они есть.
     */
    DEFAULT(R.string.share_menu_header_view_title_default),

    /**
     * Состояние отправки в прогрессе.
     * Отображаем *Отправляем* и количество файлов, если они есть, также крутилку.
     */
    SENDING(R.string.share_menu_header_view_title_sending),

    /**
     * Состояние, когда контент доставлен.
     * Отображаем *Отправлено* и галочку.
     */
    COMPLETED(R.string.share_menu_header_view_title_completed)
}

/**
 * Маппер из [ShareMenuLoadingState] в [ShareHeaderViewState]
 */
internal fun ShareMenuLoadingState.toShareHeaderViewState(): ShareHeaderViewState = when (this) {
    ShareMenuLoadingState.None -> ShareHeaderViewState.DEFAULT
    ShareMenuLoadingState.Done -> ShareHeaderViewState.COMPLETED
    else -> ShareHeaderViewState.SENDING
}