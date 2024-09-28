package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.ContactProfile
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.stubview.StubViewContent
import java.util.Date

/**
 * Данные, для отображения в реестре контактов
 *
 * @author da.zhukov
 */

sealed class ContactRegistryModel

/**
 * Модель контакта для реестра контактов
 *
 * @property contact                 модель профиля контакта
 * @property lastMessageDate         время последнего сообщения для фильтра "по дате"
 * @property formatterDateTime       отформатированная дата для отображения (форматирование происходит перед привязкой в адаптере)
 * @property isSegmentDividerVisible true, если необходимо отображать разделитель "Найдено еще в сотрудниках"
 */
@Parcelize
internal data class ContactsModel @JvmOverloads constructor(
    val contact: ContactProfile,
    val lastMessageDate: Date? = null,
    var isSegmentDividerVisible: Boolean = false,
) : ContactRegistryModel(),
    Parcelable {

    @IgnoredOnParcel
    var formatterDateTime: FormattedDateTime? = null
}

/**
 * Модель заглушки для реестра контактов
 */
internal data class ContactsStubModel(val content: StubViewContent? = null) : ContactRegistryModel()

/**
 * Модель папок для реестра контактов
 */
@Parcelize
internal object ContactFoldersModel : ContactRegistryModel(), Parcelable