package ru.tensor.sbis.design.files_picker

import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerFactory
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import ru.tensor.sbis.design.files_picker.feature.SbisFilesPickerFactoryImpl
import ru.tensor.sbis.design.files_picker.feature.SbisFilesPickerTabRegistrar
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин компонента Выбор файла.
 *
 * @author ai.abramenko
 */
object SbisFilesPickerPlugin : BasePlugin<SbisFilesPickerPlugin.Options>() {

    private val providers = mutableSetOf<SbisFilesPickerTabFeatureProvider<*>>()
    internal val tabRegistrar by lazy { SbisFilesPickerTabRegistrar(providers) }

    override val api: Set<FeatureWrapper<out Feature>> by lazy {
        if (customizationOptions.isEnabled) {
            setOf(FeatureWrapper(SbisFilesPickerFactory::class.java, ::SbisFilesPickerFactoryImpl))
        } else {
            emptySet()
        }
    }

    override val dependency: Dependency = Dependency.Builder()
        .requireSet(SbisFilesPickerTabFeatureProvider::class.java) {
            providers.addAll(it.map { featureProvider -> featureProvider.get() })
        }
        .build()

    override val customizationOptions: Options = Options()

    class Options {
        var isEnabled: Boolean = true
    }
}