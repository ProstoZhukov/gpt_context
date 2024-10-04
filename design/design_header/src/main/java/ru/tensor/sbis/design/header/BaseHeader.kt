package ru.tensor.sbis.design.header

/**
 * Интерфейс колбэков для действий в шапке и для управления видимостью разделителя.
 *
 * @author ps.smirnyh
 */
interface BaseHeader {

    /**
     * Флаг, меняющий видимость разделителя в шапке.
     */
    var isDividerVisible: Boolean

    fun addAcceptListener(onAccept: (() -> Unit))

    fun addCloseListener(onClose: (() -> Unit))

    fun removeAcceptListener(onAccept: (() -> Unit))

    fun removeCloseListener(onClose: (() -> Unit))

    fun setAcceptButtonEnabled(isEnabled: Boolean)
}