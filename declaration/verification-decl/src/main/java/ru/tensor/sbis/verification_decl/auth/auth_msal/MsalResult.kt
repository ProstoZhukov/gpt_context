package ru.tensor.sbis.verification_decl.auth.auth_msal

/**
 * Ответ MSAL, содержит имя провайдера SSO [providerName] и токен авторизации внутри MSAL [token].
 *
 * @author ar.leschev
 */
data class MsalResponse(val providerName: String = "", val token: String = "")