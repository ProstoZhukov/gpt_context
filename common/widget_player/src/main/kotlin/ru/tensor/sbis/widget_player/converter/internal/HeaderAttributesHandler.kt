package ru.tensor.sbis.widget_player.converter.internal

import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributes
import ru.tensor.sbis.jsonconverter.generated.SabyDocMteFrameHeaderAttributesHandler
import ru.tensor.sbis.widget_player.converter.attributes.resource.WidgetResource
import ru.tensor.sbis.widget_player.converter.attributes.resource.WidgetResourceFactory
import ru.tensor.sbis.widget_player.converter.attributes.store.MapAttributesStore

/**
 * @author am.boldinov
 */
internal class HeaderAttributesHandler : SabyDocMteFrameHeaderAttributesHandler() {

    private var headerAttributes: SabyDocMteFrameHeaderAttributes? = null

    override fun onFrameHeaderAttributes(attributes: SabyDocMteFrameHeaderAttributes): Boolean {
        headerAttributes = attributes
        return true
    }

    fun replaceMetaAttributes(tag: String, attributes: HashMap<String, String>, buffer: HeaderAttributesBuffer) {
        headerAttributes?.apply {
            elementMetaTypes[tag]?.attributes?.let { metaAttributes ->
                metaAttributes.keys.forEach { metaKey ->
                    if (metaAttributes[metaKey]?.type == ConverterParams.HeaderMetaType.RESOURCE) {
                        attributes[metaKey]?.let { resourceId ->
                            resources[resourceId]?.let { resource ->
                                attributes.remove(metaKey)
                                buffer.resource = WidgetResourceFactory.create(
                                    type = resource.type,
                                    value = resource.value,
                                    attributes = MapAttributesStore(resource.rest)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun release() {
        headerAttributes = null
    }
}

internal class HeaderAttributesBuffer {
    var resource: WidgetResource? = null

    fun clear() {
        resource = null
    }
}