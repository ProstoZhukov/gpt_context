package ru.tensor.sbis.message_panel.view

import android.view.View
import ru.tensor.sbis.message_panel.delegate.MessagePanelFilesPickerConfig

/**
 * Интерфейс для взаимодействия view и бизнес логики выбора файлов
 *
 * @author vv.chekurda
 */
interface AttachmentsDelegate {

    var filesPickerConfig: MessagePanelFilesPickerConfig

    fun onBottomMenuClick(anchorView: View)
}