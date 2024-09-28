package ru.tensor.sbis.widget_player.converter.attributes.resource

import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore

/**
 * @author am.boldinov
 */
internal object WidgetResourceFactory {

    fun create(type: String, value: String?, attributes: AttributesStore): WidgetResource {
        val data = if (value.isNullOrEmpty()) {
            ResourceValue.Empty
        } else when (type.lowercase()) {
            "sbisdisk" -> ResourceValue.SbisDisk(UUIDUtils.fromString(value) ?: UUIDUtils.NIL_UUID)
            "internal" -> ResourceValue.Internal(value)
            "filetransfer" -> ResourceValue.FileTransfer(value)
            "blob" -> ResourceValue.Blob(value)
            else -> ResourceValue.External(value)
        }
        return object : WidgetResource {
            override val value: ResourceValue<*> = data
            override val attributes: AttributesStore = attributes
        }
    }
}