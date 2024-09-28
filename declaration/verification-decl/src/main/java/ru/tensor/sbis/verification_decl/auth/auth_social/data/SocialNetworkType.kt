package ru.tensor.sbis.verification_decl.auth.auth_social.data

import androidx.annotation.StringRes
import ru.tensor.sbis.verification_decl.R

/**
 * Социальные сети для профилей и персонала.
 *
 * @author ra.temnikov
 */
enum class SocialNetworkType(val typeName: String, @StringRes val title: Int) {
    VK("VK", R.string.verification_decl_social_network_vk),
    FACEBOOK("FB", R.string.verification_decl_social_network_fb),
    GOOGLE("Google", R.string.verification_decl_social_network_google),
    YANDEX("Yandex", R.string.verification_decl_social_network_yandex),
    ODNOKLASSNIKI("OK", R.string.verification_decl_social_network_ok),
    MAILRU("Mail", R.string.verification_decl_social_network_mail_ru),
    GOSUSLUGI("Esia", R.string.verification_decl_social_network_gosuslugi)
}