package ru.tensor.sbis.communicator.communicator_files.mapper

import android.content.Context
import androidx.annotation.DimenRes
import ru.tensor.sbis.attachments.ui.mapper.AttachmentCardVMMapper
import ru.tensor.sbis.attachments.ui.view.collage.util.CollageAttachmentTitleFormatter
import ru.tensor.sbis.communicator.communicator_files.data.asCommunicatorFileAttachment
import ru.tensor.sbis.communicator.generated.ThemeAttachmentViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.communicator.communicator_files.R
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileData
import ru.tensor.sbis.communicator.communicator_files.ui.ConversationFileOriginDecoration
import ru.tensor.sbis.list.view.section.SectionOptions

/**
 * Реализация ItemMapper для crud3 файлов переписки.
 *
 * @author da.zhukov.
 */
internal class CommunicatorFilesMapper(
    private val context: Context,
    private val viewHolderHelper: CommunicatorFilesHolderHelper,
    private val itemDecoration: ConversationFileOriginDecoration
) : ItemInSectionMapper<ThemeAttachmentViewModel, AnyItem> {

    private val collageAttachmentCardVmMapper: AttachmentCardVMMapper = AttachmentCardVMMapper(
        context.applicationContext,
        titleFormatter = CollageAttachmentTitleFormatter(context)
    )

    @DimenRes
    private val previewSize = R.dimen.communicator_files_attachments_collage_placeholder_size_big

    override fun map(
        item: ThemeAttachmentViewModel,
        defaultClickAction: (ThemeAttachmentViewModel) -> Unit
    ): AnyItem {
        val actionsData = mutableListOf<CommunicatorFileActionData>()
        val attachments = item.attachmentList.map {
            actionsData.add(
                CommunicatorFileActionData(
                    fileId = it.id,
                    messageId = it.messageId,
                    attachmentLink = it.urlToOpen,
                    attachmentOrigin = it.origin,
                    fileInfoViewModel = it.fileInfoViewModel
                )
            )
            collageAttachmentCardVmMapper.map(
                it.fileInfoViewModel.asCommunicatorFileAttachment(context),
                previewSize,
                previewSize,
                optimizedSearchPreviews = true
            )
        }
        return CommunicatorFilesItem(
            data = CommunicatorFileData(
                attachments = attachments,
                actionData = actionsData
            ),
            viewHolderHelper = viewHolderHelper,
            itemDecoration = itemDecoration
        )
    }

    override fun mapSection(item: ThemeAttachmentViewModel): SectionOptions {
        return ru.tensor.sbis.list.view.section.Options(
            hasDividers = false,
            hasTopMargin = false
        )
    }
}