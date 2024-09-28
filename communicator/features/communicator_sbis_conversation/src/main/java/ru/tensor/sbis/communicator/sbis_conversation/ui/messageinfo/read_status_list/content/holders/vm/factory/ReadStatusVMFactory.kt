package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.factory

import ru.tensor.sbis.communicator.common.data.mapper.asNative
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatus
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.ReadStatusVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.ReadStatusVMImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.data.ReadStatusModelImpl
import javax.inject.Inject

/**
 * Фабрика вью-моделей элементов списка статусов прочитанности сообщения.
 * @see [ReadStatusVM]
 *
 * @author vv.chekurda
 */
internal interface ReadStatusVMFactory {

    /**
     * Создать вью-модель статуса прочитанности.
     *
     * @param cppModel модель контроллера статуса прочитанности сообщения.
     */
    fun create(cppModel: MessageReceiverReadStatus): ReadStatusVM
}

/**
 * Реализация фабрики вью-моделей элементов списка статусов прочитанности сообщения.
 * @see [ReadStatusModelImpl]
 */
internal class ReadStatusVMFactoryImpl @Inject constructor() : ReadStatusVMFactory {

    override fun create(cppModel: MessageReceiverReadStatus) = with(cppModel.profile.asNative) {
        ReadStatusVMImpl(
            ReadStatusModelImpl(
                personUuid = uuid,
                fullName = READ_STATUS_PERSON_NAME_MASK.format(name.lastName, name.firstName),
                photoUrl = photoUrl,
                departmentOrCompany = companyOrDepartment,
                readDateTime = cppModel.readDatetime,
                isRead = cppModel.read,
                initialsStubData = initialsStubData
            )
        )
    }
}

private const val READ_STATUS_PERSON_NAME_MASK = "%s %s"

