package ru.tensor.sbis.pushnotification.controller.base.helper

import ru.tensor.sbis.pushnotification.model.PushData

/**
 * Поставщик alert текста из модели push-уведомления для отображения.
 *
 * @author am.boldinov
 */
class AlertLineProvider : PushBuildingHelper.LineProvider<PushData> {

    override fun getLine(model: PushData): String {
        return model.message.alert
    }
}