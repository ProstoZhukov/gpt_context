package ru.tensor.sbis.design.picker_files_tab.feature

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.common.util.findOrCreateViewModel
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import kotlin.reflect.KClass

internal class PickerFilesTabFeatureProvider : SbisFilesPickerTabFeatureProvider<SbisFilesPickerTab.Files> {

    override val tabClass: KClass<SbisFilesPickerTab.Files>
        get() = SbisFilesPickerTab.Files::class

    override fun getTabFeature(
        tab: SbisFilesPickerTab.Files,
        storeOwner: ViewModelStoreOwner
    ): SbisFilesPickerTabFeature<SbisFilesPickerTab.Files> =
        findOrCreateViewModel(storeOwner) { PickerFilesTabFeature(tab) }
}