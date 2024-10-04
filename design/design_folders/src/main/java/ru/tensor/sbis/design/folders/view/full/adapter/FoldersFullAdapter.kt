package ru.tensor.sbis.design.folders.view.full.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.data.model.MoreButton
import ru.tensor.sbis.design.folders.databinding.DesignFoldersItemViewFullBinding
import ru.tensor.sbis.design.folders.databinding.DesignFoldersMoreItemBinding
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewAditionalCommandBinding
import ru.tensor.sbis.design.folders.view.common.FolderCompactDiffCallback
import ru.tensor.sbis.design.folders.view.full.adapter.holders.AdditionalCommandHolder
import ru.tensor.sbis.design.folders.view.full.adapter.holders.FolderFullHolder
import ru.tensor.sbis.design.folders.view.full.adapter.holders.MoreButtonHolder
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.SwipeableViewBinderHelper

/**
 *  Адаптер развёрнутого списка папок
 *
 * @author ma.kolpakov
 */
internal class FoldersFullAdapter(
    private val resourceProvider: FolderHolderResourceProvider,
    private val swipeHelper: SwipeableViewBinderHelper<String>
) : ListAdapter<FolderItem, ViewHolder>(FolderCompactDiffCallback()) {

    private companion object {
        const val FOLDER_TYPE = 0
        const val COMMAND_TYPE = 1
        const val MORE_BUTTON_TYPE = 2
    }

    private var moreButtonClickListener: (() -> Unit)? = null
    private var folderActionHandler: FolderActionHandler? = null
    private var selectedFolderId: String? = null

    /**
     * Установка слушателя клика по кнопке "ещё"
     *
     * @param listener слушатель клика по кнопке "ещё"
     */
    fun onMoreClicked(listener: (() -> Unit)?) {
        moreButtonClickListener = listener
    }

    /**
     * Установка слушателя действия папки
     *
     * @param handler реализация слушателя действия папки
     */
    fun setActionHandler(handler: FolderActionHandler?) {
        folderActionHandler = handler
    }

    internal fun setSelectedFolder(id: String?) {
        selectedFolderId = id
    }

    /** @SelfDocumented */
    fun closeSwipeMenu() {
        swipeHelper.closeAllOpenMenus()
    }

    override fun getItemViewType(pos: Int): Int =
        when (val item = getItem(pos)) {
            is Folder -> FOLDER_TYPE
            is AdditionalCommand -> COMMAND_TYPE
            MoreButton -> MORE_BUTTON_TYPE
            else -> error("Unexpected folder view item " + item::class.java)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            FOLDER_TYPE -> {
                val binding = DesignFoldersItemViewFullBinding.inflate(inflater, parent, false)
                FolderFullHolder(binding, folderActionHandler, resourceProvider)
            }
            COMMAND_TYPE -> {
                val binding = DesignFoldersViewAditionalCommandBinding.inflate(inflater, parent, false)
                AdditionalCommandHolder(binding, folderActionHandler, resourceProvider)
            }
            else -> {
                val binding = DesignFoldersMoreItemBinding.inflate(inflater, parent, false)
                MoreButtonHolder(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.itemView.setOnClickListener(null)
        when (holder) {
            is FolderFullHolder -> {
                val item = getItem(pos) as Folder
                holder.bind(item, selectedFolderId)
                swipeHelper.bind(holder.itemView as SwipeableLayout, item.id)
            }

            is AdditionalCommandHolder -> {
                val item = getItem(pos) as AdditionalCommand
                holder.bind(item)
            }

            is MoreButtonHolder -> holder.itemView.setOnClickListener { moreButtonClickListener?.invoke() }
        }
    }
}
