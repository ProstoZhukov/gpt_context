package ru.tensor.sbis.video_monitoring_decl

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.video_monitoring_decl.model.CameraData

/**
 * Интерфейс для получения списка камер.
 */
interface CameraListProvider : Feature {

    /**
     * Получение списка камер.
     *
     * @param salePointId строковый id точки продаж, для котороый нужно получать камеры
     * @param preload получение данных с быстрой предзагрузкой. Так как метод запроса камер отрабатывает очень долго, некоторые парамеры запроса
     * можно отключить для ускорения. Используется для получения списка камер без статусов, а после уже инициирования полного запроса
     *
     * Клиенту нет необходимости следить за навигацией, последующие вызовы [getCameras] будут запрашивать следующие еще неполученные развороты.
     * Для проверки доступности дальнейшей навигации см. метод [hasMore].
     * Для сброса навигации к первой странице используется [reset], также навигация будет сброшена при изменении [salePointId]
     *
     * @return списк камер
     */
    fun getCameras(
        salePointId: String,
        preload: Boolean = false
    ): Flow<List<CameraData>>

    /**
     * @return Есть ли ещё камеры, доступные для загрузки
     */
    fun hasMore(): Boolean

    /**
     * Сбросить состояние провайдера включая текущую навигацию
     */
    fun reset()
}
