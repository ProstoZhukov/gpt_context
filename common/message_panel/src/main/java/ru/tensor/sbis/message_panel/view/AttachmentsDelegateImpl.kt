package ru.tensor.sbis.message_panel.view

import android.view.View
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.message_panel.MessagePanelPlugin
import ru.tensor.sbis.message_panel.delegate.MessagePanelFilesPickerConfig

/**
 * Реализация [AttachmentsDelegate]
 *
 * @author vv.chekurda
 */
internal class AttachmentsDelegateImpl(
    private val fragment: Fragment,
    private val filesPicker: SbisFilesPicker
) : AttachmentsDelegate {

    override var filesPickerConfig: MessagePanelFilesPickerConfig = MessagePanelFilesPickerConfig()

    override fun onBottomMenuClick(anchorView: View) {
        val presentationParams =
            if (DeviceConfigurationUtils.isTablet(fragment.requireContext())) {
                ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerPresentationParams(
                    AnchorHorizontalLocator(HorizontalAlignment.CENTER)
                        .apply { this.anchorView = anchorView },
                    AnchorVerticalLocator(VerticalAlignment.TOP)
                        .apply { this.anchorView = anchorView }
                )
            } else {
                null
            }
        filesPicker.show(
            fragmentManager = fragment.childFragmentManager,
            tabs = filesPickerConfig.toSbisFilesPickerTab(),
            presentationParams = presentationParams
        )
    }

    private fun MessagePanelFilesPickerConfig.toSbisFilesPickerTab(): Set<SbisFilesPickerTab> =
        mutableSetOf<SbisFilesPickerTab>().also {
            val appConfig = MessagePanelPlugin.customizationOptions.filesPickerConfig
            if (galleryEnabled && appConfig.galleryEnabled) {
                it.add(
                    SbisFilesPickerTab.Gallery(
                        needOnlyImages = isOnlyImagesFromGallery && appConfig.isOnlyImagesFromGallery
                    )
                )
            }
            it.add(
                SbisFilesPickerTab.Files(
                    isRecentEnabled = recentEnabled && appConfig.recentEnabled,
                    isFavoritesEnabled = favoritesEnabled && appConfig.favoritesEnabled,
                    isMyDiskEnabled = myDiskEnabled && appConfig.myDiskEnabled,
                    isCompanyDiskEnabled = companyDiskEnabled && appConfig.companyDiskEnabled,
                    isBufferEnabled = bufferEnabled && appConfig.bufferEnabled
                )
            )
            if (scannerEnabled && appConfig.scannerEnabled) {
                it.add(SbisFilesPickerTab.Scanner())
            }
            if (tasksEnabled && appConfig.tasksEnabled) {
                it.add(SbisFilesPickerTab.Tasks())
            }
        }
}