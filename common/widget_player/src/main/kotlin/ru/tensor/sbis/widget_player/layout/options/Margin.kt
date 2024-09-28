package ru.tensor.sbis.widget_player.layout.options

import android.content.Context
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.widget_player.res.dimen.DimenRes
import ru.tensor.sbis.widget_player.res.dimen.valueInt

/**
 * @author am.boldinov
 */
class Margin(
    val left: DimenRes = DimenRes.valueInt(0),
    val right: DimenRes = DimenRes.valueInt(0),
    val top: DimenRes = DimenRes.valueInt(0),
    val bottom: DimenRes = DimenRes.valueInt(0)
) : StyleOption {

    companion object {

        val EMPTY = Margin()
    }

    override fun accept(view: View) {
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
            var changed = applyMargin(view.context, left, get = {
                lp.leftMargin
            }, set = {
                lp.leftMargin = it
            })
            changed = applyMargin(view.context, top, get = {
                lp.topMargin
            }, set = {
                lp.topMargin = it
            }) || changed
            changed = applyMargin(view.context, right, get = {
                lp.rightMargin
            }, set = {
                lp.rightMargin = it
            }) || changed
            changed = applyMargin(view.context, bottom, get = {
                lp.bottomMargin
            }, set = {
                lp.bottomMargin = it
            }) || changed
            if (changed) {
                view.layoutParams = lp
            }
        }
    }

    private inline fun applyMargin(context: Context, margin: DimenRes, get: () -> Int, set: (Int) -> Unit): Boolean {
        val value = margin.getValuePx(context)
        return if (get() != value) {
            set(value)
            true
        } else {
            false
        }
    }
}