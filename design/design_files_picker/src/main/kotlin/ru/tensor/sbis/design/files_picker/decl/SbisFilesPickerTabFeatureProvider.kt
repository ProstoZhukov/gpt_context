package ru.tensor.sbis.design.files_picker.decl

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.plugin_struct.feature.Feature
import kotlin.reflect.KClass

/**
 * Поставщик раздела пикера.
 *
 * @author ai.abramenko
 */
interface SbisFilesPickerTabFeatureProvider<TAB : SbisFilesPickerTab> : Feature {

    /** Тип раздела, по которому будет регистрироваться провайдер */
    val tabClass: KClass<TAB>

    /** @SelfDocumented */
    fun getTabFeature(tab: TAB, storeOwner: ViewModelStoreOwner): SbisFilesPickerTabFeature<TAB>
}