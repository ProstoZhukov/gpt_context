package ru.tensor.sbis.message_panel.feature

import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderArgs

/**
 * Фичи панели сообщений
 */
interface MessagePanelFeature : Feature {

    /**
     * Фабрика для создания аргументов слайдера просмотрщика вложений [ViewerSliderArgs]
     */
    fun viewerSliderArgsFactory(): ViewerSliderArgsFactory
}