package ru.tensor.sbis.base_components

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Данные, используемые [ItemSpecificDecoration] для добавления заданных разделителей после элементов
 * [RecyclerView] указанных типов, с возможностью использования особого разделителя для последнего
 * из идущих подряд элементов, тип которых принадлежит к заданному множеству
 *
 * @property itemTypes типы элементов, после которых добавляется разделитель
 * @property divider изображение основного разделителя
 * @property dividerForLastSuchItemInSeries разделитель после последнего элемента из идущих подряд
 * с одним из заданным типов
 * @property ignoreNextTypes типы элементов, перед которыми не добавлять разделитель
 */
data class ItemsDecorationData<out ITEM_TYPE>(
    val itemTypes: List<Class<out ITEM_TYPE>>,
    val divider: Drawable?,
    val dividerForLastSuchItemInSeries: Drawable? = divider,
    val ignoreNextTypes: List<Class<out ITEM_TYPE>> = listOf()
)

/**
 * Декоратор элементов [RecyclerView] для варьирования разделителей, в зависимости от типов элементов
 *
 * @property getItemType лямбда получения типа элемента (не обязательно itemViewType) с заданным индексом
 * @property decorationDataList данные, определяющие разделители после элементов определённых типов
 * @property defaultDecoration разделитель, добавляемый после элементов, чей тип не указан в [decorationDataList]
 * @see ItemsDecorationData
 */
open class ItemSpecificDecoration<ADAPTER: RecyclerView.Adapter<*>, ITEM_TYPE : Any> @JvmOverloads constructor(
    val getItemType: ADAPTER.(position: Int) -> ITEM_TYPE?,
    private val decorationDataList: List<ItemsDecorationData<ITEM_TYPE>>,
    private val defaultDecoration: Drawable? = null,
    private val shouldDrawLastDivider: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val isLastItem = position + 1 >= (parent.adapter?.itemCount ?: 0)
        if (!shouldDrawLastDivider && isLastItem) return

        val itemType = getViewModelType(parent.adapter, position)
        val nextItemType = getViewModelType(parent.adapter, position + 1)

        decorationDataList.forEach {
            if (itemType != null && it.itemTypes.contains(itemType.clazz) && allowDividerBeforeNextItem(it, nextItemType)) {
                val divider = if (itemType.isLastSuchItemOfSublist(nextItemType)) it.dividerForLastSuchItemInSeries else it.divider
                outRect.set(0, 0, 0, divider?.intrinsicHeight ?: 0)
            } else {
                defaultDecoration?.let { outRect.set(0, 0, 0, defaultDecoration.intrinsicHeight) }
            }
        }
    }

    protected open fun ITEM_TYPE.isLastSuchItemOfSublist(nextItemType: ITEM_TYPE?): Boolean = this != nextItemType

    protected open val ITEM_TYPE.clazz: Class<*>
        get() = this as Class<*>

    private fun getViewModelType(adapter: RecyclerView.Adapter<*>?, itemPosition: Int): ITEM_TYPE? {
        if (itemPosition < 0 || itemPosition >= adapter!!.itemCount) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return (adapter as ADAPTER).getItemType(itemPosition)
    }

    private fun allowDividerBeforeNextItem(data: ItemsDecorationData<ITEM_TYPE>, nextItemType: ITEM_TYPE?)
        = (nextItemType != null && data.ignoreNextTypes.contains(nextItemType.clazz)).not()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        loop@ for (i in 0 until parent.childCount) {
            if (!shouldDrawLastDivider && i == parent.childCount - 1) return

            val view = parent.getChildAt(i)
            val params = view.layoutParams as RecyclerView.LayoutParams
            val position = parent.getChildAdapterPosition(view)
            val itemType = getViewModelType(parent.adapter, position)
            val nextItemType = getViewModelType(parent.adapter, position + 1)
            val top = (view.bottom + params.bottomMargin + view.translationY).toInt()

            for (data in decorationDataList) {
                if (itemType != null && data.itemTypes.contains(itemType.clazz) && allowDividerBeforeNextItem(data, nextItemType)) {
                    val divider = if (itemType.isLastSuchItemOfSublist(nextItemType)) data.dividerForLastSuchItemInSeries else data.divider
                    divider?.let {
                        val bottom = top + divider.intrinsicHeight
                        divider.alpha = (view.alpha * 255).toInt()
                        divider.setBounds(0, top, parent.width, bottom)
                        divider.draw(c)
                    }
                    continue@loop
                }
            }
            defaultDecoration?.let {
                val bottom = top + it.intrinsicHeight
                it.alpha = (parent.alpha * 255).toInt()
                it.setBounds(0, top, parent.width, bottom)
                it.draw(c)
            }
        }
    }
}
