package ru.tensor.sbis.toolbox_decl.share.content

/**
 * Контент меню "поделиться".
 *
 * Имплементация интерфейса на фрагмент позволяет получить делегат [ShareMenuDelegate] меню
 * для управления его внешним видом и состоянием.
 *
 * @author vv.chekurda
 */
interface ShareMenuContent {

    /**
     * Установить делегат меню "поделиться" для управления его внешним видом и состоянием.
     */
    fun setShareMenuDelegate(delegate: ShareMenuDelegate)
}