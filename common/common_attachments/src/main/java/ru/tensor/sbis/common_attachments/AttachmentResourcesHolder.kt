package ru.tensor.sbis.common_attachments

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.design.R as RDesign

/**
 * Объект-холдер для вложения.
 * */
open class AttachmentResourcesHolder(@JvmField protected val mContext: Context) {
    @ColorInt
    private val mAttachmentColor = IntArray(sAttachmentTypeCount)

    private val mAttachmentIconText = arrayOfNulls<String>(sAttachmentTypeCount)

    /*** @SelfDocumented */
    @ColorInt
    fun getAttachmentColor(type: FileUtil.FileType): Int {
        val ordinal = type.ordinal
        if (mAttachmentColor[ordinal] == 0) {
            fillAttachmentColor(type)
        }
        return mAttachmentColor[ordinal]
    }

    /*** @SelfDocumented */
    fun getAttachmentIconText(type: FileUtil.FileType): String {
        val ordinal = type.ordinal
        if (mAttachmentIconText[ordinal] == null) {
            fillAttachmentIconText(type)
        }
        return mAttachmentIconText[ordinal]!!
    }

    private fun fillAttachmentColor(type: FileUtil.FileType) {
        mAttachmentColor[type.ordinal] = when (type) {
            FileUtil.FileType.AUDIO     -> ContextCompat.getColor(mContext, RDesign.color.file_type_audio)
            FileUtil.FileType.VIDEO     -> ContextCompat.getColor(mContext, RDesign.color.file_type_video)
            FileUtil.FileType.PDF       -> ContextCompat.getColor(mContext, RDesign.color.file_type_pdf)
            FileUtil.FileType.IMAGE     -> ContextCompat.getColor(mContext, RDesign.color.file_type_image)
            FileUtil.FileType.HTM,
            FileUtil.FileType.HTML,
            FileUtil.FileType.XML,
            FileUtil.FileType.XHTML,
            FileUtil.FileType.ARCHIVE,
            FileUtil.FileType.UNKNOWN   -> ContextCompat.getColor(mContext, RDesign.color.file_type_html)
            FileUtil.FileType.PPT       -> ContextCompat.getColor(mContext, RDesign.color.file_type_ppt)
            FileUtil.FileType.XLS       -> ContextCompat.getColor(mContext, RDesign.color.file_type_xls)
            FileUtil.FileType.DOC,
            FileUtil.FileType.DJVU,
            FileUtil.FileType.ODT,
            FileUtil.FileType.DOT,
            FileUtil.FileType.DOTH,
            FileUtil.FileType.DOTM,
            FileUtil.FileType.MHT,
            FileUtil.FileType.MHTML,
            FileUtil.FileType.ODC,
            FileUtil.FileType.ODF,
            FileUtil.FileType.ODG,
            FileUtil.FileType.ODI,
            FileUtil.FileType.ODM,
            FileUtil.FileType.ODP,
            FileUtil.FileType.ODS,
            FileUtil.FileType.OTC,
            FileUtil.FileType.OTF,
            FileUtil.FileType.OTG,
            FileUtil.FileType.OTH,
            FileUtil.FileType.OTI,
            FileUtil.FileType.OTP,
            FileUtil.FileType.OTS,
            FileUtil.FileType.OTT,
            FileUtil.FileType.RTF,
            FileUtil.FileType.TXT       -> ContextCompat.getColor(mContext, RDesign.color.file_type_doc)
            FileUtil.FileType.FOLDER    -> ContextCompat.getColor(mContext, RDesign.color.file_type_folder)
            else                        -> ContextCompat.getColor(mContext, RDesign.color.file_type_unknown)
        }
    }

    private fun fillAttachmentIconText(type: FileUtil.FileType) {
        mAttachmentIconText[type.ordinal] = when (type) {
            FileUtil.FileType.DOC,
            FileUtil.FileType.DOTM,
            FileUtil.FileType.RTF,
            FileUtil.FileType.OTT,
            FileUtil.FileType.ODT       -> "\ueA89"
            FileUtil.FileType.XLS       -> "\ueA8A"
            FileUtil.FileType.PDF       -> "\ueA96"
            FileUtil.FileType.PPT       -> "\ueA86"
            FileUtil.FileType.TXT       -> "\ueAE5"
            FileUtil.FileType.XML,
            FileUtil.FileType.DOT,
            FileUtil.FileType.XHTML,
            FileUtil.FileType.HTML,
            FileUtil.FileType.HTM,
            FileUtil.FileType.MHT,
            FileUtil.FileType.MHTML,
            FileUtil.FileType.ODC,
            FileUtil.FileType.OTH,
            FileUtil.FileType.DOTH,
            FileUtil.FileType.ODF,
            FileUtil.FileType.OTF,
            FileUtil.FileType.OTC       -> "\ueBA6"
            FileUtil.FileType.VSD       -> "\ueBA7"
            FileUtil.FileType.ARCHIVE   -> "\ueA8B"
            FileUtil.FileType.AUDIO     -> "\ueA87"
            FileUtil.FileType.VIDEO     -> "\ueA88"
            FileUtil.FileType.IMAGE,
            FileUtil.FileType.OTG,
            FileUtil.FileType.ODG,
            FileUtil.FileType.ODI,
            FileUtil.FileType.DJVU,
            FileUtil.FileType.OTI,
            FileUtil.FileType.PHOTOSHOP -> "\ueA84"
            FileUtil.FileType.URL       -> "\ue972"
            FileUtil.FileType.FOLDER    -> "\ueA83"
            else                        -> "\ueA01"
        }
    }

    private companion object {
        private val sAttachmentTypeCount = FileUtil.FileType.values().size
    }
}