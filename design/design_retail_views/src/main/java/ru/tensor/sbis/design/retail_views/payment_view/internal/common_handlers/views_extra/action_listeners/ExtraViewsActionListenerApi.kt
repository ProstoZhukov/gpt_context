package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners

import android.view.View

/** Обобщение Api для обработки действий пользователя с View элементами - "блок дополнительных действий". */
interface ExtraViewsActionListenerApi {

    /** Установка действия [action] по нажатию на кнопку "QR код". */
    fun setQrCodeClickListener(action: () -> Unit)

    /** Установка действия [action] по нажатию на кнопку "Еще". */
    fun setMoreClickListener(action: ((View) -> Unit))

    /** Установка действия [action] по нажатию на кнопку "Отправить". */
    fun setSendClickListener(action: () -> Unit)
}