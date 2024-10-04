package ru.tensor.sbis.design.message_panel.decl.attachments

import java.util.UUID

/**
 * Событие обновления прогресса по загрузке вложения
 *
 * @author ma.kolpakov
 */
typealias AttachmentUploadingProgress = Pair<UUID, Int>