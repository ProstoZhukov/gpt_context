package ru.tensor.sbis.message_panel.di.attachmnets

import dagger.BindsInstance
import dagger.Subcomponent
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.attachments.model.AttachmentCatalogParams
import javax.inject.Named

/**
 * Зависимости для работы с вложениями
 *
 * @author vv.chekurda
 */
@Subcomponent(modules = [MessagePanelAttachmentsModule::class])
interface MessagePanelAttachmentsComponent {

    val interactor: MessagePanelAttachmentsInteractor

    @Subcomponent.Factory
    interface Factory {

        fun create(
            @BindsInstance @Named(ATTACHMENTS_CATALOG_DATA) catalogParams: AttachmentCatalogParams =
                AttachmentCatalogParams(UrlUtils.FILE_SD_OBJECT)
        ): MessagePanelAttachmentsComponent
    }
}