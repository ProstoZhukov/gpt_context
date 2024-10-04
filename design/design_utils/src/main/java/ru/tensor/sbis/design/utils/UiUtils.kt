/**
 * Общие инструменты по работе с UI элементами
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Позволяет найти ближайший родительский view заданного типа.
 *
 * @param condition условие, которому должен удовлетворять искомый [View]
 */
inline fun <reified PARENT_TYPE : View> findViewParent(
    view: View,
    condition: (View) -> Boolean = { true }
): PARENT_TYPE? {
    var parent = view.parent
    while (parent != null) {
        if (parent is PARENT_TYPE && condition(parent)) {
            return parent
        }
        parent = parent.parent
    }
    return null
}

/**
 * Выполняет поиск [View] в иерархии первого элемента подходящего типа и удовлетворяющего [predicate], не углубляясь
 * в иерархию дальше чем на [maxDepth] уровней.
 */
inline fun <reified T : View> findViewInHierarchy(
    root: View,
    maxDepth: Int = Int.MAX_VALUE,
    noinline predicate: (View) -> Boolean = { true }
) = findViewInHierarchy(T::class, root, predicate, maxDepth, 1)

/**
 * Получить имя ресурса идентификатора [View], если это возможно.
 */
fun getViewIdName(view: View): String {
    return try {
        view.resources.getResourceName(view.id)
    } catch (e: Exception) {
        view.id.toString()
    }
}

/**
 * Реализует inline функцию [findViewInHierarchy], не предназначен для использования напрямую.
 */
fun <VIEW_TYPE : View> findViewInHierarchy(
    type: KClass<VIEW_TYPE>,
    view: View,
    predicate: (View) -> Boolean = { true },
    maxDepth: Int,
    depth: Int
): VIEW_TYPE? {
    if (type.isInstance(view) && predicate(view)) {
        return type.cast(view)
    }
    if (view is ViewGroup && depth < maxDepth) {
        view.children.forEach { child ->
            findViewInHierarchy(type, child, predicate, maxDepth, depth + 1)
                ?.let { return it }
        }
    }
    return null
}