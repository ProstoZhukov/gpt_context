package ru.tensor.sbis.verification_decl.auth.auth_social.data

import ru.tensor.sbis.verification_decl.auth.auth_social.data.SocialAuthData.Companion.TYPE_SOC_NET
import java.io.Serializable

/**
 * Класс данных для авторизации через соц. сеть.
 *
 * @property provider соц. сеть.
 * @property code - код для esia, vk.
 * @property accessToken - для facebook и google.
 * @property type - тип атуентификации, пока надо передавать [TYPE_SOC_NET].
 *
 * @author ar.leschev
 */
data class SocialAuthData(
    val provider: SocialProvider,
    val code: String? = "",
    val accessToken: String? = "",
    val state: String? = "",
    val url: String? = "",
    val redirectUri: String? = "",
    val type: String = TYPE_SOC_NET,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
) : Serializable {

    companion object {
        private const val TYPE_SOC_NET = "soc_net"
    }
}
