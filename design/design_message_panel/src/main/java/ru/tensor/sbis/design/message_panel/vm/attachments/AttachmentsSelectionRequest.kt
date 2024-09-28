package ru.tensor.sbis.design.message_panel.vm.attachments

import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerPresentationParams

/**
 * Запрос выбора вложений
 *
 * @author ma.kolpakov
 */
data class AttachmentsSelectionRequest(val presentationParams: SbisFilesPickerPresentationParams?)
