package ru.tensor.sbis.scanner.files_picker_tab

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import kotlin.reflect.KClass

internal class ScannerTabProvider : SbisFilesPickerTabFeatureProvider<SbisFilesPickerTab.Scanner> {

    override val tabClass: KClass<SbisFilesPickerTab.Scanner>
        get() = SbisFilesPickerTab.Scanner::class

    override fun getTabFeature(
        tab: SbisFilesPickerTab.Scanner,
        storeOwner: ViewModelStoreOwner
    ): SbisFilesPickerTabFeature<SbisFilesPickerTab.Scanner> = ScannerTab.from(storeOwner)
}