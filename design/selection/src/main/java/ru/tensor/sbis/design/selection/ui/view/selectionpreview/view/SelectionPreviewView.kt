package ru.tensor.sbis.design.selection.ui.view.selectionpreview.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import ru.tensor.sbis.design.collection_view.VerticalCollectionView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils.StringProvider
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils.prepareSelectionPreviewList
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils.prepareSuggestionList
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

/**
 * [View], позволяющий отображать список превью выбранных, либо предлагаемых для выбора элементов.
 * Отображаются первые несколько элементов заданного списка, в зависимости от значения
 * [SelectionPreviewListData.maxDisplayedEntries] или [SelectionSuggestionListData.maxDisplayedEntries]
 *
 * - [Стандарт](http://axure.tensor.ru/MobileAPP/#g=1&p=выбор_региона&c=1)
 * @author us.bessonov
 */
class SelectionPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.selectionPreviewViewTheme
) : VerticalCollectionView<SelectionPreviewViewHolder, SelectionPreviewViewAdapter>(
    ContextThemeWrapper(context, context.getDataFromAttrOrNull(defStyleAttr) ?: R.style.SelectionPreviewView),
    attrs,
    defStyleAttr
) {

    private val stringProvider = StringProvider(context)

    /**
     * Отображает список превью выбранных элементов ([SelectionPreviewListData]), либо список предлагаемых для выбора
     * элементов ([SelectionSuggestionListData])
     */
    fun showData(data: SelectionPreviewData) {
        val items = when (data) {
            is SelectionPreviewListData<*> -> prepareSelectionPreviewList(data, stringProvider)
            is SelectionSuggestionListData<*> -> prepareSuggestionList(data, stringProvider)
        }
        adapter.reload(items)
    }

    override fun createAdapter(context: Context, attrs: AttributeSet?) = SelectionPreviewViewAdapter(this)

    override fun addChildInLayout(view: View, position: Int) {
        addViewInLayout(view, position, view.layoutParams, true)
    }
}
