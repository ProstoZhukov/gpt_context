package ru.tensor.sbis.message_panel.contract

import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.action.DeleteAttachmentsUseCase
import ru.tensor.sbis.attachments.decl.mapper.AttachmentModelMapperFactory
import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuProvider
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerFactory
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.recorder.decl.RecorderViewDependencyProvider
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Зависимости для DI панели сообщений
 */
interface MessagePanelDependency :
    ViewerSliderIntentFactory,
    LoginInterface.Provider,
    AttachmentModelMapperFactory,
    AddAttachmentsUseCase,
    DeleteAttachmentsUseCase,
    RecorderViewDependencyProvider,
    SbisFilesPickerFactory {

    /**
     * Поставщик компонента выбора получателя.
     */
    val recipientSelectionProvider: RecipientSelectionProvider?

    /**
     * Поставщик компонента меню выбора получателя.
     */
    val recipientSelectionMenuProvider: RecipientSelectionMenuProvider?
        get() = null

    /**
     * Поставщик интерфейса утилиты для отправки аналитики.
     */
    val analyticsUtilProvider: AnalyticsUtil.Provider?
}
