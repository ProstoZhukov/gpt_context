package ru.tensor.sbis.video_monitoring_decl.player

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для снятия блокировки плеера в полноэкранном режиме.
 *
 * @author as.chadov
 */
interface VideoPlayerFullscreenUnlocker : Feature {

    /** @SelfDocumented */
    fun unlock()
}