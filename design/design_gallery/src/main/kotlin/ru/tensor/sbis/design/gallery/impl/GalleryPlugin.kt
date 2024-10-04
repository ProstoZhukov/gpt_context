package ru.tensor.sbis.design.gallery.impl

import ru.tensor.sbis.barcode_decl.barcodereader.BarcodeReaderFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import ru.tensor.sbis.design.gallery.decl.GalleryComponentFactory
import ru.tensor.sbis.design.gallery.decl.GalleryTabProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.storage.contract.InternalStorageProvider

/**
 * Плагин "Галереи"
 *
 * @author ia.nikitin
 */
object GalleryPlugin : BasePlugin<Unit>() {

    internal lateinit var internalStorageProvider: FeatureProvider<InternalStorageProvider>

    internal var barcodeReaderFeatureProvider: FeatureProvider<BarcodeReaderFeature>? = null

    override val api: Set<FeatureWrapper<out Feature>> =
        setOf(
            FeatureWrapper(SbisFilesPickerTabFeatureProvider::class.java, ::GalleryTabProvider),
            FeatureWrapper(GalleryComponentFactory::class.java, ::GalleryComponentFactoryImpl)
        )

    override val dependency: Dependency = Dependency.Builder()
        .require(InternalStorageProvider::class.java) { internalStorageProvider = it }
        .optional(BarcodeReaderFeature::class.java) { barcodeReaderFeatureProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}