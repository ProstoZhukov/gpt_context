package ru.tensor.sbis.design.picker_files_tab

import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import ru.tensor.sbis.design.gallery.decl.GalleryComponentFactory
import ru.tensor.sbis.design.picker_files_tab.feature.PickerFilesTabFeatureProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин вкладки "Файлы" для компонента выбора файлов.
 * Вкладка является обрезанной, в ней нет возможности выбора из дисковых реестров,
 * т.е. из Последних, Избранных, Моего диска, Диска компании и Буфера.
 * Выбор возможен из внутреннего хранилища и галереи.
 *
 * @author ai.abramenko
 */
object PickerFilesTabPlugin : BasePlugin<Unit>() {

    internal lateinit var galleryComponentFactoryProvider: FeatureProvider<GalleryComponentFactory>

    override val api: Set<FeatureWrapper<out Feature>> =
        setOf(
            FeatureWrapper(SbisFilesPickerTabFeatureProvider::class.java, ::PickerFilesTabFeatureProvider)
        )

    override val dependency: Dependency =
        Dependency.Builder()
            .require(GalleryComponentFactory::class.java) { galleryComponentFactoryProvider = it }
            .build()

    override val customizationOptions: Unit = Unit
}