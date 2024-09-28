package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.communicator_share_messages.ShareMessagesPlugin.loginInterfaceProvider
import ru.tensor.sbis.communicator.communicator_share_messages.ShareMessagesPlugin.sendMessageManagerProvider
import ru.tensor.sbis.communicator.communicator_share_messages.ShareMessagesPlugin.sendMessageUseCaseProvider
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareReducer
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data.ContactsShareLiveData
import ru.tensor.sbis.profiles.generated.EmployeeProfileController
import ru.tensor.sbis.communicator.communicator_share_messages.utils.ContactsInfoUtil
import ru.tensor.sbis.communicator.communicator_share_messages.utils.OfflineLinksUtil
import javax.inject.Inject

/**
 * Фабрика для создания вью-модели раздела шаринга в контакты.
 *
 * @property reducer редуктор экрана.
 * @property liveData live-data экрана.
 * @property quickShareHelper вспомогательная реализация для быстрого шаринга.
 * @property contactsInfoUtil утилиты для получения информации о контактах из телефонного справочника.
 * @property offlineLinksUtil утилиты для обработки ссылок в оффлайне.
 *
 * @author vv.chekurda
 */
internal class ContactsShareViewModelFactory @Inject constructor(
    private val reducer: ContactsShareReducer,
    private val liveData: ContactsShareLiveData,
    private val quickShareHelper: QuickShareHelper,
    private val contactsInfoUtil: ContactsInfoUtil,
    private val offlineLinksUtil: OfflineLinksUtil
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ContactsShareViewModel::class.java)
        @Suppress("UNCHECKED_CAST")
        return ContactsShareViewModel(
            reducer = reducer,
            liveData = liveData,
            sendMessageManager = sendMessageManagerProvider.get().getSendMessageManager(),
            sendMessageUseCase = sendMessageUseCaseProvider.get(),
            employeeProfileController = DependencyProvider.create(EmployeeProfileController::instance),
            quickShareHelper = quickShareHelper,
            contactsInfoUtil = contactsInfoUtil,
            loginInterface = loginInterfaceProvider.get(),
            offlineLinksUtil = offlineLinksUtil
        ) as T
    }
}