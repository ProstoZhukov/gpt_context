package ru.tensor.sbis.folderspanel

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.modalwindows.dialogalert.BaseAlertDialogFragment
import java.util.*
import ru.tensor.sbis.modalwindows.R as RModalWindows

/**
 * Диалоговое окно со списком папок, использующееся при перемещении папки в другую
 */
class FolderPickDialogFragment : BaseAlertDialogFragment() {

    override fun hasListener(): Boolean {
        return folderPickedListener != null
    }

    private lateinit var folders: List<FolderViewModel>
    private val folderPickedListener: FolderPickedListener?
        get() {
            if (activity is FolderPickedListener) return activity as FolderPickedListener?
            if (parentFragment is FolderPickedListener) return parentFragment as FolderPickedListener?
            throw ClassCastException("$activity or $parentFragment must implement " + FolderPickedListener::class.java.simpleName)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        folders = arguments?.getParcelableArrayList(FOLDERS)!!
    }

    override fun addContent(container: View) {
        val listContainer = container.findViewById<FrameLayout>(RModalWindows.id.modalwindows_alert_content_container)
        requireActivity().layoutInflater.let {
            it.inflate(RModalWindows.layout.modalwindows_dialog_alert_recycler_view, listContainer, true)
            val recyclerView = listContainer.findViewById<RecyclerView>(RModalWindows.id.modalwindows_recycler_view)
            recyclerView.isNestedScrollingEnabled =
                container.context.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            recyclerView.setHasFixedSize(true)
            this.showFolders(recyclerView)
            this.updateTopMargin(requireActivity(), hasTitle, recyclerView, MARGIN_TOP)
        }
    }

    private fun showFolders(recyclerView: RecyclerView) {
        val adapter = FolderPickListAdapter()
        folders.forEach {
            it.onClick = { folderViewModel ->
                folderPickedListener?.onFolderPicked(folderViewModel.uuid)
                dismiss()
            }
        }
        adapter.reload(folders)
        recyclerView.adapter = adapter
    }

    @Suppress("SameParameterValue")
    private fun updateTopMargin(
        context: Context,
        hasTitle: Boolean,
        target: View,
        value: Float
    ) {
        if (hasTitle) {
            ((target.parent as? ViewGroup)?.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    value,
                    context.resources.displayMetrics
                ).toInt()
        }
    }

    override fun onPositiveButtonClick() {
        dismiss()
    }

    override fun onNegativeButtonClick() {
        dismiss()
    }

    /**
     * Интерфейс для оповещения хост-фрагмента о совершении выбора пользователем
     */
    interface FolderPickedListener {
        fun onFolderPicked(folder: String)
    }

    companion object {
        private val FOLDERS = FolderPickDialogFragment::class.java.canonicalName + ".FOLDERS"
        private const val MARGIN_TOP = 16F

        /**
         * Создание экземпляра диалогового окна выбора папки для перемещения
         * @param folders список папок (ViewModel-ей)
         * @return экземпляр диалогового окна
         */
        @JvmStatic
        fun newInstance(folders: List<FolderViewModel>): FolderPickDialogFragment {
            val fragment = FolderPickDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(FOLDERS, ArrayList(folders))
            fragment.arguments = args
            return fragment
        }
    }
}

