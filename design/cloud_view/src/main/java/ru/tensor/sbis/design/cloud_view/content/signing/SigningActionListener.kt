package ru.tensor.sbis.design.cloud_view.content.signing

import ru.tensor.sbis.design.cloud_view.CloudView

/**
 * Обработчики подписания и отклонения в [CloudView]
 *
 * @author ma.kolpakov
 */
interface SigningActionListener {
    /**
     * Обработка подписания
     */
    fun onAcceptClicked()

    /**
     * Обработка отклонения
     */
    fun onDeclineClicked()
}