package ru.tensor.sbis.design.design_menu.dividers

import android.view.View
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.design_menu.utils.updateTextLayoutParams
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalAlignment

/**
 * Разделитель текстом и линией.
 *
 * @author ra.geraskin
 */
@Parcelize
class TextLineDivider(
    val text: String,
    val textAlignment: HorizontalAlignment = HorizontalAlignment.LEFT
) : Divider {

    override fun configureDefaultLayoutDivider(leftLine: View, textView: SbisTextView, rightLine: View) {
        textView.apply {
            text = this@TextLineDivider.text
            visibility = View.VISIBLE
            updateTextLayoutParams(this)
        }
        when (textAlignment) {
            HorizontalAlignment.LEFT -> {
                leftLine.visibility = View.GONE
                rightLine.visibility = View.VISIBLE
            }

            HorizontalAlignment.CENTER -> {
                leftLine.visibility = View.VISIBLE
                rightLine.visibility = View.VISIBLE
            }

            HorizontalAlignment.RIGHT -> {
                leftLine.visibility = View.VISIBLE
                rightLine.visibility = View.GONE
            }
        }
    }
}