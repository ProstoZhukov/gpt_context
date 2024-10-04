package ru.tensor.sbis.design.gallery.impl

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.gallery.decl.GalleryComponent
import ru.tensor.sbis.design.gallery.decl.GalleryComponentFactory

internal class GalleryComponentFactoryImpl : GalleryComponentFactory {

    override fun createGalleryComponent(
        tab: SbisFilesPickerTab.Gallery,
        storeOwner: ViewModelStoreOwner
    ): GalleryComponent =
        GalleryComponentImpl.from(tab, storeOwner)
}