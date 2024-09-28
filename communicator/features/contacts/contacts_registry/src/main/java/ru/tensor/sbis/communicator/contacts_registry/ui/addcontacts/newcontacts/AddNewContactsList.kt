package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.StubViewCase

/**
 * Реализация ContactsListView для блочного отображения заглушки
 *
 * @author da.zhukov
 */
internal class AddNewContactsList @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AbstractListView<AddNewContactsStubView, StubViewCase>(context, attributeSet, defStyleAttr) {

    private var content: StubViewCase? = null

    /**@SelfDocumented */
    override fun createInformationView(container: ViewGroup) = AddNewContactsStubView(container.context)

    /**@SelfDocumented */
    override fun applyInformationViewData(informationView: AddNewContactsStubView, content: StubViewCase?) {
        this.content = content
        informationView.visibility = if (content != null) View.VISIBLE else View.GONE
        content?.let(informationView::setCase)
    }
}