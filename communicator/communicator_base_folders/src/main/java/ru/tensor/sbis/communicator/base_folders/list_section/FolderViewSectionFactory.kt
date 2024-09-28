package ru.tensor.sbis.communicator.base_folders.list_section

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.view.FirstItemFactory
import ru.tensor.sbis.crud3.view.Refreshable
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.ItemOptions
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import ru.tensor.sbis.list.view.section.Options
import ru.tensor.sbis.list.view.item.Options as ItemOption

/** @SelfDocumented */
class CommunicatorBaseListFolderViewSectionFactory(
    private val listViewFoldersHolderHelper: CommunicatorBaseListViewFoldersHolderHelper,
) : FirstItemFactory<CommunicatorBaseFolderViewSectionItem> {

    override fun create(): ItemWithSection<CommunicatorBaseFolderViewSectionItem> {
        return ItemWithSection(
            sectionOption = Options(
                hasDividers = false,
                hasTopMargin = false
            ),
            item = CommunicatorBaseFolderViewSectionItem(
                listViewFoldersHolderHelper,
                ItemOption(
                    customBackground = true,
                    customSidePadding = true
                )
            )
        )
    }
}

/** @SelfDocumented */
class CommunicatorBaseListViewFoldersHolderHelper(
    private val foldersViewHolderHelper: FoldersViewHolderHelper,
    private val options: CommunicatorBaseFolderOptions = CommunicatorBaseFolderOptions()
) : ViewHolderHelper<Unit, RecyclerView.ViewHolder> {

    private var foldersView: FoldersView? = null

    override fun createViewHolder(parentView: ViewGroup): RecyclerView.ViewHolder {
        foldersView = FoldersView(parentView.context).apply {
            isExpandable = options.isExpandable
            isShownCurrentFolder = options.isShownCurrentFolder
            isVisible = false
        }
        foldersViewHolderHelper.attachFoldersView(foldersView!!)
        return CommunicatorBaseListFoldersHolder(foldersView!!)
    }

    override fun bindToViewHolder(data: Unit, viewHolder: RecyclerView.ViewHolder) {
        val itemView = viewHolder.itemView as FoldersView
        if (itemView != foldersView) {
            foldersView?.let { foldersViewHolderHelper.detachFoldersView(it) }
            foldersViewHolderHelper.attachFoldersView(itemView)
            foldersView = itemView
        }
    }
}

class CommunicatorBaseFolderViewSectionItem(
    listViewFoldersHolderHelper: CommunicatorBaseListViewFoldersHolderHelper,
    options: ItemOptions = ru.tensor.sbis.list.view.item.Options()
) : Refreshable, Item<Unit, RecyclerView.ViewHolder>(
    data = Unit,
    viewHolderHelper = listViewFoldersHolderHelper,
    options = options
)

internal class CommunicatorBaseListFoldersHolder(view: View) : AbstractViewHolder<Unit>(view)