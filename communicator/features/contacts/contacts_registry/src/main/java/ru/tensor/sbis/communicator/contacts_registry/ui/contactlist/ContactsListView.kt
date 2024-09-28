package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubs
import ru.tensor.sbis.communicator.contacts_registry.R
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Вью списка контактов для реестра
 *
 * @author vv.chekurda
 */
internal open class ContactsListView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractListView<StubView, StubViewContent>(context, attributeSet, defStyleAttr) {

    var shouldResetTouchEvents = false

    private lateinit var stubView: StubView

    private var stub: ContactsStubs? = null

    init {
        recyclerView.id = R.id.communicator_contact_list_recycler_id
    }

    /**@SelfDocumented */
    override fun createInformationView(container: ViewGroup) = StubView(container.context).also { stubView = it }

    /**@SelfDocumented */
    override fun applyInformationViewData(informationView: StubView, content: StubViewContent?) = Unit

    /**
     * Показать заглушку
     */
    fun setStub(stub: ContactsStubs, actions: Map<Int, () -> Unit>) {
        this.stub = stub
        val stubContent = stub.toStubCaseContent(actions)
        stubView.setContent(stubContent)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (shouldResetTouchEvents) {
            shouldResetTouchEvents = false
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }
}