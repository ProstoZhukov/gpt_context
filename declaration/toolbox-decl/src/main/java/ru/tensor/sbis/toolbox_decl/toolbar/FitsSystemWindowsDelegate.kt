package ru.tensor.sbis.toolbox_decl.toolbar

/**
 * Если в одном контейнере для фрагментов требуется разное значние [fitsSystemWindows].
 *
 * @author ar.leschev
 */
interface FitsSystemWindowsDelegate {
    /** Динамическое значение наезжания на системный контент. */
    val fitsSystemWindows: Boolean
}