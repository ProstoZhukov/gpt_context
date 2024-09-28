package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.vmadapter.DiffCallBack
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.holder.ThemeParticipantFolderViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.adapter.holder.ThemeParticipantsViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import java.util.*

/**
 * Адаптер участников диалога/чата.
 *
 * @author rv.krohalev
 */
internal class ThemeParticipantsAdapter : BaseTwoWayPaginationAdapter<ThemeParticipantListItem>() {

    init {
        mShowOlderLoadingProgress = false
        mWithBottomEmptyHolder = false
    }

    /** @SelfDocumented */
    var onItemClick: (profileUuid: UUID) -> Unit = {}

    /** @SelfDocumented */
    var onItemPhotoClick: (profileUuid: UUID) -> Unit = {}

    /** @SelfDocumented */
    var onChangeAdminStatusClick: (chatParticipant: ThemeParticipantListItem.ThemeParticipant) -> Unit = { _ -> }

    /** @SelfDocumented */
    var onRemoveParticipantClick: (profileUuid: UUID, isByDismiss: Boolean) -> Unit = { _, _ -> }

    /** @SelfDocumented */
    var onStartConversationClick: (profileUuid: UUID) -> Unit = {}

    /** @SelfDocumented */
    var onStartVideoCallClick: (profileUuid: UUID) -> Unit = {}

    /** @SelfDocumented */
    var onFolderItemClick: (folder: ThemeParticipantListItem.ThemeParticipantFolder) -> Unit = {}

    private var isSwipePossible: Boolean = false
    private var needShowContactIcon: Boolean = false
    private var isSwipeEnabled: Boolean = false
    private var currentUserUuid: UUID? = null
    private val canvasStylesProvider = object : CanvasStylesProvider() {}

    /** @SelfDocumented */
    override fun getItemType(dataModel: ThemeParticipantListItem?): Int = when (dataModel) {
        is ThemeParticipantListItem.ThemeParticipant -> THEME_PARTICIPANT_HOLDER
        is ThemeParticipantListItem.ThemeParticipantFolder -> THEME_PARTICIPANT_FOLDER_HOLDER
        else -> HOLDER_EMPTY
    }

    /** @SelfDocumented */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<ThemeParticipantListItem> =
        when (viewType) {
            THEME_PARTICIPANT_HOLDER -> ThemeParticipantsViewHolder(
                parent,
                onItemClick,
                onItemPhotoClick,
                onChangeAdminStatusClick,
                onRemoveParticipantClick,
                isSwipePossible,
                canvasStylesProvider.textStyleProvider,
                onStartConversationClick,
                onStartVideoCallClick,
                needShowContactIcon,
            )
            THEME_PARTICIPANT_FOLDER_HOLDER -> ThemeParticipantFolderViewHolder(
                parent,
                onFolderItemClick,
            )
            else -> super.onCreateViewHolder(parent, viewType)
        }

    /** @SelfDocumented */
    override fun onBindViewHolder(holder: AbstractViewHolder<ThemeParticipantListItem>, index: Int) {
        super.onBindViewHolder(holder, index)
        if (holder.itemViewType == THEME_PARTICIPANT_HOLDER) {
            with(holder as ThemeParticipantsViewHolder) {
                if (!needShowContactIcon) {
                    showSeparator(index < mOffset + mContent.size - 1)
                }
                this.isSwipeEnabled = this@ThemeParticipantsAdapter.isSwipeEnabled
                this.currentUserUuid = this@ThemeParticipantsAdapter.currentUserUuid
                bind(content[index - mOffset])
            }
        } else if (holder.itemViewType == THEME_PARTICIPANT_FOLDER_HOLDER) {
            with(holder as ThemeParticipantFolderViewHolder) {
                bind(content[index])
            }
        }
    }

    /** Хинт для адаптера, возможен ли свайп в принципе. Если невозможен, ячейки не будут оборачиваться в свайплайут */
    fun setSwipePossible(value: Boolean) {
        isSwipePossible = value
    }

    /** Нужно ли показывать иконки начала переписки и начала видеозвонка */
    fun setNeedShowContactIcon(value: Boolean) {
        needShowContactIcon = value
    }

    /**
     * Сделать ли доступным свайп-меню для конкретного item'а списка
     * @param isEnable - true, если меню должно быть доступно
     */
    fun setSwipeEnabled(isEnable: Boolean) {
        if (isEnable) {
            val changed = isEnable != isSwipeEnabled
            if (changed) {
                isSwipeEnabled = isEnable
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Задать UUID текущего пользователя приложения
     * @param uuid - UUID пользователя
     */
    fun setCurrentUserUuid(uuid: UUID) {
        val changed = !UUIDUtils.equals(currentUserUuid, uuid)
        if (changed) {
            currentUserUuid = uuid
            notifyDataSetChanged()
        }
    }

    /** Обновление контента при помощи DiffUtil */
    override fun setContent(newContent: MutableList<ThemeParticipantListItem>?, notifyDataSetChanged: Boolean) {
        val diffCallback = DiffCallBack(
            mContent ?: emptyList(),
            newContent ?: emptyList(),
            checkAreItemsTheSame = { oldItem: Any, newItem: Any ->
                val old = oldItem as ThemeParticipantListItem
                val new = newItem as ThemeParticipantListItem
                if (old is ThemeParticipantListItem.ThemeParticipantFolder && new is ThemeParticipantListItem.ThemeParticipantFolder) {
                    return@DiffCallBack old.uuid == new.uuid
                } else if (old is ThemeParticipantListItem.ThemeParticipant && new is ThemeParticipantListItem.ThemeParticipant) {
                    return@DiffCallBack old.employeeProfile.uuid == new.employeeProfile.uuid
                } else {
                    return@DiffCallBack false
                }
            },
            checkAreContentsTheSame = { oldItem: Any, newItem: Any ->
                val old = oldItem as ThemeParticipantListItem
                val new = newItem as ThemeParticipantListItem
                return@DiffCallBack old == new
            },
        )

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        super.setContent(newContent, false)

        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(position, count, payload)
            }
        })
    }

    /** @SelfDocumented */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        canvasStylesProvider.isResourceCacheEnabled = true
    }

    /** @SelfDocumented */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        canvasStylesProvider.isResourceCacheEnabled = false
        canvasStylesProvider.clearReferences()
    }
}

private const val THEME_PARTICIPANT_HOLDER = 0
private const val THEME_PARTICIPANT_FOLDER_HOLDER = 1
