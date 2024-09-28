package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.fab_options

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.design.R

/**
 * Опции меню плавающей кнопки.
 *
 * @author da.zhukov
 */
enum class ConversationInformationFabOption(@StringRes val textRes: Int) {

    ADD_FILE(R.string.communicator_files_fab_menu_option_add_file),

    CREATE_FOLDER(R.string.communicator_files_fab_menu_option_create_folder)
}