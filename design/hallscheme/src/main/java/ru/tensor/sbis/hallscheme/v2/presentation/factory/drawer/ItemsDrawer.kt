package ru.tensor.sbis.hallscheme.v2.presentation.factory.drawer

import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Shader
import android.view.ViewGroup
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.TableTexture
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi
import ru.tensor.sbis.hallscheme.v2.widget.AbstractItemView
import java.util.UUID

/**
 * Интерфейс для отображения элементов схемы.
 * @author aa.gulevskiy
 */
internal abstract class HallSchemeDrawer {
    /**
     * Отображает элементы схемы.
     */
    abstract fun drawItems(
        items: List<HallSchemeItemUi>,
        viewGroup: ViewGroup,
        clickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    )

    /**
     * Осуществляет поиск вью элемента схемы зала.
     */
    internal fun findTable(tablesLayout: ViewGroup, id: UUID?, cloudId: Int?): AbstractItemView? {
        var viewForSelection: AbstractItemView? = null
        for (i in 0 until tablesLayout.childCount) {
            viewForSelection = tablesLayout.getChildAt(i) as? AbstractItemView
            viewForSelection?.let {
                if ((id !=null && it.tableId == id) || (cloudId != null && it.cloudId == cloudId)) {
                    return viewForSelection
                }
            }
        }

        return viewForSelection
    }

    /**
     * Осуществляет поиск вью элемента схемы зала и осуществляет над ним определённое действие.
     */
    fun findViewAndDoAction(tablesLayout: ViewGroup, id: UUID?, cloudId: Int?, action: (AbstractItemView) -> Unit) {
        var view: AbstractItemView?
        for (i in 0 until tablesLayout.childCount) {
            view = tablesLayout.getChildAt(i) as? AbstractItemView
            view?.let { itemView ->
                if ((id !=null && itemView.tableId == id) || (cloudId != null && itemView.cloudId == cloudId)) {
                    action.invoke(itemView)
                }
            }
        }
    }
}

/**
 * Отображает элементы плоской схемы.
 */
internal class DrawerFlat : HallSchemeDrawer() {
    override fun drawItems(
        items: List<HallSchemeItemUi>,
        viewGroup: ViewGroup,
        clickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        items.asSequence().forEach { it.draw(viewGroup, clickListener) }
    }
}

/**
 * Отображает элементы объёмной схемы.
 */
internal class Drawer3D(private val tableTexture: TableTexture) : HallSchemeDrawer() {
    override fun drawItems(
        items: List<HallSchemeItemUi>,
        viewGroup: ViewGroup,
        clickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {

        val shaderMode = Shader.TileMode.REPEAT
        val pressedBitmap = BitmapFactory.decodeResource(
            viewGroup.context.resources, tableTexture.pressedImageResId
        )
        val pressedShader = BitmapShader(pressedBitmap, shaderMode, shaderMode)

        val unpressedBitmap = BitmapFactory.decodeResource(
            viewGroup.context.resources, tableTexture.unpressedImageResId
        )
        val unpressedShader = BitmapShader(unpressedBitmap, shaderMode, shaderMode)

        items.asSequence().forEach {
            it.draw3D(viewGroup, pressedShader, unpressedShader, clickListener)
        }
    }
}