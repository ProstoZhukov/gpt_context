package ru.tensor.sbis.video_monitoring_decl.registry

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс предоставляющий фрагменты реестров камер видеомониторинга.
 *
 * @author as.chadov
 */
interface CameraRegistryFragmentProvider : Feature {
    /**
     * Создание корневой фрагмент для просмотра иерархии реестров камер видеомониторинга.
     */
    fun createCameraRegistryHostFragment(): Fragment
}