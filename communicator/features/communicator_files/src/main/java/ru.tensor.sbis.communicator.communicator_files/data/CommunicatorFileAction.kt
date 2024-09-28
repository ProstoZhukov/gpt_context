package ru.tensor.sbis.communicator.communicator_files.data

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Enum действий с файлом.
 *
 * @param iconRes - иконка.
 * @param textRes - ресурс текста.
 * @param destructive - выделение красным цветом.
 *
 * @author da.zhukov
 */
enum class CommunicatorFileAction(
    val iconRes: SbisMobileIcon.Icon,
    @StringRes val textRes: Int,
    val destructive: Boolean = false
) {

    /** Закрепить. */
    PIN(SbisMobileIcon.Icon.smi_SwipePin, R.string.communicator_selected_message_action_pin),

    /**
     * Открепить.
     */
    UNPIN(SbisMobileIcon.Icon.smi_SwipeUnpin, R.string.communicator_selected_message_action_unpin),

    /** Удалить. */
    DELETE(SbisMobileIcon.Icon.smi_delete, R.string.communicator_selected_message_action_delete, true),

    /** Скопировать ссылку. */
    COPY_LINK(SbisMobileIcon.Icon.smi_link, ru.tensor.sbis.design.R.string.design_menu_item_copy_link),

    /** Переместить в папку. */
    MOVE_TO_FOLDER(SbisMobileIcon.Icon.smi_moveToFolder, R.string.communicator_files_selected_file_action_move_to_folder),

    /** Перейти к сообщению. */
    GO_TO_MESSAGE(SbisMobileIcon.Icon.smi_conversation, R.string.communicator_files_selected_file_action_go_to_message),
}