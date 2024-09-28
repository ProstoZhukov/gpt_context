package ru.tensor.sbis.communicator.push

import ru.tensor.sbis.pushnotification.PushContentCategory

/**
 * Категория пушей по сообщениям.
 */
class MessageContentCategory : PushContentCategory

/**
 * Категория пушей по каналам.
 */
class ChannelContentCategory : PushContentCategory

/**
 * Категория пушей службы поддержки для клиентов.
 */
class SupportClientConversationCategory : PushContentCategory

/**
 * Категория пушей службы поддержки (Поддержка СБИС).
 */
class SupportSabyConversationCategory : PushContentCategory {
    companion object{
        const val SABY_SUPPORT_TITLE = "Поддержка СБИС"
        const val SETTY_SUPPORT_TITLE = "Поддержка Setty"
        const val SETTY_KZ_SUPPORT_TITLE = "Setty қолдауы"
    }
}
/**
 * Категория пушей по чатам CRM.
 */
class CRMContentCategory : PushContentCategory