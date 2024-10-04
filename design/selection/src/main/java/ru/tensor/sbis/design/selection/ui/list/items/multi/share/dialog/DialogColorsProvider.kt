package ru.tensor.sbis.design.selection.ui.list.items.multi.share.dialog

/**
 * Интерфейс провайдера цветов для диалогов
 *
 * @author vv.chekurda
 */
internal interface DialogColorsProvider {
    /** Цвет иконки исходящего сообщения */
    val outgoingIconColor: Int

    /** Цвет иконки входящего непрочитанного сообщения */
    val incomingUnreadIconColor: Int

    /** Цвет иконки при ошибке */
    val errorIconColor: Int

    /** Цвет темы диалога */
    val dialogTitle: Int
}