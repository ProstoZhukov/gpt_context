package ru.tensor.sbis.design.files_picker.decl

import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.Flow

/**
 * Компонент "Выбор файла"
 *
 * // Реализуем SbisFilesPickerTab
 * class CustomTab : SbisFilesPickerTab
 *
 * // Реализуем SbisFilesPickerTabFeature
 * class CustomTabFeature : SbisFilesPickerTabFeature<CustomTab> {
 *      // Overrides
 * }
 *
 * // Реализуем SbisFilesPickerTabProvider
 * class CustomTabFeatureProvider : SbisFilesPickerTabProvider<CustomTab> {
 *      // Overrides
 * }
 *
 * // Через PluginSystem
 * override val api: Set<FeatureWrapper<out Feature>> = setOf(
 *      FeatureWrapper(SbisFilesPickerTabProvider::class.java, ::CustomTabFeatureProvider
 * )
 *
 * // Показываем окно выбора с CustomTab
 * val filesPickerFactory: SbisFilesPickerFactory
 * val filesPicker: SbisFilesPicker = filesPickerFeatureFactory.get(viewModelStoreOwner)
 * filesPicker.show(fragmentManager, listOf(CustomTab()))
 *
 * @author ai.abramenko
 */
interface SbisFilesPicker {

    /**
     * Отобразить компонент с заданными разделами по их типам.
     *
     * @param fragmentManager       [FragmentManager] в котором будет отображаться фрагмент компонента
     * @param tabs                  Список разделов, откуда пользователь может выбирать
     * @param presentationParams    Общие параметры для отображения
     */
    fun show(
        fragmentManager: FragmentManager,
        tabs: Set<SbisFilesPickerTab>,
        presentationParams: SbisFilesPickerPresentationParams? = null
    )

    /**
     * События от пикера
     */
    val events: Flow<SbisFilesPickerEvent>
}