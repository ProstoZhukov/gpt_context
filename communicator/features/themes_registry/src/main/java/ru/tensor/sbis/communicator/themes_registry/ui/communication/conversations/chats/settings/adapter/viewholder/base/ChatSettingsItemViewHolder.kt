package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.base

import android.view.View
import androidx.annotation.CallSuper
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem

/**
 * Базовый класс вьюхолдера элемента экрана настроек чата.
 *
 * @author dv.baranov
 */
internal abstract class ChatSettingsItemViewHolder<ITEM : ChatSettingsItem>(
    view: View,
) : AbstractViewHolder<ChatSettingsItem>(view) {

    private var data: ITEM? = null

    /** @SelfDocumented */
    internal val item: ITEM
        get() = data!!

    /** @SelfDocumented */
    fun setData(data: ITEM?) {
        this.data = data
        bind()
    }

    @CallSuper
    override fun recycle() {
        super.recycle()
        data = null
    }

    /** @SelfDocumented */
    protected abstract fun bind()
}
