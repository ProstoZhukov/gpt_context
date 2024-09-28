package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.list_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Вью списка контактов внутри компании
 *
 * @author da.zhukov
 */
internal class AddInternalEmployeesListView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractListView<StubView, StubViewContent>(context, attributeSet, defStyleAttr) {

    private var content: StubViewContent? = null

    /**@SelfDocumented */
    override fun createInformationView(container: ViewGroup) = StubView(container.context)

    /**@SelfDocumented */
    override fun applyInformationViewData(informationView: StubView, content: StubViewContent?) {
        this.content = content
        informationView.visibility = if (content != null) View.VISIBLE else View.GONE
        content?.let(informationView::setContent)
    }
}