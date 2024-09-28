package ru.tensor.sbis.design.gallery.decl

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Компонент галереи
 * Для получения вызовите [GalleryComponentFactory.createGalleryComponent]
 *
 * @author ia.nikitin
 */
interface GalleryComponent : Feature {

    /** Наблюдаемые события, см. [GalleryEvent] */
    val events: Flow<GalleryEvent>

    /** @SelfDocumented */
    fun createFragment(config: GalleryConfig): Fragment
}