package ru.tensor.sbis.design.gallery.decl

import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика компонента галереи
 * Реализация поставляется через GalleryPlugin, для её получения в прикладном модуле добавьте этот интерфейс в
 * зависимости плагина
 *
 * @author ia.nikitin
 */
interface GalleryComponentFactory : Feature {

    /**
     * Создать компонент галереи для вкладки [tab]
     *
     * @param storeOwner Владелец хранилища, в котором компонент будет "жить"
     */
    fun createGalleryComponent(
        tab: SbisFilesPickerTab.Gallery,
        storeOwner: ViewModelStoreOwner
    ): GalleryComponent
}