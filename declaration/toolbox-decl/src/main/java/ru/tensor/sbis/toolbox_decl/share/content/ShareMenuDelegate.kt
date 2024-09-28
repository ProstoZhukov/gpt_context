package ru.tensor.sbis.toolbox_decl.share.content

import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode

/**
 * Делегат меню "поделиться".
 *
 * @author vv.chekurda
 */
interface ShareMenuDelegate : ShareMenuHeaderDelegate {

    /**
     * Размер нижнего отступа до контента.
     */
    val bottomOffset: StateFlow<Int>

    /**
     * Изменить видимость навигационной панели меню.
     */
    fun changeNavPanelVisibility(isVisible: Boolean)

    /**
     * Изменить режим определения высоты меню.
     */
    fun changeHeightMode(mode: ShareMenuHeightMode)

    /**
     * Закрыть меню.
     */
    fun dismiss()
}