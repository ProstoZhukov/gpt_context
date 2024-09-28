/**
 * Инструменты для маппинга вложений
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.message_panel.helper

import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.common_attachments.Attachment

/** SelfDocumented */
internal fun List<FileInfo>.toAttachments() = map {
    Attachment(it.attachId.toString(), it.fileName, FileUtil.detectFileType(it.fileName), it.isOffice)
}