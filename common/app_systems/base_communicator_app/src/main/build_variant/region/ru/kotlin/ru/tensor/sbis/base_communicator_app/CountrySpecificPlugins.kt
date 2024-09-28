package ru.tensor.sbis.base_communicator_app

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.videocall.VideoCallPlugin
import ru.tensor.sbis.videocall.contract.VideoCallFeatureConfiguration

/** Плагины, подключаемые для текущей сборки */
internal object CountrySpecificPlugins {
    private val videoCallPlugin: VideoCallPlugin = VideoCallPlugin.apply {
        customizationOptions.configuration = VideoCallFeatureConfiguration.DEFAULT
    }

    val countrySpecificPlugins = arrayOf<BasePlugin<*>>(videoCallPlugin)
}