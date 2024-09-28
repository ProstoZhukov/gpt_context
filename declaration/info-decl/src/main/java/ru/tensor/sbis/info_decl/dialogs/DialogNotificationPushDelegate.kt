package ru.tensor.sbis.info_decl.dialogs

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Делегат управления пушами по уведомлениям.
 *
 * @author dv.baranov
 */
interface DialogNotificationPushDelegate : Feature {

    /**
     * Очистить все пуши по уведомлениям.
     */
    fun cleanAllPushTypes()

    /**
     * Включить пуши по уведомлениям.
     */
    fun enablePushes()

    /**
     * Отключить пуши по уведомлениям.
     */
    fun disablePushes()
}