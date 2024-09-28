package ru.tensor.sbis.verification_decl.auth.auth_social.data


/**
 * Событие с данными от социальной сети для дальнейшей обработки.
 *
 * @author ar.leschev
 */
data class SocialAuthEvent(
    val authData: SocialAuthData?,
    val error: String? = null,
    val success: Boolean,
    val navigateBack: Boolean = false,
    val showError: Boolean = true
)