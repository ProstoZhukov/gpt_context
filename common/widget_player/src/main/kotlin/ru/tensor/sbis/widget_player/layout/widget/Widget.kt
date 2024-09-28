package ru.tensor.sbis.widget_player.layout.widget

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.widget_player.converter.WidgetID
import ru.tensor.sbis.widget_player.converter.element.WidgetElement
import ru.tensor.sbis.widget_player.layout.internal.WidgetHostAccessor
import ru.tensor.sbis.widget_player.layout.internal.WidgetStateStore
import ru.tensor.sbis.widget_player.layout.internal.WidgetViewLifecycleOwner
import ru.tensor.sbis.widget_player.layout.widget.controller.WidgetController
import ru.tensor.sbis.widget_player.layout.widget.controller.StatefulWidgetController
import ru.tensor.sbis.widget_player.util.setWidget

/**
 *
 *
 * @author am.boldinov
 */
open class Widget<ELEMENT : WidgetElement> private constructor(
    private val context: WidgetContext,
    private val renderer: WidgetRenderer<ELEMENT>,
    private val controllerFactory: (() -> WidgetController<ELEMENT>)?,
    private val lifecycleOwner: WidgetViewLifecycleOwner
) : LifecycleOwner by lifecycleOwner {

    constructor(
        context: WidgetContext,
        renderer: WidgetRenderer<ELEMENT>,
        controller: (() -> WidgetController<ELEMENT>)? = null
    ) : this(
        context,
        renderer,
        controller,
        WidgetViewLifecycleOwner()
    )

    internal val view: View get() = renderer.view

    private var controller: WidgetController<ELEMENT>? = null

    var id: WidgetID? = null
        private set(value) {
            if (field != value) {
                field = value
                initController(value)
            }
        }

    private var isRecyclable = false

    private var isRecycled = false

    private lateinit var stateStore: Lazy<WidgetStateStore>
    private lateinit var hostAccessor: WidgetHostAccessor

    init {
        @Suppress("LeakingThis")
        view.setWidget(this)
    }

    /**
     * ViewModelStore и Lifecycle должны быть привязаны к ID виджета.
     * // TODO inject navigation params (containerId)
     */
    internal fun dispatchInitialize(
        lifecycle: Lifecycle,
        stateStore: Lazy<WidgetStateStore>,
        hostAccessor: WidgetHostAccessor
    ) {
        this.stateStore = stateStore
        this.hostAccessor = hostAccessor
        lifecycleOwner.dispatchWidgetInitialize(lifecycle)
    }

    // TODO set navigation container id
    // TODO navigate (router)
    //protected val fragmentManager get() = FragmentManager.findFragment<Fragment>(view).childFragmentManager

    internal open fun dispatchAttachedToPlayer() {
        renderer.onAttachedToPlayer()
        onAttachedToPlayer()
        // move to on start
    }

    internal open fun dispatchDetachedFromPlayer() {
        // TODO подумать когда устанавливается слушатель, мне нужно получить доступ к данным по текущему виджету
        renderer.onDetachedFromPlayer()
        onDetachedFromPlayer()
        // move to on stop
    }

    protected open fun onAttachedToPlayer() {}

    protected open fun onDetachedFromPlayer() {}

    // TODO Жизненный цикл биндера короче, у него нет метода onRecycle, он переходит в destroy состояние.
    //  Сам виджет переходит в onRecycle состояние, но не переходит в onDestroy.

    /**
     * Вызывается в случае если больше не используется после полного удаления.
     * Виджет может попасть во ViewPool на переиспользование, но будет destroyed, т.к в данный момент больше не используется.
     */
    internal fun dispatchDestroy() {
        lifecycleOwner.dispatchWidgetDestroy()
        id = null
        renderer.onDestroy()
    }

    /**
     * При восставновлении из ViewPool. Вызывается перед биндингом.
     */
    internal fun dispatchRestore(uiContext: Context) {
        context.baseContext = uiContext
        isRecycled = false
        onRestore()
    }

    protected open fun onRestore() {}

    internal fun dispatchRecycle() {
        id = null
        isRecycled = true
        context.baseContext = context.applicationContext
        renderer.onRecycle()
    }

    internal fun bind(element: ELEMENT) {
        id = element.id
        controller?.setElement(element)
        renderer.render(element)
    }

    private fun initController(id: WidgetID?) {
        controller?.destroy()
        controller = null
        if (id != null) {
            controller = controllerFactory?.invoke()
            (controller as? StatefulWidgetController<ELEMENT>)?.initialize(
                id = id,
                lifecycle = lifecycle,
                hostAccessor = hostAccessor,
                viewModelStore = stateStore.value.getOrCreate(id)
            ) ?: run {
                controller?.initialize(id, lifecycle, hostAccessor)
            }
        }
    }
}