package ru.tensor.sbis.pushnotification.controller.base.helper

import ru.tensor.sbis.pushnotification.model.PushData

/**
 * Поставщик основного текста сообщения из модели push-уведомления для отображения.
 *
 * @author am.boldinov
 */
class MessageLineProvider : PushBuildingHelper.LineProvider<PushData> {

    override fun getLine(model: PushData): String {
        return model.message.message
    }
}