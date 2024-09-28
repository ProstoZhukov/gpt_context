package ru.tensor.sbis.decorated_link.mapper

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewImpl
import ru.tensor.sbis.linkdecorator.generated.LinkPreview as DecoratorLinkPreview

/**
 * Маппер модели контроллера
 */
internal fun DecoratorLinkPreview.mapLinkPreview(isIntentSource: Boolean = false): LinkPreviewImpl {
    return LinkPreviewImpl(
        date = date.orEmpty(),
        href = href,
        fullUrl = fullRef,
        image = image.orEmpty(),
        title = title,
        subtitle = subtitle,
        details = details.orEmpty(),
        secondDetails = secondDetails.orEmpty(),
        docUuid = id?.toString().orEmpty(),
        urlType = urlType,
        docType = mapDocType(),
        docSubtype = mapDocSubtype(),
        rawDocSubtype = docSubtype,
        isIntentSource = isIntentSource,
        parameters = parameters
    )
}