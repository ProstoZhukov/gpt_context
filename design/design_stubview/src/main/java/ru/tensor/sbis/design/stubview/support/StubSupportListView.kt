package ru.tensor.sbis.design.stubview.support

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Реализация [AbstractListView] с [StubView] в качестве вью заглушки
 *
 * @author sa.nikitin
 */
open class StubSupportListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AbstractListView<StubView, StubViewContent>(context, attrs, defStyleAttr) {

    override fun createInformationView(container: ViewGroup): StubView = StubView(context)

    override fun applyInformationViewData(informationView: StubView, content: StubViewContent?) {
        if (content == null) {
            informationView.visibility = View.GONE
        } else {
            informationView.setContent(content)
        }
    }
}