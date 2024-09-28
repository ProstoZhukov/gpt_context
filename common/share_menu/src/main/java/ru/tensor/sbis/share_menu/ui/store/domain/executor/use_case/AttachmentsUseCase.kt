package ru.tensor.sbis.share_menu.ui.store.domain.executor.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.share_menu.R
import ru.tensor.sbis.share_menu.utils.base_mvi.executor.BaseExecutorUseCase
import ru.tensor.sbis.share_menu.ui.store.domain.Label
import ru.tensor.sbis.share_menu.ui.store.domain.Message
import ru.tensor.sbis.share_menu.ui.store.domain.State
import ru.tensor.sbis.toolbox_decl.share.ShareData
import javax.inject.Inject

/**
 * Use-case по работе с вложениями в меню шаринга.
 *
 * @author vv.chekurda
 */
internal class AttachmentsUseCase @Inject constructor(
    private val shareData: ShareData,
    private val fileUriUtil: FileUriUtil,
) : BaseExecutorUseCase<State, Message, Label>() {

    /**
     * Выполнить действие [action] после проверки вложений.
     * Проверка включает в себя ограничения по количеству файлов и их размер.
     */
    fun withCheckAttachments(action: () -> Unit) {
        if (shareData.files.isEmpty()) {
            action()
        } else {
            val attachments = shareData.files
            scope.launch(Dispatchers.IO) {
                val errorMessageRes = if (attachments.size > MAX_ATTACHMENTS_COUNT) {
                    R.string.share_menu_attachments_count_restriction
                } else {
                    val bigFile = attachments.find { uri ->
                        fileUriUtil
                            .getFileInfo(uri, requestName = false, requestSize = true, requestMimeType = false)
                            ?.isTooBig() ?: false
                    }
                    if (bigFile != null) {
                        R.string.share_menu_attachment_big_size_restriction
                    } else {
                        -1
                    }
                }

                withContext(Dispatchers.Main) {
                    if (errorMessageRes > 0) {
                        publish(
                            Label.ShowErrorMessage(
                                message = PlatformSbisString.Res(errorMessageRes),
                                withFinish = true
                            )
                        )
                    } else {
                        action()
                    }
                }
            }
        }
    }
}

private fun FileUriUtil.FileInfo.isTooBig(): Boolean =
    size > FileUriUtil.convertMbToBytes(MAX_MESSAGE_ATTACHMENT_SIZE_MB)

private const val MAX_ATTACHMENTS_COUNT = 25
private const val MAX_MESSAGE_ATTACHMENT_SIZE_MB = 1024