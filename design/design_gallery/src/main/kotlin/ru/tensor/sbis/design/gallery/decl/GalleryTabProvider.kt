package ru.tensor.sbis.design.gallery.decl

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.gallery.impl.GalleryComponentImpl
import kotlin.reflect.KClass

internal class GalleryTabProvider : SbisFilesPickerTabFeatureProvider<SbisFilesPickerTab.Gallery> {

    override val tabClass: KClass<SbisFilesPickerTab.Gallery>
        get() = SbisFilesPickerTab.Gallery::class

    override fun getTabFeature(
        tab: SbisFilesPickerTab.Gallery,
        storeOwner: ViewModelStoreOwner
    ): SbisFilesPickerTabFeature<SbisFilesPickerTab.Gallery> = GalleryComponentImpl.from(tab, storeOwner)
}