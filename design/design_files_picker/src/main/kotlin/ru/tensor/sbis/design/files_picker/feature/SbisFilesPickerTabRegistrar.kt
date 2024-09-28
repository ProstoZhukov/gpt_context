package ru.tensor.sbis.design.files_picker.feature

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeature
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTabFeatureProvider
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * Реестр [SbisFilesPickerTabFeatureProvider].
 *
 * @author ai.abramenko
 */
internal class SbisFilesPickerTabRegistrar(initialProviders: Set<SbisFilesPickerTabFeatureProvider<*>>) {

    private val providers =
        HashMap<KClass<out SbisFilesPickerTab>, SbisFilesPickerTabFeatureProvider<SbisFilesPickerTab>>()

    init {
        initialProviders.forEach { provider ->
            if (providers.containsKey(provider.tabClass)) {
                illegalState { "Provider with this key is already registered." }
            } else {
                @Suppress("UNCHECKED_CAST")
                providers[provider.tabClass] = provider as SbisFilesPickerTabFeatureProvider<SbisFilesPickerTab>
            }
        }
    }

    internal fun getTabFeature(
        tab: SbisFilesPickerTab,
        storeOwner: ViewModelStoreOwner
    ): SbisFilesPickerTabFeature<*>? {
        val provider = providers[tab::class]
        if (provider == null) {
            Timber.d("The provider for this key is not registered.")
            return null
        }
        return provider.getTabFeature(tab, storeOwner)
    }
}