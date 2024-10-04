package ru.tensor.sbis.appdesign.cloudview.resources

import android.content.Context
import androidx.core.content.ContextCompat
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.common.util.FileUtil.FileType
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder
import ru.tensor.sbis.common_views.DetailAttachmentResourcesHolder.*

/**
 * @author ma.kolpakov
 */
internal class DemoDetailAttachmentResourcesHolder(
    private val context: Context
) : DetailAttachmentResourcesHolder {

    override fun getAttachmentColor(type: FileType): Int = ContextCompat.getColor(context, R.color.text_color_gray)

    override fun getDetailedAttachmentColor(type: Int): Int {
        return when (type) {
            DISABLED -> ContextCompat.getColor(context, android.R.color.secondary_text_dark)
            TITLE    -> ContextCompat.getColor(context, R.color.item_full_attachment_info_name_color)
            SUBTITLE -> ContextCompat.getColor(context, R.color.item_full_attachment_info_info_color)
            else     -> ContextCompat.getColor(context, R.color.item_full_attachment_info_info_color)
        }
    }

    override fun getAttachmentIconText(type: FileType): String = "PDF"
}
