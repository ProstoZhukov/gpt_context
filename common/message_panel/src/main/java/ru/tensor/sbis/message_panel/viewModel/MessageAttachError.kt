package ru.tensor.sbis.message_panel.viewModel

import ru.tensor.sbis.attachments.generated.FileInfo

data class MessageAttachError(
    val errorMessage: String,
    val fileInfo: FileInfo
)
