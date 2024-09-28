package ru.tensor.sbis.design.toolbar.util.collapsingimage

/**
 * Слушатель событий изменения состояния изображения, сворачиваемого/разворачиваемого в графической шапке.
 *
 * @author us.bessonov
 */
internal interface CollapsingImageStateListener {

    fun onStateChanged(state: CollapsingImageState)
}