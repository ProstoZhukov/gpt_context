package ru.tensor.sbis.design.gallery.impl.utils

import android.database.Cursor
import ru.tensor.sbis.common.util.safeThrow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Столбец курсора
 */
abstract class CursorColumn<T, V : Any?>(
    protected val cursor: Cursor,
    name: String,
    private val defaultValue: V
) : ReadOnlyProperty<T, V> {

    private val columnIndex: Int? = cursor.getColumnIndex(name).takeIf { it >= 0 }

    override fun getValue(thisRef: T, property: KProperty<*>): V =
        if (columnIndex != null) {
            try {
                get(columnIndex)
            } catch (e: Exception) {
                safeThrow(e)
                defaultValue
            }
        } else {
            defaultValue
        }

    fun isExists(): Boolean = columnIndex != -1

    protected abstract fun get(columnIndex: Int): V
}