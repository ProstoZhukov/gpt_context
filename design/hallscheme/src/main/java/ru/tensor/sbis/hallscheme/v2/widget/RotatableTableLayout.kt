package ru.tensor.sbis.hallscheme.v2.widget

import android.content.Context
import android.widget.TableLayout
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.util.evaluateLayoutParams
import ru.tensor.sbis.hallscheme.v2.util.rotateItem


/**
 * TableLayout с возможностью поворота.
 * @author aa.gulevskiy
 */
internal class RotatableTableLayout private constructor(context: Context) : TableLayout(context) {

    companion object {
        /**@SelfDocumented*/
        fun newInstance(context: Context, item: HallSchemeItem): RotatableTableLayout {
            return RotatableTableLayout(context).apply {
                evaluateLayoutParams(item)
                rotateItem(item)
            }
        }
    }
}