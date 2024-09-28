package ru.tensor.sbis.widget_player.layout.widget.controller

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.internal.WidgetHostAccessor
import ru.tensor.sbis.widget_player.layout.internal.WidgetViewLifecycleOwner
import kotlin.properties.Delegates

/**
 * @author am.boldinov
 */
abstract class WidgetController<ELEMENT : WidgetElement> : LifecycleOwner {

    private val lifecycleOwner = WidgetViewLifecycleOwner()

    private lateinit var _elementFlow: MutableStateFlow<ELEMENT>

    val elementFlow: StateFlow<ELEMENT> get() = _elementFlow

    internal lateinit var hostAccessor: WidgetHostAccessor

    override val lifecycle: Lifecycle = lifecycleOwner.lifecycle

    var widgetId by Delegates.notNull<WidgetID>()
        private set

    private var created = false

    // TODO widget store provider нужен здесь, чтобы доставать разные элементы.
    internal fun initialize(id: WidgetID, lifecycle: Lifecycle, hostAccessor: WidgetHostAccessor) {
        widgetId = id
        this.hostAccessor = hostAccessor
        lifecycleOwner.dispatchWidgetInitialize(lifecycle)
    }

    internal fun setElement(element: ELEMENT) {
        if (!created) {
            _elementFlow = MutableStateFlow(element)
            onCreate()
            created = true
        } else {
            _elementFlow.tryEmit(element)
        }
    }

    internal fun destroy() {
        lifecycleOwner.dispatchWidgetDestroy()
        onDestroy()
        created = false
    }

    protected abstract fun onCreate()

    protected open fun onDestroy() {}
}