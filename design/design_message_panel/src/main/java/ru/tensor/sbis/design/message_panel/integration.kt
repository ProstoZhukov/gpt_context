/**
 * Набор вспомогательных инструментов для интеграции панели ввода сообщений с внешними сервисами.
 * Реализованы самостоятельно так как зависят от жц хоста
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.message_panel

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.message_panel.vm.attachments.MessagePanelAttachmentsApi

/**
 * Активация подписок для работы с вложениями
 */
fun MessagePanelAttachmentsApi.enableAttachments(fragment: Fragment) {
    // TODO: объединить проверку с остальными сервисами вложений в MessagePanelPlugin
    val filesPicker = checkNotNull(
        MessagePanelPlugin.filesPickerTabFeatureFeatureProvider
    ) { "Attachment selection service is not provided to MessagePanelPlugin" }
        .get()
        .createSbisFilesPicker(viewModelStoreOwner = fragment)

    // подписка для получения выбранных файлов
    filesPicker.events
        .filterIsInstance<SbisFilesPickerEvent.OnItemsSelected>()
        .onEach { addAttachments(attachments = it.selectedItems, isNeedCompressImages = it.compressImages) }
        .launchIn(fragment.viewLifecycleOwner.lifecycleScope)

    // подписка на запросы выбора файлов
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        attachmentsSelectionRequest.collect { request ->
            filesPicker.show(
                fragmentManager = fragment.childFragmentManager,
                tabs = setOf(
                    SbisFilesPickerTab.Gallery(),
                    SbisFilesPickerTab.Files(),
                    SbisFilesPickerTab.Scanner()
                ),
                presentationParams = request.presentationParams
            )
        }
    }
}
