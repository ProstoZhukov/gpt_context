package ru.tensor.sbis.verification_decl.auth.auth_social.data

/**
 * Тип соц. сети.
 *
 * @param isForeign является ли провайдер иностранным.
 *
 * @author ar.leschev
 */
enum class SocialProvider(val isForeign: Boolean) {
    VK(false),
    GOOGLE(true),
    YANDEX(false),
    ESIA(false),
    APPLE(true),
    EMPTY(false)
}