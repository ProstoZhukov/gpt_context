package ru.tensor.sbis.video_monitoring_decl

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.video_monitoring_decl.model.CameraAffiliation
import ru.tensor.sbis.video_monitoring_decl.model.CameraInfo
import ru.tensor.sbis.video_monitoring_decl.model.CollectionFilter

/**
 * Интерфейс для открытия экрана видеоплеера.
 *
 * @author as.chadov
 */
interface VideoMonitoringIntentProvider : Feature {
    /**
     * Получить [Intent], открывающий Activity экрана видеоплеера из нотификейшена.
     *
     * @param toolbarTitle текст заголовка тулбара
     * @param toolbarSubtitle текст подзаголовка тулбара
     * @param cameraId идентификатор камеры
     * @param startTimestamp время начала события
     * @param endTimestamp время конца события
     * @return Intent для открытия экрана видеоплеера из нотификейшена
     */
    fun getVideoMonitoringActivityIntent(
        context: Context,
        toolbarTitle: String,
        toolbarSubtitle: String,
        cameraId: Long,
        startTimestamp: Long,
        endTimestamp: Long
    ): Intent

    /**
     * Получить [Intent], открывающий Activity экрана видеоплеера.
     *
     * @param toolbarTitle текст заголовка тулбара
     * @param toolbarSubtitle текст подзаголовка тулбара
     * @param affiliation информация о принадлежности [cameras]
     * @param cameras список камер
     * @param selectedCameraId id выбранной камеры
     * @param collectionFilter дополнительный фильтр по коллекции камер [cameras]
     * @return Intent для открытия экрана видеоплеера
     */
    fun getVideoMonitoringIntent(
        context: Context,
        toolbarTitle: String,
        toolbarSubtitle: String = "",
        affiliation: CameraAffiliation,
        cameras: List<CameraInfo> = emptyList(),
        selectedCameraId: Long = 0,
        collectionFilter: CollectionFilter? = null
    ): Intent
}