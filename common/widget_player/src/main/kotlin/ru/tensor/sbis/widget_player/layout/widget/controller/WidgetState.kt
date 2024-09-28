package ru.tensor.sbis.widget_player.layout.widget.controller

import androidx.lifecycle.ViewModel

/**
 * @author am.boldinov
 */
open class WidgetState : ViewModel()

fun interface WidgetStateFactory<STATE : WidgetState> : () -> STATE