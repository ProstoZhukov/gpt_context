package ru.tensor.sbis.design.gallery.impl.utils

import android.database.Cursor

/**
 * Создать (вытащить) столбец курсора с int значениями
 */
fun <T> Cursor.intColumn(
    name: String,
    defaultValue: Int? = null
): CursorColumn<T, Int?> =
    object : CursorColumn<T, Int?>(this, name, defaultValue) {
        override fun get(columnIndex: Int): Int = cursor.getInt(columnIndex)
    }

/**
 * Создать (вытащить) столбец курсора с long значениями
 */
fun <T> Cursor.longColumn(
    name: String,
    defaultValue: Long? = null
): CursorColumn<T, Long?> =
    object : CursorColumn<T, Long?>(this, name, defaultValue) {
        override fun get(columnIndex: Int): Long = cursor.getLong(columnIndex)
    }

/**
 * Создать (вытащить) столбец курсора с string значениями
 */
fun <T> Cursor.stringColumn(
    name: String,
    defaultValue: String? = null
): CursorColumn<T, String?> =
    object : CursorColumn<T, String?>(this, name, defaultValue) {
        override fun get(columnIndex: Int): String? = cursor.getString(columnIndex)?.ifEmpty { null }
    }