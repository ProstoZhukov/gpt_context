package ru.tensor.sbis.viper.helper.recyclerviewitemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.list_utils.decoration.Decoration
import ru.tensor.sbis.design.list_utils.decoration.drawer.AlphaDecorationDrawer
import ru.tensor.sbis.design.utils.extentions.getColorFrom
import ru.tensor.sbis.design.R as RDesign

/**
 * Уточнение работы декораторов: декораторы берут свое расстояние из вью, для которой декоратор создается
 * Соответственно, при применении декоратора, необходимо понимать, что контент во вью будет занимать
 * "width - decoratorWidth"
 * Особенно важно это учитывать для элементов списка, так как из-за декораторов могут быть по итогу неравные по
 * размеру ячейки
 *
 * Пример: Recycler с GridLayoutManager и 2 столбцами. При добавлении декоратора слева для крайнего левого итема, и
 * декораторов для всех итемов в строке справа, по итогу получится, что первая ячейка будет уже, чем другие,
 * так как для нее был применен декоратор слева
 */

/**
 * Разделитель элементов списка высотой 6dp
 */
class DefaultDividerItemDecoration(context: Context) : Decoration() {
    init {
        setDrawer(
            OneSideDividerDrawerBefore(
                OneSideDividerDrawerBefore.Side.BOTTOM,
                createDecorationDrawable(
                    context.resources.dp(6),
                    context.getColorFrom(RDesign.color.palette_color_gray3)
                )
            )
        )
    }
}

/**
 * Разделитель элементов списка
 */
class OneSideDividerItemDecoration(
    context: Context,
    side: OneSideDividerDrawerBefore.Side,
    drawable: Drawable = createDecorationDrawable(
        context.resources.dp(6),
        context.getColorFrom(RDesign.color.palette_color_gray3)
    )
) : Decoration() {
    init {
        setDrawer(OneSideDividerDrawerBefore(side, drawable))
    }
}

/**
 * Разделить Drawable для одной стороны
 */
class OneSideDividerDrawerBefore(
    private val side: Side,
    private val drawable: Drawable
) : AlphaDecorationDrawer(DEFAULT_ADJUST_ALPHA), Decoration.BeforeDrawer,
    Decoration.ItemOffsetProvider {

    override fun getItemOffsets(
        outRect: Rect,
        itemView: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(
            if (side == Side.LEFT) drawable.intrinsicWidth else 0,
            if (side == Side.TOP) drawable.intrinsicHeight else 0,
            if (side == Side.RIGHT) drawable.intrinsicWidth else 0,
            if (side == Side.BOTTOM) drawable.intrinsicHeight else 0
        )
    }

    override fun draw(canvas: Canvas, itemView: View, left: Int, top: Int, right: Int, bottom: Int, offsets: Rect) {
        drawable.setBounds(left, top, right, bottom)
        drawable.alpha = if (adjustAlpha) intAlpha(itemView) else ALPHA_OPAQUE
        drawable.draw(canvas)
    }

    /**
     * Сторона для разделителя
     */
    enum class Side {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }
}