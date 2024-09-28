package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * @author am.boldinov
 */
class WidgetEnvironment(
    val context: SbisThemedContext,
    val resources: WidgetResources,
    val textConverter: FormattedTextConverter
)