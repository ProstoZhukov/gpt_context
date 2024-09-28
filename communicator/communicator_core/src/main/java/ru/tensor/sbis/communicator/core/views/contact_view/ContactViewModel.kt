package ru.tensor.sbis.communicator.core.views.contact_view

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.custom_view_tools.TextLayoutConfig
import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Модель данных контакта.
 * @property photoData данные для отображения в компонентах фото.
 * @property title заголовок.
 * @property subtitle подзаголовок.
 * @property subtitleSecond второй подзаголовок.
 * @property roleIcon иконка роли.
 * @property titleParamsConfigurator доп. настройки для заголовка.
 * @property subtitleParamsConfigurator доп. настройки для подзаголовка.
 * @property needShowContactIcon true, чтобы отобразить иконку переписки с контактом.
 * @property needShowVideoCallIcon true, чтобы отобразить иконку видеозвонка.
 *
 * @author rv.krohalev
 */
data class ContactViewModel(
    val photoData: PhotoData,
    val title: String,
    val subtitle: String,
    val subtitleSecond: String = StringUtils.EMPTY,
    val roleIcon: String? = null,
    val titleParamsConfigurator: TextLayoutConfig? = null,
    val subtitleParamsConfigurator: TextLayoutConfig? = null,
    val needShowContactIcon: Boolean = false,
    val needShowVideoCallIcon: Boolean = true,
)