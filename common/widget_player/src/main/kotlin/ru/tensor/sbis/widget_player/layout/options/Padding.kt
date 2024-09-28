package ru.tensor.sbis.widget_player.layout.options

import android.view.View
import androidx.core.view.updatePadding
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.valueInt

/**
 * @author am.boldinov
 */
class Padding(
    val left: DimenRes = DimenRes.valueInt(0),
    val right: DimenRes = DimenRes.valueInt(0),
    val top: DimenRes = DimenRes.valueInt(0),
    val bottom: DimenRes = DimenRes.valueInt(0)
) : StyleOption {

    companion object {

        val EMPTY = Padding()
    }

    override fun accept(view: View) {
        view.updatePadding(
            left = left.getValuePx(view.context),
            top = top.getValuePx(view.context),
            right = right.getValuePx(view.context),
            bottom = bottom.getValuePx(view.context)
        )
    }
}