package ru.tensor.sbis.verification_decl.auth.auth_social

import ru.tensor.sbis.verification_decl.auth.auth_social.data.SocialAuthData

/**
 * Интерфейс обработки поступивших данных из
 * соц. сети и логики дальнейших операций с ними.
 *
 * @author ar.leschev
 */
interface SocialRequestDelegate {

    /**
     * Отправка данных.
     */
    fun sendDataAction(data: SocialAuthData)

    /**
     * Освобождение ресурсов.
     */
    fun clear()
}