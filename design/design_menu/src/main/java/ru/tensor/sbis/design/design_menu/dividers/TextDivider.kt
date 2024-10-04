package ru.tensor.sbis.design.design_menu.dividers

import android.view.View
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.design_menu.api.MenuItem
import ru.tensor.sbis.design.design_menu.utils.updateTextLayoutParams
import ru.tensor.sbis.design.sbis_text_view.SbisTextView

/**
 * Разделитель только текстом.
 *
 * @author ra.geraskin
 */
@Parcelize
class TextDivider(val text: String) : MenuItem, Divider {

    override fun configureDefaultLayoutDivider(leftLine: View, textView: SbisTextView, rightLine: View) {
        leftLine.visibility = View.GONE
        rightLine.visibility = View.INVISIBLE
        textView.apply {
            text = this@TextDivider.text
            visibility = View.VISIBLE
            this@TextDivider.updateTextLayoutParams(this)
        }
    }
}