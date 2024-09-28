package ru.tensor.sbis.red_button.utils

import android.app.Activity
import android.content.res.Resources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.red_button.R
import ru.tensor.sbis.red_button.data.RedButtonOpenAction
import ru.tensor.sbis.red_button.data.RedButtonStubType
import ru.tensor.sbis.red_button.ui.host.HostFragment
import ru.tensor.sbis.red_button.ui.stub.RedButtonStubActivity
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon

/**
 * Хелпер для открытия фрагмента и диалоговых окон "Красной Кнопки"
 * Инкапсулирует логику открытия [HostFragment] и отображения диалоговых окон
 *
 * @author ra.stepanov
 */
class RedButtonOpenHelper @Inject constructor() {

    /**
     * Возвращает хост фрагмент красной кнопки
     * @return [HostFragment]
     */
    fun getRedButtonHost(): Fragment = HostFragment.newInstance()

    /**
     * Возвращает диалоговое окно для красной кнопки
     * @param action событие открытия
     * @param resources класс для работы с ресурсами
     * @return диалоговое окно описывающее операцию
     */
    fun getDialog(
        action: RedButtonOpenAction,
        resources: Resources
    ): DialogFragment = if (action == RedButtonOpenAction.OPEN_DIALOG_MANAGEMENT) {
        getUUDialog(resources)
    } else {
        getEmptyDialog(resources)
    }

    /**
     * Открывает активность заглушки красной кнопки
     * @param activity родительская активность
     * @param stubType тип заглушки
     */
    fun openRedButtonStub(activity: Activity, stubType: RedButtonStubType) {
        RedButtonStubActivity.openStub(activity, stubType)
    }

    /**
     * Возвращает созданное диалоговое окно для режима работы "Управленческий Учёт"
     * @param resources класс для работы с ресурсами
     */
    private fun getUUDialog(resources: Resources): DialogFragment {
        return PopupConfirmation.newSimpleInstance(DIALOG_CODE_RED_BUTTON_DIALOG)
            .requestTitle(resources.getString(R.string.red_button_uu_alert_message))
            .requestPositiveButton(resources.getString(RCommon.string.dialog_button_ok), false)
            .requestNegativeButton(resources.getString(RCommon.string.common_folder_edit_dialog_cancle))
            .setEventProcessingRequired(true)
    }

    /**
     * Возвращает созданное диалоговое окно для режима работы "Пустой кабинет"
     * @param resources класс для работы с ресурсами
     */
    private fun getEmptyDialog(resources: Resources): DialogFragment {
        return PopupConfirmation
            .newMessageInstance(
                DIALOG_CODE_RED_BUTTON_DIALOG,
                resources.getString(R.string.red_button_cloud_alert_message)
            )
            .requestTitle(resources.getString(R.string.red_button_cloud_alert_title))
            .requestPositiveButton(resources.getString(RCommon.string.dialog_button_ok), false)
            .requestNegativeButton(resources.getString(RCommon.string.common_folder_edit_dialog_cancle))
            .setEventProcessingRequired(true)
    }
}

internal const val DIALOG_CODE_RED_BUTTON_DIALOG = 1005