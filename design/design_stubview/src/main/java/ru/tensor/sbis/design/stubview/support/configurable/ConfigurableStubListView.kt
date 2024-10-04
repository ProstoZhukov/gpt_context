package ru.tensor.sbis.design.stubview.support.configurable

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.text_span.SimpleInformationView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.support.StubViewState

/**
 * Реализация [AbstractListView], поддерживающая
 * [StubConfiguration] из коробки.
 * Предназначена для отображения заглушек с персонажем
 *
 * @author sr.golovkin on 05.08.2020
 */
open class ConfigurableStubListView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
): AbstractListView<StubView, SimpleInformationView.Content>(context, attributeSet, defStyleAttr) {

    /**
     * Экземпляр [Правил][StubConfiguration] отображения заглушек для данной [AbstractListView].
     * Устанавливаются посредством использования [StubConfiguration.Builder]
     * @see [StubConfiguration.Builder]
     */
    var configuration : StubConfiguration? = null

    private var content: SimpleInformationView.Content? = null

    override fun createInformationView(container: ViewGroup): StubView {
        return StubView(context)
    }

    override fun applyInformationViewData(informationView: StubView, content: SimpleInformationView.Content?) {
        this.content = content
        val stubContent = content?.let {
            configuration?.getStubContentByInformationContent(it)
        }
        stubContent?.let { informationView.setContent(it) }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return StubViewState(super.onSaveInstanceState()).apply {
            setContent(content)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state ?: return
        with(state as StubViewState) {
            super.onRestoreInstanceState(superState)
            showInformationViewData(getContent(context))
        }
    }

    /**
     * Видна ли заглушка на экране
     * @return true - видна
     */
    fun isEmptyState() = !recyclerView.isVisible && this.isVisible
}