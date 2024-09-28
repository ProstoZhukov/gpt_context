package ru.tensor.sbis.widget_player.contract

import ru.tensor.sbis.design.audio_player_view.view.message.contact.AudioMessageViewDataFactory
import ru.tensor.sbis.design.video_message_view.message.contract.VideoMessageViewDataFactory
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.viewer.decl.slider.ViewerSliderIntentFactory

/**
 * @property viewerSliderIntentFactory фабрика для открытия вложенных в плеере файлов и изображений
 *
 * @author am.boldinov
 */
internal interface WidgetPlayerDependency : WidgetLinkDependency {

    val themedContext: SbisThemedContext

    val initializers: Set<WidgetPlayerStoreInitializer>

    val audioMessageViewDataFactory: AudioMessageViewDataFactory?

    val videoMessageViewDataFactory: VideoMessageViewDataFactory?

    val viewerSliderIntentFactory: ViewerSliderIntentFactory?
}