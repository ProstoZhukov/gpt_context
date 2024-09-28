package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts

import android.content.Context
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewMode

/**
 * Обертка для StubView, которая позволяет скролить его внутри CoordinatorLayout.
 *
 * @author da.zhukov
 */
internal class AddNewContactsStubView(context: Context) : NestedScrollView(context) {

    private val stubView = StubView(context)

    init {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(stubView)
    }

    fun setCase(content: StubViewCase) {
        stubView.apply {
            setCase(content)
            setMode(StubViewMode.BLOCK)
        }
    }
}