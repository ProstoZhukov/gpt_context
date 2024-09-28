package ru.tensor.sbis.message_panel.attachments

import androidx.fragment.app.Fragment
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.message_panel.contract.attachments.AttachmentsRouter
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * Реализация [AttachmentsRouter] для открытия вложений из панели ввода сообщений
 *
 * @author vv.chekurda
 * Создан 7/30/2019
 */
internal class AttachmentsRouterImpl(
    private val fragment: Fragment,
    private val sliderIntentFactory: ViewerSliderIntentFactory,
    private val sliderArgsFactory: ViewerSliderArgsFactory
) : AttachmentsRouter {

    override fun showViewerSlider(attachmentList: List<FileInfo>, selectedAttachment: FileInfo) {
        val viewerSliderArgs = sliderArgsFactory.createViewerSliderArgs(attachmentList, selectedAttachment)
        val intent = sliderIntentFactory.createViewerSliderIntent(fragment.requireContext(), viewerSliderArgs)
        fragment.startActivity(intent)
    }
}