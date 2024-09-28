package ru.tensor.sbis.pushnotification.controller.notification

import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.PushType

/**
 * Категория пушей по уведомлениям, полученным в период тишины
 */
class DigestContentCategory(val pushType: PushType) : PushContentCategory