package ru.tensor.sbis.design.design_menu.dividers

import android.view.View
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Разделитель линией.
 *
 * @author ra.geraskin
 */
@Parcelize
object LineDivider : Divider {
    override fun configureDefaultLayoutDivider(leftLine: View, textView: SbisTextView, rightLine: View) {
        textView.visibility = View.GONE
        leftLine.visibility = View.VISIBLE
        rightLine.visibility = View.VISIBLE
    }
}