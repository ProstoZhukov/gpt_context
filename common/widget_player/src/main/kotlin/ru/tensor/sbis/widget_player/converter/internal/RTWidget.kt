package ru.tensor.sbis.widget_player.converter.internal

//import android.content.Context
//import android.content.MutableContextWrapper
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import ru.tensor.sbis.objectpool.base.ConcurrentObjectPool
//import ru.tensor.sbis.widget_player.config.WidgetOptions
//import ru.tensor.sbis.widget_player.converter.WidgetResources
//import ru.tensor.sbis.widget_player.converter.internal.RTWidgetBuilder.Companion.create
//import ru.tensor.sbis.widget_player.converter.attributes.WidgetAttributes
//import ru.tensor.sbis.widget_player.converter.element.WidgetElement
//import ru.tensor.sbis.widget_player.layout.VerticalBlockLayout
//import ru.tensor.sbis.widget_player.layout.inline.InlineLayout
//import ru.tensor.sbis.widget_player.widget.list.item.ListItemElement
//import ru.tensor.sbis.widget_player.widget.paragraph.ParagraphElement

/**
 * // TODO for tests
 *
 * @author am.boldinov
 */
//interface RTWidgetBuilder {
//
//    fun build(environment: WidgetEnvironment1): RTWidget
//
//    companion object {
//
//        fun <E : WidgetElement> RTWidgetBuilder.create(content: WidgetInfo<E>.() -> Unit): RTWidget {
//            @Suppress("UNCHECKED_CAST")
//            return RTWidget(content as WidgetInfo<WidgetElement>.() -> Unit)
//        }
//    }
//}
//
//class WidgetInfo<ELEMENT : WidgetElement> {
//
//    private lateinit var factory: (ElementParams) -> ELEMENT
//
//    private lateinit var renderer: (Renderer<ELEMENT>) -> Unit
//
//    private var precompute: (ELEMENT.() -> Unit)? = null
//
//    private val children = mutableMapOf<String, RTWidget>()
//
//    fun element(factory: ElementParams.() -> ELEMENT): ElementProcessor<ELEMENT> {
//        this.factory = factory
//        return object : ElementProcessor<ELEMENT> {
//            override fun doAfterProcessing(block: ELEMENT.() -> Unit) {
//                precompute
//            }
//        }
//    }
//
//    fun doAfterProcessing(block: ELEMENT.() -> Unit) {
//        this.precompute = block
//    }
//
//    fun renderer(renderer: Renderer<ELEMENT>.() -> Unit) {
//        this.renderer = renderer
//    }
//
//    @Suppress("UNCHECKED_CAST")
//    fun <E : WidgetElement> child(vararg tag: String, content: WidgetInfo<E>.() -> Unit) {
//        tag.forEach {
//            children[it] = RTWidget(content as WidgetInfo<WidgetElement>.() -> Unit)
//        }
//    }
//}
//
//@DslMarker
//annotation class RendererDslMarker
//
//interface ElementProcessor<ELEMENT : WidgetElement> {
//
//    fun doAfterProcessing(block: ELEMENT.() -> Unit)
//}
//
//class ElementParams(val tag: String, val attributes: WidgetAttributes, val resources: WidgetResources)
//
//
//object ViewProviderStore {
//
//    private val poolStore = mutableMapOf<Class<out View>, ViewPool>()
//
//    fun from(clazz: Class<out View>, poolSize: Int = 10): ViewPool {
//        var pool = poolStore[clazz]
//        if (pool == null) {
//            pool = ViewPool(poolSize)
//            poolStore[clazz] = pool
//        }
//        return pool
//    }
//}
//
//class ViewPool(capacity: Int) : ConcurrentObjectPool<View>(capacity)
//
//class ParentContext(val parent: ViewGroup) {
//
//    val context = MutableContextWrapper(parent.context)
//}
//
//@RendererDslMarker
//class Renderer<ELEMENT : WidgetElement>(
//    val parent: ViewGroup
//) {
//
//    var viewFactory: (ParentContext.() -> View)? = null
//
//    inline fun <reified VIEW: View> view(noinline factory: ParentContext.() -> VIEW): VIEW {
//        this.viewFactory = factory as ParentContext.() -> View
//        return ViewProviderStore.from(VIEW::class.java).take() as? VIEW ?: factory.invoke(ParentContext(parent))
//    }
//
//    inline fun render(render: (ELEMENT) -> View) {
//
//    }
//
//}
//
//interface ViewProvider<VIEW : View> {
//
//    fun provide(provider: ViewGroup.() -> VIEW): VIEW
//}
//
//class Bindable {
//
//    lateinit var withElement: (WidgetElement) -> Unit
//
//    inline fun <reified E : WidgetElement> withElement(noinline element: (E) -> Unit) {
//        @Suppress("UNCHECKED_CAST")
//        withElement = element as (WidgetElement) -> Unit
//    }
//}
//
//class RenderObject(val parent: ViewGroup)
//
//class RTWidget(val content: WidgetInfo<WidgetElement>.() -> Unit) {
//
//    private val info = WidgetInfo<WidgetElement>()
//
//    init {
//        content.invoke(info)
//    }
//
//}
//
//
//class TestWidgetBuilder : RTWidgetBuilder {
//
//    override fun build(environment: WidgetEnvironment1) = create {
//        element {
//            ParagraphElement(tag, attributes, resources)
//            // TODO Отдельно style reducer
//        }.doAfterProcessing {
//
//        }
//        renderer {
//            render {
//                view {
//                    InlineLayout(context)
//                }
//            }
//        }
//        child("ListItem") {
//            element {
//                ListItemElement(tag, attributes, resources, false)
//            }
//            renderer {
//                render { el ->
//                    view {
//                        if (el is ListItemElement) {
//                            VerticalBlockLayout(context)
//                        } else {
//                            FrameLayout(context)
//                        }
//                    }
////                    if (checked) {
////                        view { VerticalBlockLayout(context) }
////                    } else {
////                        view { FrameLayout(context) }
////                    }
//                }
//            }
//        }
//    }
//
//}
//
//// TODO stateful widget слушает lifecycle экрана (возможность присоединять view model) и метод UpdateState()
//// TODO player смотрит у parent есть ли childInterceptor, если есть, то смотрит по какому тегу, если тег совпадает - передает ему управление
//
//class WidgetEnvironment1(
//    val androidContext: Context,
//    val options: WidgetOptions
//)

//fun interface WidgetComponentFactory<E : WidgetElement> {
//
//    fun WidgetOptions.build(): WidgetHolder<E>
//}
//
//class WidgetHolder<E : WidgetElement>(
//    elementFactory: WidgetElementFactory<E>,
//    binder: BinderContainer<E>
//)
//
//fun interface BinderContainer<E : WidgetElement> {
//
//    fun BinderContext<E>.bind()
//}
//
//class BinderContext<E : WidgetElement>(val context: Context, val element: E) {
//
//    inline fun <reified VIEW: View> view(factory: Context.() -> VIEW): VIEW {
//        return ViewProviderStore.from(VIEW::class.java).take() as? VIEW ?: factory.invoke(context)
//    }
//}
//
//class Binder<E : WidgetElement> {
//
//    var context: Context? = null
//}
//
//object ViewProviderStore {
//
//    private val poolStore = mutableMapOf<Class<out View>, ViewPool>()
//
//    fun from(clazz: Class<out View>, poolSize: Int = 10): ViewPool {
//        var pool = poolStore[clazz]
//        if (pool == null) {
//            pool = ViewPool(poolSize)
//            poolStore[clazz] = pool
//        }
//        return pool
//    }
//}