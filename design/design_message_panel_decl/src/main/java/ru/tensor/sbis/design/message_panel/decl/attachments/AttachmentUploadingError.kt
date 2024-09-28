package ru.tensor.sbis.design.message_panel.decl.attachments

import java.util.UUID

/**
 * Ошибка при загрузке вложения
 *
 * @author ma.kolpakov
 */
typealias AttachmentUploadingError = Pair<UUID, String>