package ru.tensor.sbis.widget_player.converter.element

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

/**
 * Производит поиск в родительской иерархии элемент с необходимым типом.
 */
inline fun <reified T : WidgetElement> WidgetElement.findParentAs(
    noinline predicate: ((element: T) -> Boolean)? = null
): T? = findParentAs(T::class, predicate)

fun <T : WidgetElement> WidgetElement.findParentAs(
    type: KClass<T>,
    predicate: ((element: T) -> Boolean)? = null
): T? {
    return type.safeCast(parent)?.takeIf {
        predicate == null || predicate(it)
    } ?: parent?.findParentAs(type, predicate)
}