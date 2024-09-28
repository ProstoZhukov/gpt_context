package ru.tensor.sbis.message_panel.contract.attachments

import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs

/**
 * Фабрика для создания аргументов слайдера просмотрщика вложений [ViewerSliderArgs]
 *
 * @author vv.chekurda
 */
interface ViewerSliderArgsFactory {

    /**
     * Создать аргументы слайдера просмотрщика
     * @param attachmentList     список вложений
     * @param selectedAttachment вложение из списка, на котором необходимо открыть просмотрщик
     */
    fun createViewerSliderArgs(attachmentList: List<FileInfo>, selectedAttachment: FileInfo): ViewerSliderArgs
}