package ru.tensor.sbis.pushnotification.repository.model

import ru.tensor.sbis.pushnotification.PushType

/**
 * Модель данных, содержащая типы пушей для подписки
 *
 * @author am.boldinov
 */
data class SupportTypesData(val types: Set<PushType>)