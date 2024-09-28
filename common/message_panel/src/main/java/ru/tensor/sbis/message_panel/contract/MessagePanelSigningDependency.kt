package ru.tensor.sbis.message_panel.contract

import ru.tensor.sbis.common_attachments.Attachment

/**
 * Делегат для управления механикой подписания из панели ввода
 *
 * @author vv.chekurda
 */
interface MessagePanelSignDelegate {

    /**
     * Возвращает `true`, если для вложений [attachments] доступно подписание
     */
    fun isSignButtonVisible(attachments: List<Attachment>): Boolean

    /**
     * Обработка нажатия на кнопку подписания в панели ввода
     */
    fun onSignButtonClicked()
}