@file:JvmName("PermissionDialogUtil")

/**
 * Утилита для работы с диалоговым окном разрешений.
 */

package ru.tensor.sbis.scanner.util

import android.content.Context
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.scanner.R
import ru.tensor.sbis.base_components.R as RBaseComponents

internal const val OPEN_SETTINGS_DIALOG_REQUEST_CODE = 1
private const val OPEN_SETTINGS_DIALOG_TAG = "ScannerOpenSettingsDialogTag"

/**
 * Показать новый диалог, сообщающий о необходимости предоставить разрешения.
 * @param context контекст
 * @param fragmentManager менеджер фрагментов
 */
internal fun showPermissionDialog(context: Context, fragmentManager: FragmentManager) {
    PopupConfirmation
        .newMessageInstance(OPEN_SETTINGS_DIALOG_REQUEST_CODE, context.getString(R.string.scanner_permission_dialog_text))
        .setEventProcessingRequired(true)
        .requestTitle(context.getString(R.string.scanner_permission_dialog_title))
        .requestNegativeButton(context.getString(RBaseComponents.string.base_components_dialog_button_cancel))
        .requestPositiveButton(context.getString(R.string.scanner_permission_dialog_positive))
        .show(fragmentManager, OPEN_SETTINGS_DIALOG_TAG)
}

/**
 * Соотнести полученый [requestCode] с кодом, с которым открывается диалог отсутствия необходимых разрешений
 */
internal fun isPermissionDialogRequestCode(requestCode: Int) = requestCode == OPEN_SETTINGS_DIALOG_REQUEST_CODE