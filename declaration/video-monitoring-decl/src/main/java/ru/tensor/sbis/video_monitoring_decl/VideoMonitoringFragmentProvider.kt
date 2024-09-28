package ru.tensor.sbis.video_monitoring_decl

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.video_monitoring_decl.model.CameraAffiliation
import ru.tensor.sbis.video_monitoring_decl.model.CameraInfo
import ru.tensor.sbis.video_monitoring_decl.model.CollectionFilter

/**
 * Интерфейс, предоставляющий фрагменты видеомониторинга.
 *
 * @author as.chadov
 */
interface VideoMonitoringFragmentProvider : Feature {
    /**
     * Создание фрагмента видеомониторинга.
     *
     * @param affiliation информация о принадлежности [cameras]
     * @param cameras список камер
     * @param toolbarTitle текст заголовка тулбара
     * @param toolbarSubtitle текст подзаголовка тулбара
     * @param collectionFilter дополнительный фильтр по коллекции камер
     *
     * @return фрагмент видеомониторинга
     */
    fun createFragment(
        affiliation: CameraAffiliation,
        cameras: List<CameraInfo>,
        toolbarTitle: String,
        toolbarSubtitle: String = "",
        collectionFilter: CollectionFilter = CollectionFilter.default
    ): Fragment

    /**
     * Создание фрагмента видеомониторинга.
     *
     * @param affiliation информация о принадлежности [selectedCameraId]
     * @param toolbarTitle текст заголовка тулбара
     * @param toolbarSubtitle текст подзаголовка тулбара
     * @param collectionFilter дополнительный фильтр по коллекции камер
     *
     * @return фрагмент видеомониторинга
     */
    fun createFragment(
        affiliation: CameraAffiliation,
        selectedCameraId: Long,
        toolbarTitle: String,
        toolbarSubtitle: String = "",
        collectionFilter: CollectionFilter = CollectionFilter.default
    ): Fragment
}