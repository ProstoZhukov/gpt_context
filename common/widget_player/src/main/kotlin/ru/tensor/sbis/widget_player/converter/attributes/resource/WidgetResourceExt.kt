package ru.tensor.sbis.widget_player.converter.attributes.resource

import android.content.Context
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.richtext.util.FileUtil
import ru.tensor.sbis.richtext.util.HtmlHelper

/**
 * @author am.boldinov
 */

fun WidgetResource.sbisDiskValue() = value as? ResourceValue.SbisDisk

fun WidgetResource.externalValue() = value as? ResourceValue.External

fun WidgetResource.internalValue() = value as? ResourceValue.Internal

fun WidgetResource.fileTransferValue() = value as? ResourceValue.FileTransfer

fun WidgetResource.blobValue() = value as? ResourceValue.Blob


fun ResourceValue<*>.getAsPreviewUrl(context: Context, size: Int? = null): String? {
    return when (this) {
        is ResourceValue.External -> formatExternalImageUrl(context, source, size)

        is ResourceValue.FileTransfer -> formatExternalImageUrl(context, source, size)

        is ResourceValue.Internal -> if (source.startsWith(FileUtil.getRootCachePath(context))) { // файл из локального кеша
            FileUtil.buildFileSchemePath(source)
        } else {
            formatExternalImageUrl(context, source, size)
        }

        is ResourceValue.SbisDisk -> HtmlHelper.formatImageUrl(
            context,
            CommonUtils.createLinkByUuid(
                UrlUtils.DISK_API_V1_SERVICE_POSTFIX,
                source.toString()
            )
        )

        is ResourceValue.Blob -> source

        ResourceValue.Empty -> null
    }
}

private fun formatExternalImageUrl(context: Context, url: String, size: Int?): String? {
    return if (FileUtil.isBase64Image(url)) {
        url
    } else {
        HtmlHelper.formatImageUrl(context, url, size)
    }
}
