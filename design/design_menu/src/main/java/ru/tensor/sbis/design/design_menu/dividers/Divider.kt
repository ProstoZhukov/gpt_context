package ru.tensor.sbis.design.design_menu.dividers

import android.view.View
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Общий интерфейс разделителей элементов меню.
 *
 * @author ra.geraskin
 */
sealed interface Divider : MenuItem {
    fun configureDefaultLayoutDivider(leftLine: View, textView: SbisTextView, rightLine: View)
}