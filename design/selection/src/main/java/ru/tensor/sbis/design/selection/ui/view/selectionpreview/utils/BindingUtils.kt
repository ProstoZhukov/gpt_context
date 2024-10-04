/**
 * Инструменты для установки данных через биндинг в компонент SelectionPreviewView
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.selection.ui.view.selectionpreview.utils

import android.view.View
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.view.SelectionPreviewView

@BindingAdapter("selectionPreviewData")
fun showSelectionPreviewData(view: SelectionPreviewView, data: SelectionPreviewData) {
    view.showData(data)
}

@BindingAdapter("onClick")
fun View.setActionOnClick(action: (() -> Unit)?) {
    val listener = action?.let { onClick -> View.OnClickListener { onClick() } }
    setOnClickListener(listener)
    isClickable = listener != null
}