package ru.tensor.sbis.widget_player.layout.widget.controller

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.internal.WidgetHostAccessor

/**
 * @author am.boldinov
 */
abstract class StatefulWidgetController<ELEMENT : WidgetElement> : WidgetController<ELEMENT>(),
    ViewModelStoreOwner {

    private lateinit var _viewModelStore: ViewModelStore

    override val viewModelStore: ViewModelStore
        get() = _viewModelStore

    internal fun initialize(
        id: WidgetID,
        lifecycle: Lifecycle,
        hostAccessor: WidgetHostAccessor,
        viewModelStore: ViewModelStore
    ) {
        _viewModelStore = viewModelStore
        initialize(id, lifecycle, hostAccessor)
    }

    @CallSuper
    override fun onDestroy() {

    }

    protected inline fun <reified STATE : WidgetState> state(
        factory: WidgetStateFactory<STATE>
    ): Lazy<STATE> {
        return ViewModelLazy(
            viewModelClass = STATE::class,
            storeProducer = {
                viewModelStore
            },
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return factory.invoke() as T
                    }
                }
            })
    }
}