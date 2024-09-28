package ru.tensor.sbis.toolbox_decl.share.content

import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState

/**
 * Делегат шапки меню "поделиться".
 *
 * @author vv.chekurda
 */
interface ShareMenuHeaderDelegate {

    /**
     * Изменить видимость кнопки назад.
     */
    fun changeBackButtonVisibility(isVisible: Boolean)

    /**
     * Изменить состояние загрузки контента "поделиться".
     */
    fun changeLoadingState(state: ShareMenuLoadingState)
}