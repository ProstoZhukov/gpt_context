package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.text_span.SimpleInformationView

/**
 * Адаптация [AbstractListView] для работы списка участников с заглушками типа [StubView].
 * [AbstractListView] параметризован контентом типа [SimpleInformationView.Content] для интеграции
 * в существующую инфраструктуру.
 *
 * @author vv.chekurda
 */
internal class ParticipantsListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractListView<StubView, StubViewCase>(context, attrs, defStyleAttr) {

    override fun createInformationView(container: ViewGroup): StubView =
        StubView(container.context)

    override fun applyInformationViewData(informationView: StubView, content: StubViewCase?) {
        content?.let(informationView::setCase)
    }
}