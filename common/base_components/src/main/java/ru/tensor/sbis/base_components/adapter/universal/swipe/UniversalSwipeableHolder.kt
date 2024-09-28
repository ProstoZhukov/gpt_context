package ru.tensor.sbis.base_components.adapter.universal.swipe

import android.util.SparseArray
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder
import ru.tensor.sbis.swipeablelayout.*
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.ItemInListChecker
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper

/**
 * Базовый вью холдер ячейки, поддерживающей свайп
 *
 * @author am.boldinov
 */
abstract class UniversalSwipeableHolder<DM : UniversalBindingItem> : UniversalViewHolder<DM> {

    internal interface SwipeableListener : ItemInListChecker {

        fun onSwipeDismissed(uuid: String, bySwipe: Boolean)
    }

    /**
     * Базовая реализация действия для любой опции внутри меню, открывающегося по свайпу
     */
    protected abstract inner class ActionOptionRunnable : Runnable {
        @CallSuper
        override fun run() {
            SwipeHelper.closeAll(animated = true)
        }
    }

    protected val removeOptionRunnable = Runnable {
        swipeableLayout?.itemUuid?.let { uuid ->
            listener?.onSwipeDismissed(uuid, false)
        }
    }

    private val swipeableLayout = itemView as? SwipeableLayout
    private var listener: SwipeableListener? = null

    constructor(binding: ViewDataBinding) : super(binding)

    @Suppress("unused")
    constructor(binding: ViewDataBinding, variables: SparseArray<Any>?) : super(binding, variables)

    constructor(binding: ViewDataBinding, clickHandlerVariableId: Int, clickHandler: Any?) : super(
        binding,
        clickHandlerVariableId,
        clickHandler
    )

    init {
        swipeableLayout?.apply {
            setDismissMessage(resources.getString(ru.tensor.sbis.swipeable_layout.R.string.swipeable_layout_cancel_deletion_message))
            addSwipeEventListener<Dismissed> {
                it.uuid?.let { uuid ->
                    listener?.onSwipeDismissed(uuid, true)
                }
            }
            isDragLocked = true
        }
    }

    internal fun attachListener(listener: SwipeableListener) {
        this.listener = listener
    }

    override fun executeBind(dataModel: DM) {
        super.executeBind(dataModel)
        listener?.let { listener ->
            swipeableLayout?.apply {
                itemUuid = dataModel.itemTypeId
                isDragLocked = false
                setItemInListChecker(listener)
                setMenu(generateSwipeMenu(dataModel))
                itemDismissType = if ((dataModel as UniversalSwipeableVM).canRemove()) {
                    SwipeItemDismissType.CANCELLABLE
                } else {
                    SwipeItemDismissType.NONE
                }
            }
        }
    }

    /**
     * Закрывает свайп если он был открыт
     */
    fun closeSwipeIfNeed() {
        if (isSwipeOpened()) {
            swipeableLayout?.close(false)
        }
    }

    /**
     * Проверяет состояние свайпа
     */
    fun isSwipeOpened(): Boolean {
        return swipeableLayout?.let {
            it.lastEvent is MenuOpened
        } ?: false
    }

    private fun generateSwipeMenu(dataModel: DM): List<SwipeMenuItem> {
        val builder = SwipeMenuItemList(3)
        generateSwipeMenu(dataModel, builder)
        return builder
    }

    protected abstract fun generateSwipeMenu(dataModel: DM, builder: SwipeMenuItemBuilder)
}