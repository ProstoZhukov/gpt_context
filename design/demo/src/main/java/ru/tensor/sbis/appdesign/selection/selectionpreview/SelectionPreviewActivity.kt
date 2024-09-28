package ru.tensor.sbis.appdesign.selection.selectionpreview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.appdesign.databinding.ActivitySelectionPreviewBinding
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.listener.SelectionPreviewActionListenerAdapter
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.DefaultSelectionPreviewItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.DefaultSelectionSuggestionItem
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionPreviewListData
import ru.tensor.sbis.design.selection.ui.view.selectionpreview.model.SelectionSuggestionListData

/**
 * @author us.bessonov
 */
class SelectionPreviewActivity : AppCompatActivity() {

    private val previewActionListener = object : SelectionPreviewActionListenerAdapter<DefaultSelectionPreviewItem>() {
        override fun onItemClick(item: DefaultSelectionPreviewItem) {
            showToast("Clicked preview item '${item.title}'")
        }

        override fun onRemoveClick(item: DefaultSelectionPreviewItem) {
            showToast("Removed preview item '${item.title}'")
            demoPreviewItems.remove(item)
            if (demoPreviewItems.isEmpty()) {
                demoPreviewItems.add(demoSelectAllItem)
            }
            showPreviewData()
        }
    }

    private val suggestionActionListener = object : SelectionPreviewActionListenerAdapter<DefaultSelectionSuggestionItem>() {
        override fun onItemClick(item: DefaultSelectionSuggestionItem) {
            showToast("Clicked suggestion item '${item.title}'")
        }

        override fun onShowAllClick() {
            showToast("Clicked to show all suggestions")
        }
    }

    private lateinit var viewBinding: ActivitySelectionPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectionPreviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        showPreviewData()
        showSuggestionData()
    }

    private fun showPreviewData() {
        viewBinding.preview.showData(SelectionPreviewListData(demoPreviewItems, previewActionListener))
    }

    private fun showSuggestionData() {
        viewBinding.suggestions.showData(
            SelectionSuggestionListData(
                R.string.categories_title,
                demoSuggestionItems,
                suggestionActionListener
            )
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}