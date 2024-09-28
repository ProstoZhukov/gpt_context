package ru.tensor.sbis.communicator.common.contract

import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communicator.common.crud.ThemeRepositoryProvider
import ru.tensor.sbis.communicator.common.push.CommunicatorPushSubscriberProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.person_decl.profile.PersonActivityStatusNotifier
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper

/**
 * Интерфейс внешних зависимостей модуля communicator_common.
 *
 * @author vv.chekurda
 */
interface CommunicatorCommonDependency :
    MessagesPushManagerProvider,
    ThemeRepositoryProvider,
    CommunicatorPushSubscriberProvider,
    AttachmentModelMapperFactory,
    EmployeeProfileControllerWrapper.Provider,
    PersonActivityStatusNotifier {

    /** Поставщик интерфейса сервиса "пожаловаться" */
    val complainServiceProvider: ComplainService.Provider?
}
