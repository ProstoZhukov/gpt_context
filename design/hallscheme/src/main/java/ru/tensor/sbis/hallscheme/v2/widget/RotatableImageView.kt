package ru.tensor.sbis.hallscheme.v2.widget

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.util.evaluateLayoutParams
import ru.tensor.sbis.hallscheme.v2.util.rotateItem


/**
 * ImageView с возможностью поворота.
 * @author aa.gulevskiy
 */
internal class RotatableImageView private constructor(context: Context) : AppCompatImageView(context) {

    companion object {
        /**@SelfDocumented*/
        fun newInstance(context: Context, item: HallSchemeItem, drawable: Drawable?): RotatableImageView {
            return RotatableImageView(context).apply {
                adjustViewBounds = true
                evaluateLayoutParams(item)
                setImageDrawable(drawable)
                rotateItem(item)
            }
        }
    }
}