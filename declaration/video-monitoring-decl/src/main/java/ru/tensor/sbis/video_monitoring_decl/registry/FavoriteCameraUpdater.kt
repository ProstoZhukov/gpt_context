package ru.tensor.sbis.video_monitoring_decl.registry

import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Обновитель избранности камер.
 */
interface FavoriteCameraUpdater : Feature {

    /**
     * Изменить статус избранности камеры [cameraUuid] к состоянию [isFavorite].
     */
    suspend fun changeFavoriteStatus(
        cameraUuid: UUID,
        isFavorite: Boolean
    ): Boolean
}