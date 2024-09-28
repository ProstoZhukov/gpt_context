package ru.tensor.sbis.message_panel.contract.options

import ru.tensor.sbis.disk.decl.attach_helper.MediaOption

/**
 * Обработчик пунктов меню [MediaOption]
 *
 * @author vv.chekurda
 */
interface MediaOptionHandler {

    /**
     * Набор доступных элементов в меню. Список может меняться от вызова к вызову в зависимости от внутренних правил.
     */
    fun getOptions(): Set<MediaOption>

    /**
     * Обработка меню [option]
     *
     * @return `true`, если элемент [option] был обработан. В противном случае элемент будет передан дальше для
     * обработки. Например: `ConversationMediaOptionHandler -> MessagePanelAttachmentPresenterImpl ->
     * AttachmentPresenterHelper`
     */
    fun handle(option: MediaOption): Boolean
}