package ru.tensor.sbis.widget_player.converter

import kotlinx.coroutines.flow.Flow

/**
 * @author am.boldinov
 */
class WidgetBodyStream(
    val body: Flow<WidgetBodyEvent>,
    val throbberVisibility: Flow<Boolean>
)