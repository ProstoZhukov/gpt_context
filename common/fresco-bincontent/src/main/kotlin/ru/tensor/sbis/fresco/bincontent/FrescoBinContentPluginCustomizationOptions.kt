package ru.tensor.sbis.fresco.bincontent

import android.app.Application
import java.io.File
import ru.tensor.sbis.frescoutils.FrescoPluginCustomizationOptions

/**
 * Конфигурация [FrescoBinContentPlugin]
 */
class FrescoBinContentPluginCustomizationOptions internal constructor() {

    /**
     * @see [FrescoPluginCustomizationOptions.isNativeCodeEnabled]
     */
    var isNativeCodeEnabled: Boolean = true

    /**
     * @see [FrescoPluginCustomizationOptions.cacheDirProvider]
     */
    var cacheDirProvider: (Application) -> File = Application::getCacheDir

    /**
     * @see [FrescoPluginCustomizationOptions.cacheSizeInMB]
     */
    var cacheSizeInMB: Int = 200
}