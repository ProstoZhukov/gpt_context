/**
 * Синглтон маппера вьюмоделей вложений для коллажа
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.cloud_view.content.utils

import android.annotation.SuppressLint
import android.content.Context
import ru.tensor.sbis.attachments.ui.mapper.AttachmentCardVMMapper
import ru.tensor.sbis.attachments.ui.view.collage.util.CollageAttachmentTitleFormatter

@SuppressLint("StaticFieldLeak")
private lateinit var vmMapper: AttachmentCardVMMapper

/** @SelfDocumented */
internal fun getCollageAttachmentCardVmMapper(context: Context): AttachmentCardVMMapper {
    if (!::vmMapper.isInitialized) {
        vmMapper = AttachmentCardVMMapper(
            context.applicationContext,
            titleFormatter = CollageAttachmentTitleFormatter(context)
        )
    }
    return vmMapper
}