package ru.tensor.sbis.design.cloud_view.model

import androidx.annotation.IntRange

/**
 * Получатель сообщения
 *
 * @author ma.kolpakov
 */
data class ReceiverInfo(
    /**
     * Первый/единственный получатель сообщения
     */
    val receiver: PersonModel,

    /**
     * Общее количество получателей (включая [receiver])
     */
    @IntRange(from = 1)
    val count: Int = 1
)