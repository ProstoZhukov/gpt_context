package ru.tensor.sbis.communicator.communicator_navigation.contract

import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationProvider
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.person_decl.employee.person_card.PersonCardProvider
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardFragmentFactory
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import ru.tensor.sbis.info_decl.news.ui.NewsActivityProvider
import ru.tensor.sbis.calendar_decl.schedule.ViolationActivityProvider
import ru.tensor.sbis.verification_decl.verification.ui.VerificationFragmentProvider
import ru.tensor.sbis.communication_decl.videocall.ui.CallActivityProvider
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider
import ru.tensor.sbis.tasks.feature.DocumentFeature
import ru.tensor.sbis.employee.employees_declaration.EmployeesRegistryFragmentFactory
import ru.tensor.sbis.info_decl.notification.NotificationListFragmentProvider

/**
 * Внешние зависимости модуля навигации коммуникатора.
 * @see DocWebViewerFeature
 * @see NewsActivityProvider
 * @see VerificationFragmentProvider
 * @see ConversationProvider
 * @see PersonCardProvider
 * @see ViolationActivityProvider
 * @see ContactsRegistryFragmentFactory
 * @see EmployeesRegistryFragmentFactory
 * @see ThemesRegistryFragmentFactory
 * @see OpenLinkController.Provider
 * @see PersonCardFragmentFactory
 * @see DocumentFeature
 * @see CallActivityProvider
 * @see CRMConversationFragmentFactory
 *
 * @author da.zhukov
 */
interface CommunicatorNavigationDependency {

    val notificationFeature: NotificationListFragmentProvider?

    /** Фича просмотра документа в WebView */
    val docWebViewerFeature: DocWebViewerFeature?

    /** Поставщик активити новостей */
    val newsActivityProvider: NewsActivityProvider?

    /** Поставщик фрагментов для верификации */
    val verificationFragmentProvider: VerificationFragmentProvider?

    /** Поставщик активити и фрагмента диалога */
    val conversationProvider: ConversationProvider?

    /** Поставщик активити консультации */
    val crmConversationProvider: CRMConversationProvider?

    /** Поставщик фрагмента консультации */
    val crmConversationFragmentFactory: CRMConversationFragmentFactory?

    /** Поставщик карточки сотрудника */
    val personCardProvider: PersonCardProvider?

    /** Поставщик активностей экранов нарушений */
    val violationActivityProvider: ViolationActivityProvider?

    /** Фабрика фрагмента реестров диалогов и чатов */
    val themesRegistryFragmentFactory: ThemesRegistryFragmentFactory?

    /** @SelfDocumented */
    val openLinkControllerProvider: OpenLinkController.Provider?

    /** Фабрика фрагмента карточки сотрудника */
    val personCardFragmentFactory: PersonCardFragmentFactory?

    /** Фича документов */
    val documentFeature: DocumentFeature?

    /** Проставщик активити звонка */
    val callActivityProvider: CallActivityProvider?

    /** Поставщик интерфейса утилиты для отправки аналитики. */
    val analyticsUtilProvider: AnalyticsUtil.Provider?

    interface Provider {

        /** @SelfDocumented */
        val communicatorNavigationDependency: CommunicatorNavigationDependency
    }
}