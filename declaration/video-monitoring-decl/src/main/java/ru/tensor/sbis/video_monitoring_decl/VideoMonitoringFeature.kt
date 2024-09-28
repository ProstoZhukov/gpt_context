package ru.tensor.sbis.video_monitoring_decl

import ru.tensor.sbis.video_monitoring_decl.player.VideoPlayerFullscreenUnlocker

/**
 * Интерфейс описывающий публичное api модуля "Видеонаблюдения".
 * @see [VideoMonitoringIntentProvider]
 */
interface VideoMonitoringFeature :
    VideoMonitoringIntentProvider,
    VideoMonitoringFragmentProvider,
    VideoPlayerFullscreenUnlocker {

    /**
     * Получение провайдера списка камер.
     *
     * @see CameraListProvider
     */
    fun getCameraListProvider(): CameraListProvider

    /**
     * Можно ли использовать автономный плеер в собственной оптимизированной Activity [VideoMonitoringIntentProvider]
     * для данного клиентского МП или же требуется использовать только фрагмент [VideoMonitoringFragmentProvider].
     */
    fun isSelfContainedVideoPlayerAllowed(): Boolean
}
