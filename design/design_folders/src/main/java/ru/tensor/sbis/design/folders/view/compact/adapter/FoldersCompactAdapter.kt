package ru.tensor.sbis.design.folders.view.compact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderButton
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.databinding.DesignFoldersItemViewCompactBinding
import ru.tensor.sbis.design.folders.databinding.DesignFoldersViewIconBinding
import ru.tensor.sbis.design.folders.view.common.FolderCompactDiffCallback
import ru.tensor.sbis.design.folders.view.compact.adapter.holders.FolderCompactHolder
import ru.tensor.sbis.design.folders.view.compact.adapter.holders.FolderIconHolder
import java.util.LinkedList

/**
 * Адаптер развёрнутого списка папок
 *
 * @author ma.kolpakov
 */
internal class FoldersCompactAdapter : ListAdapter<FolderItem, ViewHolder>(FolderCompactDiffCallback()) {

    private companion object {
        const val FOLDER_TYPE = 0
        const val FOLDER_ICON_TYPE = 1
    }

    private var hasIcon = false

    private var folderIconClickListener: (() -> Unit)? = null
    private var folderClickListener: ((id: String) -> Unit)? = null
    private val itemViewPool = LinkedList<DesignFoldersItemViewCompactBinding>()

    /**
     * Признак необходимости заготовить несколько ячеек папок для первого синхронного отображения.
     */
    var prepareFolderItems: Boolean = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (prepareFolderItems) {
            prepareItemsViewPool(recyclerView)
        }
    }

    /**
     * Установка видимости иконки папки
     *
     * @param isVisible видима ли иконка папки
     */
    fun setFolderIconVisible(isVisible: Boolean) {
        hasIcon = isVisible
    }

    /**
     * Установка слушателя клика по элементу списка
     *
     * @param listener слушатель клика. В лямбду передаётся флаг "является ли иконкой папки" и id кликнутой папки
     */
    fun onFolderIconClick(listener: () -> Unit) {
        folderIconClickListener = listener
    }

    fun onFolderClick(listener: (id: String) -> Unit) {
        folderClickListener = listener
    }

    override fun getItemViewType(pos: Int): Int =
        if (getItem(pos) is FolderButton) {
            FOLDER_ICON_TYPE
        } else {
            FOLDER_TYPE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == FOLDER_TYPE) {
            val binding = getItemViewBinding(parent)
            FolderCompactHolder(binding)
        } else {
            val binding = createIconItemViewBinding(parent)
            FolderIconHolder(binding.root)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = getItem(pos)

        if (holder is FolderCompactHolder) {
            val isFirst = hasIcon && pos == 1 || pos == 0
            (item as? Folder)?.isFirst = isFirst
            val isLast = pos == itemCount - 1

            holder.bind(getItem(pos) as Folder, isFirst, isLast, hasIcon)

            holder.onClickListener = {
                when (item) {
                    is Folder -> folderClickListener?.invoke(item.id)
                    else -> error("Unexpected folder view item " + item::class.java)
                }
            }
        } else {
            holder.itemView.setOnClickListener {
                when (item) {
                    FolderButton -> folderIconClickListener?.invoke()
                    else -> error("Unexpected folder view item " + item::class.java)
                }
            }
        }
    }

    private fun prepareItemsViewPool(parent: ViewGroup) {
        repeat(DEFAULT_PREPARE_ITEM_COUNT) {
            itemViewPool.add(createItemViewBinding(parent))
        }
    }

    private fun createItemViewBinding(
        parent: ViewGroup
    ): DesignFoldersItemViewCompactBinding {
        val inflater = LayoutInflater.from(parent.context)
        return DesignFoldersItemViewCompactBinding.inflate(inflater, parent, false)
    }

    private fun createIconItemViewBinding(
        parent: ViewGroup
    ): DesignFoldersViewIconBinding {
        val inflater = LayoutInflater.from(parent.context)
        return DesignFoldersViewIconBinding.inflate(inflater, parent, false)
    }

    private fun getItemViewBinding(parent: ViewGroup): DesignFoldersItemViewCompactBinding =
        itemViewPool.poll() ?: createItemViewBinding(parent)

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        itemViewPool.clear()
    }
}

private const val DEFAULT_PREPARE_ITEM_COUNT = 4