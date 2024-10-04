package ru.tensor.sbis.design.contact_data_view.model

import java.util.*

/**
 * Модель для отображения номера телефона
 *
 * @property phoneNumber номер телефона контакта
 * @property formattedPhoneNumber Форматированный номер телефона контакта
 * Если параметр определен, то для отображения будет использоваться переданное значение,
 * если нет - компонент имеет встроенный форматер и приведет к форматированному виду значение из phoneNubmer.
 * Применяется если прикладной программист сам подготавливает форматированный вариант номера
 *
 * @property additionalNumber добавочный номер
 * @property calleeId идентификатор контакта
 * @property calleeName имя контакта
 *
 * @author av.efimov1
 */
class SbisContactPhoneNumberModel(
    val phoneNumber: String,
    val formattedPhoneNumber: String? = null,
    val additionalNumber: String? = null,
    val calleeId: UUID? = null,
    val calleeName: String? = null
)