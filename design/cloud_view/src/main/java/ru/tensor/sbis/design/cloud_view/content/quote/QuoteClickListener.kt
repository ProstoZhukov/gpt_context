package ru.tensor.sbis.design.cloud_view.content.quote

import ru.tensor.sbis.design.cloud_view.CloudView
import java.util.*

/**
 * Подписка на нажатие по цитате в [CloudView]
 *
 * @author ma.kolpakov
 */
interface QuoteClickListener {

    fun onQuoteClicked(quotedMessageUuid: UUID)

    fun onQuoteLongClicked(quotedMessageUuid: UUID, enclosingMessageUuid: UUID) = Unit
}