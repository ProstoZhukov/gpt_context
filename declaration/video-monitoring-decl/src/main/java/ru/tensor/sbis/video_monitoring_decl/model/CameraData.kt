package ru.tensor.sbis.video_monitoring_decl.model

import java.util.UUID

/**
 * Модель данных камеры.
 * Запрашивается через модуль видеомониторинга.
 *
 * @param deviceId идентификатор устройства
 * @param modelType идентификатор модели камеры из сервиса
 * @param workplaceId идентификатор рабочего места
 * @param departmentId идентификатор подразделения
 * @param roomId идентификатор помещения
 * @param companyId идентификатор организации
 * @param cameraName название камеры
 * @param workplaceName название рабочего места
 * @param roomName название помещения
 * @param companyName название организации
 * @param isActive активна ли камера
 * @param isFavorite помечена ли камера избранной
 * @param isAccessGranted разрешён ли доступ к камере
 * @param preview исходная ссылка на изображение для превью
 * @param status статус видеокамеры, см. [CameraStatus]
 */
data class CameraData(
    val deviceId: Long,
    val modelType: UUID,
    val workplaceId: Int,
    val departmentId: Int,
    val roomId: Int,
    val companyId: Int,
    val cameraName: String,
    val workplaceName: String,
    val roomName: String,
    val companyName: String,
    val isActive: Boolean,
    val isFavorite: Boolean,
    val isAccessGranted: Boolean,
    val status: CameraStatus,
    val preview: String
) {

    /** Ссылка на превью привязанная к активности камерыю */
    val previewUrl: String
        get() = preview.takeIf { isActive && status == CameraStatus.ONLINE }.orEmpty()
}