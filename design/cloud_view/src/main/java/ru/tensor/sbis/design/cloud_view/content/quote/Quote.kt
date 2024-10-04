package ru.tensor.sbis.design.cloud_view.content.quote

import ru.tensor.sbis.design.cloud_view.CloudView
import java.util.*

/**
 * Бизнес модель цитаты для [CloudView]
 *
 * @author ma.kolpakov
 */
interface Quote {
    val enclosingMessageUuid: UUID
    val messageUuid: UUID
}