package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.data

import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import java.util.*

/**
 * Интерфейс модели статуса прочитанности сообщения
 *
 * @author vv.chekurda
 */
internal interface ReadStatusModel  {

    /**
     * Идентификатор получателя
     */
    val personUuid: UUID

    /**
     * Полное имя получателя
     */
    val fullName: String

    /**
     * Url фотографии получателя
     */
    val photoUrl: String?

    /**
     * Подразделение или компания получателя
     */
    val departmentOrCompany: String

    /**
     * Время прочтения сообщения получателем
     * null - еще непрочитано
     */
    val readDateTime: Date?

    /**
     * Признак прочитанности получателем
     * true, если сообщение прочитано получателем
     */
    val isRead: Boolean

    /**
     * Заглушка для получателя без фото
     */
    val initialsStubData: InitialsStubData?
}