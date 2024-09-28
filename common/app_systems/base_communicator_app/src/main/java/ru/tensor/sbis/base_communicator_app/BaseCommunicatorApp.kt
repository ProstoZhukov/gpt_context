package ru.tensor.sbis.base_communicator_app

import android.app.Application
import ru.tensor.sbis.attachments.AttachmentsPlugin
import ru.tensor.sbis.attachments.encryption.AttachmentsEncryptionPlugin
import ru.tensor.sbis.attachments.loading.AttachmentsLoadingPlugin
import ru.tensor.sbis.attachments.signing.AttachmentsSigningPlugin
import ru.tensor.sbis.attachments.ui.AttachmentsUiPlugin
import ru.tensor.sbis.auth_settings.AuthSettingsPlugin
import ru.tensor.sbis.barcodereader.BarcodeReaderPlugin
import ru.tensor.sbis.base_saby_app.BaseSabyApp
import ru.tensor.sbis.cadres.CadresPlugin
import ru.tensor.sbis.calendar.CalendarCommonPlugin
import ru.tensor.sbis.calendar.CalendarPlugin
import ru.tensor.sbis.calendar.events.create_event.CalendarCreateEventPlugin
import ru.tensor.sbis.calendar.eventcards.CalendarEventCardsPlugin
import ru.tensor.sbis.calendar_activity_tracking.CalendarTrackingPlugin
import ru.tensor.sbis.certificate.request.CertificateRequestPlugin
import ru.tensor.sbis.certificate_activation.CertificateActivationPlugin
import ru.tensor.sbis.certificate_backup.CertificateBackupPlugin
import ru.tensor.sbis.city_selector.CitySelectorPlugin
import ru.tensor.sbis.clients_impl.ClientsPlugin
import ru.tensor.sbis.communicator.CommunicatorPushPlugin
import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin
import ru.tensor.sbis.communicator.communicator_host.CommunicatorHostPlugin
import ru.tensor.sbis.communicator.communicator_navigation.CommunicatorNavigationPlugin
import ru.tensor.sbis.communicator.crud.CommunicatorCrudPlugin
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin
import ru.tensor.sbis.contractors_card.ContractorsCardPlugin
import ru.tensor.sbis.crypto_operation.DocumentSignPlugin
import ru.tensor.sbis.cryptopro_config.CryptoProPlugin
import ru.tensor.sbis.date_picker.DatePickerPlugin
import ru.tensor.sbis.decorated_link.DecoratedLinkPlugin
import ru.tensor.sbis.design.navigation.NavigationPlugin
import ru.tensor.sbis.design.profile.person.feature.PersonViewPlugin
import ru.tensor.sbis.disk.diskmain.DiskPlugin
import ru.tensor.sbis.docwebviewer.DocWebViewerPlugin
import ru.tensor.sbis.e_signatures.ESignsPlugin
import ru.tensor.sbis.edo.faces.selection.EdoFacesSelectionPlugin
import ru.tensor.sbis.edo.passage.EdoPassagePlugin
import ru.tensor.sbis.employee.EmployeePlugin
import ru.tensor.sbis.employee.employee_register.EmployeeRegisterPlugin
import ru.tensor.sbis.employee_common.EmployeeCommonPlugin
import ru.tensor.sbis.language.LanguagePlugin
import ru.tensor.sbis.link_opener.LinkOpenerPlugin
import ru.tensor.sbis.login.VerificationPlugin
import ru.tensor.sbis.login.auth_devices.devices.AuthDevicePlugin
import ru.tensor.sbis.manage_features.ManageFeaturePlugin
import ru.tensor.sbis.master.certificate.MasterCertificatePlugin
import ru.tensor.sbis.certificate_copying.CertificateCopyingPlugin
import ru.tensor.sbis.certificate_nfc_access.CertificateNfcAccessPlugin
import ru.tensor.sbis.certificate_selection.CertificateSelectionPlugin
import ru.tensor.sbis.clients_contact_list.di.ClientsContactListPlugin
import ru.tensor.sbis.clients_representative_create.ClientsRepresentativeCreatePlugin
import ru.tensor.sbis.communicator.communicator_files.CommunicatorFilesPlugin
import ru.tensor.sbis.communicator.communicator_share_messages.ShareMessagesPlugin
import ru.tensor.sbis.communicator.send_message.SendMessagePlugin
import ru.tensor.sbis.design.audio_player_view.AudioPlayerViewPlugin
import ru.tensor.sbis.design.cloud_view.CloudViewPlugin
import ru.tensor.sbis.design.files_picker.SbisFilesPickerPlugin
import ru.tensor.sbis.design.gallery.impl.GalleryPlugin
import ru.tensor.sbis.design.media_player.MediaPlayerPlugin
import ru.tensor.sbis.design.message_panel.audio_recorder.MessagePanelAudioRecorderPlugin
import ru.tensor.sbis.design.message_panel.video_recorder.MessagePanelVideoRecorderPlugin
import ru.tensor.sbis.design.message_view.MessageViewPlugin
import ru.tensor.sbis.design.period_picker.SbisPeriodPickerPlugin
import ru.tensor.sbis.design.video_message_view.VideoPlayerViewPlugin
import ru.tensor.sbis.media.MediaPlugin
import ru.tensor.sbis.meeting.MeetingPlugin
import ru.tensor.sbis.message_panel.MessagePanelPlugin
import ru.tensor.sbis.message_panel.recorder.MessagePanelRecorderPlugin
import ru.tensor.sbis.motivation.MotivationPlugin
import ru.tensor.sbis.my_profile.MyProfilePlugin
import ru.tensor.sbis.news.NewsPlugin
import ru.tensor.sbis.notification.push_history.PushNotificationHistoryPlugin
import ru.tensor.sbis.notification_settings.NotificationSettingsPlugin
import ru.tensor.sbis.person_card.PersonCardPlugin
import ru.tensor.sbis.platform_event_manager.EventManagerPlugin
import ru.tensor.sbis.plugin_manager.PluginManager
import ru.tensor.sbis.profile.ProfilePlugin
import ru.tensor.sbis.pushnotification.PushNotificationPlugin
import ru.tensor.sbis.design.recipient_selection.RecipientSelectionPlugin
import ru.tensor.sbis.edo.doc.card.extension.EdoDocCardExtensionPlugin
import ru.tensor.sbis.employee.employees_registry.EmployeesRegistryPlugin
import ru.tensor.sbis.fresco.bincontent.FrescoBinContentPlugin
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.reporting.ReportingPlugin
import ru.tensor.sbis.richtext.RichTextPlugin
import ru.tensor.sbis.sabydoc_viewer.SabyDocViewerPlugin
import ru.tensor.sbis.scanner.ScannerPlugin
import ru.tensor.sbis.schedulecontrol.ViolationMessagePanelPlugin
import ru.tensor.sbis.schedulecontrol.ViolationPlugin
import ru.tensor.sbis.settings_screen.SettingsScreenPlugin
import ru.tensor.sbis.share_menu.ShareMenuPlugin
import ru.tensor.sbis.signature.authority.SignatureAuthorityPlugin
import ru.tensor.sbis.status.ActivityStatusPlugin
import ru.tensor.sbis.storage.StoragePlugin
import ru.tensor.sbis.sync_manager.SyncManagerPlugin
import ru.tensor.sbis.user_activity_track_watcher.UserActivityTrackWatcherPlugin
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.viewer.ViewerPlugin
import ru.tensor.sbis.webviewer.WebViewerPlugin
import ru.tensor.sbis.widget_player.WidgetPlayerPlugin
import ru.tensor.sbis.motivation_impl.MotivationPlugin as NewMotivationPlugin

/**
 * Базовая конфигурация для приложения "Коммуникатор", "Saby.Tasks", "Courier".
 * Сюда входят только те плагины, которые нужны во всех приложениях линейки "Коммуникатор".
 *
 * @author kv.martyshenko
 */
open class BaseCommunicatorApp : BaseSabyApp() {
    protected val authDevicePlugin = AuthDevicePlugin
    protected val frescoPlugin = FrescoBinContentPlugin
    protected val verificationPlugin: VerificationPlugin = VerificationPlugin
    protected val versionCheckerPlugin: VersionCheckerPlugin = VersionCheckerPlugin
    protected val pushNotificationPlugin: PushNotificationPlugin = PushNotificationPlugin
    protected val languagePlugin: LanguagePlugin = LanguagePlugin
    protected val storagePlugin: StoragePlugin = StoragePlugin
    protected val eventManagerPlugin: EventManagerPlugin = EventManagerPlugin
    protected val datePickerPlugin: DatePickerPlugin = DatePickerPlugin
    protected val barcodeReaderPlugin: BarcodeReaderPlugin = BarcodeReaderPlugin
    protected val linkOpenerPlugin: LinkOpenerPlugin = LinkOpenerPlugin
    protected val recipientSelectionPlugin: RecipientSelectionPlugin = RecipientSelectionPlugin
    protected val mediaPlugin: MediaPlugin = MediaPlugin
    protected val employeePlugin: EmployeePlugin = EmployeePlugin
    protected val employeeCommonPlugin: EmployeeCommonPlugin = EmployeeCommonPlugin
    protected val personCardPlugin: PersonCardPlugin = PersonCardPlugin
    protected val myProfilePlugin: MyProfilePlugin = MyProfilePlugin
    protected val citySelectorPlugin: CitySelectorPlugin = CitySelectorPlugin
    protected val documentSignPlugin: DocumentSignPlugin = DocumentSignPlugin
    protected val cryptoProPlugin: CryptoProPlugin = CryptoProPlugin
    protected val masterCertificatePlugin: MasterCertificatePlugin = MasterCertificatePlugin
    protected val certificateRequestPlugin: CertificateRequestPlugin = CertificateRequestPlugin
    protected val userActivityTrackWatcherPlugin: UserActivityTrackWatcherPlugin = UserActivityTrackWatcherPlugin
    protected val signatureAuthorityPlugin: SignatureAuthorityPlugin = SignatureAuthorityPlugin
    protected val shareMenuPlugin: ShareMenuPlugin = ShareMenuPlugin
    protected val scannerPlugin: ScannerPlugin = ScannerPlugin
    protected val syncManagerPlugin: SyncManagerPlugin = SyncManagerPlugin
    protected val clientsPlugin: ClientsPlugin = ClientsPlugin
    protected val clientsContactListPlugin: ClientsContactListPlugin = ClientsContactListPlugin
    protected val clientsRepresentativeCreatePlugin: ClientsRepresentativeCreatePlugin = ClientsRepresentativeCreatePlugin
    protected val eSignsPlugin: ESignsPlugin = ESignsPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }
    protected val certificateCopyingPlugin: CertificateCopyingPlugin = CertificateCopyingPlugin
    protected val certificateActivationPlugin: CertificateActivationPlugin = CertificateActivationPlugin
    protected val certificateBackupPlugin: CertificateBackupPlugin = CertificateBackupPlugin
    protected val certificateNfcAccessPlugin: CertificateNfcAccessPlugin = CertificateNfcAccessPlugin
    protected val viewerPlugin: ViewerPlugin = ViewerPlugin
    protected val edoPassagePlugin: EdoPassagePlugin = EdoPassagePlugin
    protected val edoDocCardExtensionPlugin: EdoDocCardExtensionPlugin = EdoDocCardExtensionPlugin
    protected val manageFeaturePlugin: ManageFeaturePlugin = ManageFeaturePlugin
    protected val violationPlugin: ViolationPlugin = ViolationPlugin
    protected val violationMessagePanelPlugin: ViolationMessagePanelPlugin = ViolationMessagePanelPlugin

    protected val reportingPlugin: ReportingPlugin = ReportingPlugin
    protected val profilePlugin: ProfilePlugin = ProfilePlugin
    protected val notificationPlugins: NotificationPlugins = NotificationPlugins
    protected val notificationSettingsPlugin: NotificationSettingsPlugin = NotificationSettingsPlugin
    protected val newsPlugin: NewsPlugin = NewsPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }
    protected val motivationPlugin: MotivationPlugin = MotivationPlugin
    protected val newMotivationPlugin: NewMotivationPlugin = NewMotivationPlugin
    protected val messagePanelRecorderPlugin: MessagePanelRecorderPlugin = MessagePanelRecorderPlugin
    protected val messagePanelPlugin: MessagePanelPlugin = MessagePanelPlugin
    protected val messagePanelAudioRecorderPlugin: MessagePanelAudioRecorderPlugin = MessagePanelAudioRecorderPlugin
    protected val messagePanelVideoRecorderPlugin: MessagePanelVideoRecorderPlugin = MessagePanelVideoRecorderPlugin

    protected val activityStatusPlugin: ActivityStatusPlugin = ActivityStatusPlugin
    protected val attachmentsEncryptionPlugin: AttachmentsEncryptionPlugin = AttachmentsEncryptionPlugin
    protected val attachmentsLoadingPlugin: AttachmentsLoadingPlugin = AttachmentsLoadingPlugin
    protected val attachmentsPlugin: AttachmentsPlugin = AttachmentsPlugin
    protected val attachmentsSigningPlugin: AttachmentsSigningPlugin = AttachmentsSigningPlugin
    protected val cadresPlugin: CadresPlugin = CadresPlugin
    protected val calendarCommonPlugin: CalendarCommonPlugin = CalendarCommonPlugin
    protected val calendarPlugin: CalendarPlugin = CalendarPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }
    protected val calendarCreateEventsPlugin: CalendarCreateEventPlugin = CalendarCreateEventPlugin
    protected val calendarEventCardsPlugin: CalendarEventCardsPlugin = CalendarEventCardsPlugin
    protected val communicatorCommonPlugin: CommunicatorCommonPlugin = CommunicatorCommonPlugin
    protected val communicatorPushPlugin: CommunicatorPushPlugin = CommunicatorPushPlugin.apply {
        customizationOptions.needToReceiveSupportChatMessagesPushes = true
        customizationOptions.needToReceiveSabySupportChatMessagesPushes = true
    }
    protected val communicatorSbisConversationPlugin: CommunicatorSbisConversationPlugin =
        CommunicatorSbisConversationPlugin
    protected val communicatorCrudPlugin: CommunicatorCrudPlugin = CommunicatorCrudPlugin
    protected val communicatorHostPlugin: CommunicatorHostPlugin = CommunicatorHostPlugin
    protected val communicatorNavigationPlugin: CommunicatorNavigationPlugin = CommunicatorNavigationPlugin
    protected val themesRegistryPlugin: ThemesRegistryPlugin = ThemesRegistryPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }
    protected val communicatorFilesPlugin: CommunicatorFilesPlugin = CommunicatorFilesPlugin
    protected val messageViewPlugin: MessageViewPlugin = MessageViewPlugin
    protected val employeesRegistryPlugin: EmployeesRegistryPlugin = EmployeesRegistryPlugin
    protected val employeeRegisterPlugin: EmployeeRegisterPlugin = EmployeeRegisterPlugin
    protected val contractorsCardPlugin: ContractorsCardPlugin = ContractorsCardPlugin
    protected val diskPlugin: DiskPlugin = DiskPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }
    protected val webViewerPlugin: WebViewerPlugin = WebViewerPlugin
    protected val docWebViewerPlugin: DocWebViewerPlugin = DocWebViewerPlugin
    protected val meetingPlugin: MeetingPlugin = MeetingPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }
    protected val personViewPlugin: PersonViewPlugin = PersonViewPlugin
    protected val edoFacesSelectionPlugin: EdoFacesSelectionPlugin = EdoFacesSelectionPlugin
    protected val navigationPlugin: NavigationPlugin = NavigationPlugin
    protected val settingsScreenPlugin: SettingsScreenPlugin = SettingsScreenPlugin
    protected val pushNotificationHistoryPlugin: PushNotificationHistoryPlugin = PushNotificationHistoryPlugin
    protected val authSettingsPlugin: AuthSettingsPlugin = AuthSettingsPlugin
    protected val decoratedLinkPlugin: DecoratedLinkPlugin = DecoratedLinkPlugin
    protected val richTextPlugin: RichTextPlugin = RichTextPlugin
    protected val widgetPlayerPlugin: WidgetPlayerPlugin = WidgetPlayerPlugin
    protected val mediaPlayerPlugin: MediaPlayerPlugin = MediaPlayerPlugin
    protected val audioPlayerViewPlugin: AudioPlayerViewPlugin = AudioPlayerViewPlugin
    protected val videoPlayerViewPlugin: VideoPlayerViewPlugin = VideoPlayerViewPlugin
    protected val cloudViewPlugin: CloudViewPlugin = CloudViewPlugin
    protected val certificateSelectionPlugin: CertificateSelectionPlugin = CertificateSelectionPlugin
    protected val shareMessagesPlugin: ShareMessagesPlugin = ShareMessagesPlugin
    protected val sendMessagesPlugin: SendMessagePlugin = SendMessagePlugin
    protected val sabyDocViewerPlugin: SabyDocViewerPlugin = SabyDocViewerPlugin
    protected val filesPickerPlugin: SbisFilesPickerPlugin = SbisFilesPickerPlugin
    protected val galleryPlugin: GalleryPlugin = GalleryPlugin
    protected val attachmentsUiPlugin: AttachmentsUiPlugin = AttachmentsUiPlugin
    protected val calendarTrackingPlugin: CalendarTrackingPlugin = CalendarTrackingPlugin
    protected val sbisPeriodPickerPlugin: SbisPeriodPickerPlugin = SbisPeriodPickerPlugin

    init {
        onboardingPlugin.customizationOptions.apply {
            userAware = true
        }

        authPlugin.customizationOptions.apply {
            registrationSocialAuthEnable = true
            initialProdHostForAuthInReleaseMode = true
        }
    }

    override fun registerPlugins(app: Application, pluginManager: PluginManager) {
        super.registerPlugins(app, pluginManager)

        pluginManager.registerPlugins(
            authDevicePlugin,
            frescoPlugin,
            verificationPlugin,
            versionCheckerPlugin,
            pushNotificationPlugin,
            languagePlugin,
            storagePlugin,
            eventManagerPlugin,
            datePickerPlugin,
            barcodeReaderPlugin,
            linkOpenerPlugin,
            recipientSelectionPlugin,
            mediaPlugin,
            employeePlugin,
            employeeCommonPlugin,
            personViewPlugin,
            personCardPlugin,
            myProfilePlugin,
            citySelectorPlugin,
            documentSignPlugin,
            cryptoProPlugin,
            masterCertificatePlugin,
            certificateRequestPlugin,
            userActivityTrackWatcherPlugin,
            signatureAuthorityPlugin,
            shareMenuPlugin,
            scannerPlugin,
            syncManagerPlugin,
            clientsPlugin,
            clientsContactListPlugin,
            clientsRepresentativeCreatePlugin,
            eSignsPlugin,
            certificateCopyingPlugin,
            certificateActivationPlugin,
            certificateBackupPlugin,
            certificateNfcAccessPlugin,
            viewerPlugin,
            edoPassagePlugin,
            edoDocCardExtensionPlugin,
            edoFacesSelectionPlugin,
            manageFeaturePlugin,
            violationPlugin,
            violationMessagePanelPlugin,
            /* Набор специфичных плагинов для региональных сборок RU/KZ etc. */
            *CountrySpecificPlugins.countrySpecificPlugins,
            reportingPlugin,
            profilePlugin,
            notificationSettingsPlugin,
            *notificationPlugins.plugins,
            newsPlugin,
            motivationPlugin,
            newMotivationPlugin,
            messagePanelRecorderPlugin,
            messagePanelPlugin,
            messagePanelAudioRecorderPlugin,
            messagePanelVideoRecorderPlugin,
            activityStatusPlugin,
            attachmentsEncryptionPlugin,
            attachmentsLoadingPlugin,
            attachmentsPlugin,
            attachmentsSigningPlugin,
            cadresPlugin,
            calendarCommonPlugin,
            calendarPlugin,
            calendarEventCardsPlugin,
            calendarCreateEventsPlugin,
            communicatorCommonPlugin,
            communicatorPushPlugin,
            communicatorSbisConversationPlugin,
            communicatorCrudPlugin,
            communicatorHostPlugin,
            communicatorNavigationPlugin,
            messageViewPlugin,
            employeeRegisterPlugin,
            themesRegistryPlugin,
            communicatorFilesPlugin,
            contractorsCardPlugin,
            diskPlugin,
            webViewerPlugin,
            docWebViewerPlugin,
            meetingPlugin,
            navigationPlugin,
            settingsScreenPlugin,
            pushNotificationHistoryPlugin,
            authSettingsPlugin,
            decoratedLinkPlugin,
            richTextPlugin,
            widgetPlayerPlugin,
            mediaPlayerPlugin,
            audioPlayerViewPlugin,
            videoPlayerViewPlugin,
            cloudViewPlugin,
            certificateSelectionPlugin,
            shareMessagesPlugin,
            sendMessagesPlugin,
            sabyDocViewerPlugin,
            filesPickerPlugin,
            galleryPlugin,
            attachmentsUiPlugin,
            calendarTrackingPlugin,
            sbisPeriodPickerPlugin
        )
    }
}