@file:Suppress("unused")

package ru.tensor.sbis.design.list_utils.decoration.dsl

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.decoration.Decoration
import ru.tensor.sbis.design.list_utils.decoration.drawer.SolidDecorationDrawer
import ru.tensor.sbis.design.list_utils.decoration.offset.BaseOffsetProvider
import ru.tensor.sbis.design.list_utils.decoration.predicate.ViewTypePredicate
import ru.tensor.sbis.design.list_utils.decoration.predicate.viewtype.ExcludeViewTypePredicate
import ru.tensor.sbis.design.list_utils.decoration.predicate.viewtype.IncludeViewTypePredicate

/**
 * @author sa.nikitin
 */

// region Recycler View extensions

/**
 * Добавить декорацию к RecyclerView.
 * @param block - блок, конфигурирующий декорацию
 */
inline infix fun RecyclerView.decorate(block: Decoration.() -> Unit): RecyclerView {
    // Создаем и конфигурируем декорацию
    val decoration = Decoration().apply(block)
    // Добавляем декорацию
    addItemDecoration(decoration)
    return this
}

// endregion

// region Decoration property extensions

/**
 * Задать объект, реализующий один или несколько аспектов декорирования.
 */
infix fun Decoration.drawer(drawer: Any): Decoration {
    setDrawer(drawer)
    return this
}

/**
 * Задать объект, реализующий один или несколько аспектов декорирования.
 */
inline infix fun Decoration.drawer(create: () -> Any): Decoration {
    setDrawer(create())
    return this
}

/**
 * Задать отрисовщик декорации до отрисовки элемента списка.
 */
@Suppress("UsePropertyAccessSyntax")
inline infix fun Decoration.beforeDrawer(create: () -> Decoration.BeforeDrawer): Decoration {
    setBeforeDrawer(create())
    return this
}

/**
 * Задать отрисовщик декорации после отрисовки элемента списка.
 */
@Suppress("UsePropertyAccessSyntax")
inline infix fun Decoration.afterDrawer(create: () -> Decoration.AfterDrawer): Decoration {
    setAfterDrawer(create())
    return this
}

/**
 * Задать делегат условия выбора элементов для декорирования.
 */
inline fun Decoration.predicate(strategy: Decoration.Predicate.Strategy = Decoration.Predicate.Strategy.AND,
                                create: () -> Decoration.Predicate): Decoration {
    setPredicate(create(), strategy)
    return this
}

// endregion

// region Utility decoration extensions

/**
 * Задать отступы для элемента.
 */
@Suppress("UsePropertyAccessSyntax")
inline infix fun Decoration.offsets(block: BaseOffsetProvider.() -> Unit): Decoration {
    setOffsets(BaseOffsetProvider().apply(block))
    return this
}

/**
 * Задать заливку для декорации.
 */
@Suppress("UsePropertyAccessSyntax")
inline infix fun Decoration.solid(block: SolidDecorationDrawer.After.() -> Unit): Decoration {
    setAfterDrawer(SolidDecorationDrawer.After().apply(block))
    return this
}

/**
 * Задать декорируемый тип.
 */
fun Decoration.viewType(viewType: Int): Decoration {
    return predicate(Decoration.Predicate.Strategy.AND) { IncludeViewTypePredicate(viewType) }
}

/**
 * Задать декорируемый тип.
 */
inline fun Decoration.viewType(viewType: () -> Int): Decoration {
    return viewType(viewType())
}

/**
 * Задать перечень декорироруемых типов.
 */
fun Decoration.viewTypes(vararg types: Int): Decoration {
    return predicate(Decoration.Predicate.Strategy.AND) { IncludeViewTypePredicate(*types) }
}

/**
 * Задать перечень декорироруемых типов.
 */
inline fun Decoration.viewTypes(types: () -> IntArray): Decoration {
    return viewTypes(*types())
}

/**
 * DSL для формирования условий наложения декорации. Данный метод
 * применяется для указания первого условия декорирования, заменяя все предыдущие
 * наложенные условия.
 */
@Suppress("FunctionName")
fun Decoration.When(predicate: Decoration.Predicate): Decoration {
    return setPredicate(predicate, Decoration.Predicate.Strategy.REPLACE)
}

/**
 * DSL для формирования условий наложения декорации. Данный метод
 * применяется для присоединения предиката с помощью логической связки И.
 */
@Suppress("FunctionName")
fun Decoration.And(predicate: Decoration.Predicate): Decoration {
    return setPredicate(predicate, Decoration.Predicate.Strategy.AND)
}

/**
 * DSL для формирования условий наложения декорации. Данный метод
 * применяется для присоединения предиката с помощью логической связки ИЛИ.
 */
@Suppress("FunctionName")
fun Decoration.Or(predicate: Decoration.Predicate): Decoration {
    return setPredicate(predicate, Decoration.Predicate.Strategy.OR)
}

/**
 * Создать предикат, проверяющий тип элемента списка. Если тип текущего элемента
 * содержится в [types], тогда элемент нужно декорировать, иначе - нет.
 *
 * @param types - типы элементов, которые нужно декорировать
 */
@Suppress("unused")
fun Decoration.viewTypeIn(vararg types: Int): Decoration.Predicate {
    return IncludeViewTypePredicate(ViewTypePredicate.Target.CURRENT, false, *types)
}

/**
 * Создать предикат, проверяющий тип элемента списка. Если тип текущего элемента
 * не содержится в [types], тогда элемент нужно декорировать, иначе - нет.
 *
 * @param types - типы элементов, которые не нужно декорировать
 */
@Suppress("unused")
fun Decoration.viewTypeNotIn(vararg types: Int): Decoration.Predicate {
    return ExcludeViewTypePredicate(ViewTypePredicate.Target.CURRENT, false, *types)
}

/**
 * Создать предикат, проверяющий тип элемента списка. Если тип предыдущего элемента
 * содержится в [types], тогда элемент нужно декорировать, иначе - нет.
 *
 * @param types         - типы элементов, после которых нужно декорировать
 * @param decorateFirst - нужно ли декорировать первый элемент (перед ним нет элемента списка, условие не применить)
 */
@Suppress("unused")
fun Decoration.previousViewTypeIn(vararg types: Int, decorateFirst: Boolean = false): Decoration.Predicate {
    return IncludeViewTypePredicate(ViewTypePredicate.Target.PREVIOUS, decorateFirst, *types)
}

/**
 * Создать предикат, проверяющий тип элемента списка. Если тип предыдущего элемента
 * не содержится в [types], тогда элемент нужно декорировать, иначе - нет.
 *
 * @param types         - типы элементов, после которых не нужно декорировать
 * @param decorateFirst - нужно ли декорировать первый элемент (перед ним нет элемента списка, условие не применить)
 */
@Suppress("unused")
fun Decoration.previousViewTypeNotIn(vararg types: Int, decorateFirst: Boolean = true): Decoration.Predicate {
    return ExcludeViewTypePredicate(ViewTypePredicate.Target.PREVIOUS, decorateFirst, *types)
}

/**
 * Создать предикат, проверяющий тип элемента списка. Если тип следующего элемента
 * содержится в [types], тогда элемент нужно декорировать, иначе - нет.
 *
 * @param types         - типы элементов, перед которыми нужно декорировать
 * @param decorateLast  - нужно ли декорировать последний элемент (после него нет элемента списка, условие не применить)
 */
@Suppress("unused")
fun Decoration.nextViewTypeIn(vararg types: Int, decorateLast: Boolean = false): Decoration.Predicate {
    return IncludeViewTypePredicate(ViewTypePredicate.Target.NEXT, decorateLast, *types)
}

/**
 * Создать предикат, проверяющий тип элемента списка. Если тип следующего элемента
 * не содержится в [types], тогда элемент нужно декорировать, иначе - нет.
 *
 * @param types         - типы элементов, перед которыми не нужно декорировать
 * @param decorateLast  - нужно ли декорировать последний элемент (после него нет элемента списка, условие не применить)
 */
@Suppress("unused")
fun Decoration.nextViewTypeNotIn(vararg types: Int, decorateLast: Boolean = true): Decoration.Predicate {
    return ExcludeViewTypePredicate(ViewTypePredicate.Target.NEXT, decorateLast, *types)
}

// endregion