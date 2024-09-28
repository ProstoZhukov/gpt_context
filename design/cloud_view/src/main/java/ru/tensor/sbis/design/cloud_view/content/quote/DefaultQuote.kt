package ru.tensor.sbis.design.cloud_view.content.quote

import java.util.*

/**
 * Реализация по умолчанию для [Quote]
 *
 * @author ma.kolpakov
 */
data class DefaultQuote(
    override val enclosingMessageUuid: UUID,
    override val messageUuid: UUID
) : Quote