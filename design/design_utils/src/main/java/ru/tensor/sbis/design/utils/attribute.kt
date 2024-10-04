/**
 * Набор инструментов для работы с атрибутами View
 *
 * @author ma.kolpakov
 * Создан 9/28/2019
 */

@file:JvmName("ViewAttributeUtil")
package ru.tensor.sbis.design.utils

import android.content.Context
import android.content.res.TypedArray
import androidx.annotation.StyleableRes

private val ATTRIBUTE_MATCHER = "[a-zA-Z,0-9_]+".toRegex()

/**
 * Проверка корректности строки с атрибутами
 */
internal val CharSequence.isValidReferenceString get() = ATTRIBUTE_MATCHER.matches(this)

/**
 * Получение набора id из параметра [idSetAttribute]. Для простоты обращения метод использует id только из пространства
 * [ru.tensor.sbis.design.R]
 *
 * @return
 *  - атрибут не указан - `null`
 *  - иначе - набор идентификаторов
 */
fun TypedArray.getIdSet(context: Context, @StyleableRes idSetAttribute: Int): Set<Int>? {
    val attrValue = getString(idSetAttribute) ?: return null
    require(attrValue.isValidReferenceString) { "Invalid id set string '$attrValue'" }

    val idTokens = attrValue.split(',')
    val idSet = idTokens.map { getIdByName(context, it) }.toSet()

    require(idTokens.size == idSet.size) { "Parameter string should be without duplicates" }
    return idSet
}

/**
 * Вызов [handler] с набором id из параметра [idSetAttribute].
 *
 * @param handler вызывается только в том случае, если атрибут указан и набор не пустой
 *
 * @see getIdSet
 */
internal inline fun TypedArray.withIdSet(
    context: Context, @StyleableRes idSetAttribute: Int, handler: (Set<Int>) -> Unit
) {
    getIdSet(context, idSetAttribute)?.takeIf(Collection<*>::isNotEmpty)?.run(handler)
}

/**
 * Получение значений для id реализовано на основе решения для ConstraintLayout_Layout_constraint_referenced_ids
 */
private fun getIdByName(context: Context, idName: String) = try {
    R.id::class.java.getField(idName).getInt(null /* можно передать null так как получение из статичного поля */)
} catch (ex: NoSuchFieldException) {
    // решение для id из внешних пакетов
    context.resources.getIdentifier(idName, "id", context.packageName)
}

/**
 * Проверяет, есть ли данный флаг в множестве флагов
 */
infix fun Int.hasFlag(flag: Int) = this or flag == this