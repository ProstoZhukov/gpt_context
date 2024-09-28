package ru.tensor.sbis.communicator.sbis_conversation.ui.list

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Реализация [AbstractListView] для реестра сообщений на crud коллекции.
 *
 * @author vv.chekurda
 */
internal class MessageCrudListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractListView<StubView, StubViewContent>(
    context,
    attrs,
    defStyleAttr
) {

    /**
     * Установить [adapter] списка.
     */
    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        // Зануление адаптера сбрасывает кэш холдеров.
        if (adapter != null) super.setAdapter(adapter)
    }

    override fun createInformationView(container: ViewGroup) = StubView(container.context)

    override fun applyInformationViewData(informationView: StubView, content: StubViewContent?) {
        content?.let { informationView.setContent(it) }
    }
}