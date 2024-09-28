package ru.tensor.sbis.communicator.sbis_conversation.ui.list

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.SbisListView

/**
 * Реализация [SbisListView] для реестра сообщений.
 *
 * @author vv.chekurda
 */
internal class MessageListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SbisListView(context, attrs, defStyleAttr) {

    /**
     * Установить [adapter] списка.
     */
    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        // Зануление адаптера сбрасывает кэш холдеров.
        if (adapter != null) super.setAdapter(adapter)
    }
}