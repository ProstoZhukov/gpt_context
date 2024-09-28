package ru.tensor.sbis.frescoutils

import android.app.Application
import com.facebook.imagepipeline.cache.CacheKeyFactory
import java.io.File

/**
 * Конфигурация [FrescoPlugin]
 */
class FrescoPluginCustomizationOptions internal constructor() {

    /**
     * Режим авторизации при выполнении сетевых запросов
     *
     * При активации автоматически пробрасывает нужные cookie
     */
    var authRequired: Boolean = true

    /**
     * Определяет тип реализации загрузчика
     *
     * true - используется нативный код, C++ библиотеки
     * false - java реализация, доступно с версии fresco=2.1.0
     */
    var isNativeCodeEnabled: Boolean = true

    /**
     * Поставщик директории кэша загруженных картинок приложения
     */
    var cacheDirProvider: (Application) -> File = Application::getCacheDir

    /**
     * Размер кэша загруженных картинок приложения в МБ
     */
    var cacheSizeInMB: Int = 200

    /**
     * Используемая реализация [CacheKeyFactory].
     */
    var cacheKeyFactory: CacheKeyFactory = FrescoHostIndependentKeyFactory
}