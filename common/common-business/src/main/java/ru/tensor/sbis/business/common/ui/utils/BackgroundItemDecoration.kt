package ru.tensor.sbis.business.common.ui.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.ItemsDecorationData
import ru.tensor.sbis.business.common.R
import ru.tensor.sbis.business.common.ui.base.adapter.data.BottomStub
import ru.tensor.sbis.business.common.ui.bind_adapter.setPaddingBottomAttrRes
import ru.tensor.sbis.business.common.ui.bind_adapter.setPaddingTopAttrRes

/**
 * Позиция элемента в списке.
 *
 * @author aa.kobeleva
 */
enum class ItemPosition {
    /** Первый */
    FIRST,

    /** Последний */
    LAST
}

/**
 * Данные, используемые [BackgroundItemDecoration] для добавления заданного бэкграунда элементам
 * [RecyclerView] указанных типов в зависимости от их позиции в списке идущих подряд элементов,
 * тип которых принадлежит к заданному множеству.
 *
 * @property itemTypes типы элементов для которых меняется заливка
 * @property background используемый бэкграунд для [itemTypes]
 * @property position позиция в списке элементах типа [itemTypes]
 * @property ignoreNextTypes типы элементов, перед которыми не нужно менять бэкграунд
 * @property ignorePreviousTypes типы элементов, после которых не нужно менять бэкграунд
 * @property padding внутренний отступ элемента. Сверху для [ItemPosition.FIRST] или снизу для [ItemPosition.LAST]
 * @property expand требуется ли растянуть элемент [itemTypes], т.е. заполнить пустое пространство под ячейкой ее же бэкграундом [background].
 * Для [ItemPosition.LAST] выполняется проверка что элемент действительно последний в списке, для [ItemPosition.FIRST] проверка отсутствует.
 * @property ignoreOnRemoveAnimation true если не менять бэкграунд во время анимации удаления элементов из списка
 *
 * @author aa.kobeleva
 */
data class BackgroundItemsDecorationData<out ITEM_TYPE>(
    val itemTypes: List<Class<out ITEM_TYPE>>,
    @DrawableRes val background: Int,
    val position: ItemPosition,
    val ignoreNextTypes: List<Class<out ITEM_TYPE>> = listOf(),
    val ignorePreviousTypes: List<Class<out ITEM_TYPE>> = listOf(),
    @AttrRes val padding: Int? = null,
    val expand: Boolean = false,
    val ignoreOnRemoveAnimation: Boolean = false
)

/**
 * Декоратор элементов [RecyclerView] для варьирования бэкгранда, в зависимости от типов элементов.
 *
 * @property getItemType лямбда получения типа элемента (не обязательно itemViewType) с заданным индексом
 * @property decorationDataList данные, определяющие бэкграунды после элементов определённых типов
 * @see ItemsDecorationData
 *
 * @author aa.kobeleva
 */
class BackgroundItemDecoration<ADAPTER : RecyclerView.Adapter<*>, ITEM_TYPE : Any> constructor(
    val getItemType: ADAPTER.(position: Int) -> ITEM_TYPE?,
    private val decorationDataList: List<BackgroundItemsDecorationData<ITEM_TYPE>>,
    @DrawableRes private val defaultDecoration: Int? = null,
) : RecyclerView.ItemDecoration() {

    private fun ITEM_TYPE.isFirstItemOfSublist(prevItemType: ITEM_TYPE?): Boolean = this != prevItemType

    private fun ITEM_TYPE.isLastSuchItemOfSublist(nextItemType: ITEM_TYPE?): Boolean = this != nextItemType

    private val ITEM_TYPE.clazz: Class<*>
        get() = this as Class<*>

    private fun RecyclerView.Adapter<*>?.getViewModelType(itemPosition: Int): ITEM_TYPE? {
        if (this == null) return null
        if (itemPosition < 0 || itemPosition >= itemCount) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return (this as ADAPTER).getItemType(itemPosition)
    }

    private fun allowChangeBeforeNextItem(data: BackgroundItemsDecorationData<ITEM_TYPE>, nextItemType: ITEM_TYPE?) =
        (nextItemType != null && data.ignoreNextTypes.contains(nextItemType.clazz)).not()

    private fun allowChangeAfterPreviousItem(data: BackgroundItemsDecorationData<ITEM_TYPE>, prevItemType: ITEM_TYPE?) =
        (prevItemType != null && data.ignorePreviousTypes.contains(prevItemType.clazz)).not()

    private fun allowChange(
        data: BackgroundItemsDecorationData<ITEM_TYPE>,
        itemType: ITEM_TYPE,
        prevItemType: ITEM_TYPE?,
        nextItemType: ITEM_TYPE?
    ): Boolean =
        (data.position == ItemPosition.FIRST && itemType.isFirstItemOfSublist(prevItemType) && allowChangeAfterPreviousItem(
            data,
            prevItemType
        )) || (data.position == ItemPosition.LAST && itemType.isLastSuchItemOfSublist(nextItemType) && allowChangeBeforeNextItem(
            data,
            nextItemType
        ))

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        loop@ for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val itemType = parent.adapter.getViewModelType(position)
            val prevItemType = parent.adapter.getViewModelType(position - 1)
            val nextItemType = parent.adapter.getViewModelType(position + 1)

            for (data in decorationDataList) {
                if (data.ignoreOnRemoveAnimation && parent.isAnimating && parent.childCount > state.itemCount) {
                    continue@loop
                }
                if (itemType != null && data.itemTypes.contains(itemType.clazz)) {
                    if (allowChange(data, itemType, prevItemType, nextItemType)) {
                        view.setPaddings(data)
                        view.background = ContextCompat.getDrawable(parent.context, data.background)
                        if (data.expand && parent.allowExpand(view, data)) {
                            view.createExpandedBackground(parent, data)?.draw(c)
                        }
                        continue@loop
                    } else {
                        defaultDecoration?.let { view.background = ContextCompat.getDrawable(parent.context, it) }
                    }
                }
            }
        }
    }

    private fun RecyclerView.allowExpand(child: View, data: BackgroundItemsDecorationData<ITEM_TYPE>): Boolean {
        if (data.position == ItemPosition.FIRST) return true
        val position = getChildAdapterPosition(child)
        if (position == RecyclerView.NO_POSITION) return false
        val adapterItemCount = adapter?.itemCount ?: Int.MIN_VALUE
        return if (childCount < adapterItemCount) {
            adapter?.getViewModelType(position + 1) == BottomStub::class.java
        } else {
            getChildViewHolder(getChildAt(position + 1)).itemViewType == stubItemType
        }
    }

    private fun View.createExpandedBackground(
        parent: RecyclerView,
        data: BackgroundItemsDecorationData<*>
    ): Drawable? =
        ContextCompat.getDrawable(parent.context, data.background)?.apply {
            setBounds(
                left,
                top,
                right,
                parent.measuredHeight
            )
        }

    private fun View.setPaddings(data: BackgroundItemsDecorationData<*>) {
        data.padding?.let {
            if (data.position == ItemPosition.FIRST) {
                setPaddingTopAttrRes(it)
            } else if (data.position == ItemPosition.LAST) {
                setPaddingBottomAttrRes(it)
            }
        }
    }

    private companion object {
        /** Тип ячейки с заглушкой для отображения пустого элемента или индикатора загрузки. */
        val stubItemType = R.layout.business_item_bottom_stub
    }
}
