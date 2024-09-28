package ru.tensor.sbis.message_panel.di.recipients

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.RecipientsController
import ru.tensor.sbis.message_panel.interactor.recipients.DefaultMessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.mapper.ContactVMItemMapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import timber.log.Timber

/**
 * @author vv.chekurda
 */
@Module
internal class MessagePanelRecipientsModule {

    @Provides
    fun provideContactVmMapper(appContext: Context) =
        ContactVMItemMapper(appContext)

    @Provides
    fun provideRecipientsInteractor(
        employeeProfiles: DependencyProvider<EmployeeProfileControllerWrapper>?,
        recipients: DependencyProvider<RecipientsController>?,
        employees: DependencyProvider<EmployeesControllerWrapper>?,
        mapper: ContactVMItemMapper
    ): MessagePanelRecipientsInteractor? =
        if (employeeProfiles == null || recipients == null) {
            Timber.d("Message panel initialised without recipient selection. " +
                    "EmployeeProfileControllerWrapper = %s, RecipientsController = %s",
                employeeProfiles, recipients
            )
            null
        } else {
            DefaultMessagePanelRecipientsInteractor(employeeProfiles, recipients, employees, mapper)
        }
}
