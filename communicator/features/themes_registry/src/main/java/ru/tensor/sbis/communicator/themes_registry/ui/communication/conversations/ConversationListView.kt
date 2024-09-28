package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.Stubs
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.design.list_utils.AbstractListView
import ru.tensor.sbis.design.text_span.SimpleInformationView
import ru.tensor.sbis.design.stubview.StubView
import ru.tensor.sbis.design.stubview.StubViewContent

/**
 * Адаптация [AbstractListView] для работы списка обсуждений с заглушками типа [StubView].
 * [AbstractListView] параметризован контентом типа [SimpleInformationView.Content] для интеграции
 * в существующую инфраструктуру.
 *
 * @author ma.kolpakov
 * @since 6/2/2019
 */
internal class ConversationListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractListView<StubView, StubViewContent>(context, attrs, defStyleAttr) {

    var shouldResetTouchEvents = false

    var reloadCurrentPage: ((View) -> Unit)? = null

    private lateinit var stubView: StubView

    private var stub: Stubs? = null
    var onStubClick: ((View) -> Unit) = {}

    private val stubVisibilityHandler by lazy { Handler() }

    init {
        recyclerView.id = R.id.themes_registry_conversation_list_recycler_id
    }

    override fun createInformationView(container: ViewGroup): StubView = StubView(container.context).also { stubView = it }

    // игнорируем этот метод, поскольку заглушки контролируются контроллером
    override fun applyInformationViewData(informationView: StubView, content: StubViewContent?) = Unit

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stubVisibilityHandler.removeCallbacksAndMessages(null)
    }

    override fun updateViewState() {
        val prevStubVisibility = stubView.visibility
        super.updateViewState()
        val newStubVisibility = stubView.visibility
        // костыльный способ добавить задержку на скрытие заглушки для того чтобы она не моргала при показе снова
        addDelayForStubViewHiding(newStubVisibility, prevStubVisibility)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (shouldResetTouchEvents) {
            shouldResetTouchEvents = false
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * Показать заглушку
     */
    fun setStub(stub: Stubs, actions: Map<Int, () -> Unit>) {
        this.stub = stub
        val stubContent = stub.toStubCaseContent(actions)
        stubView.setContent(stubContent)
    }

    private fun addDelayForStubViewHiding(newStubVisibility: Int, prevStubVisibility: Int) {
        if (newStubVisibility != prevStubVisibility && newStubVisibility != View.VISIBLE) {
            stubView.visibility = prevStubVisibility
            stubVisibilityHandler.removeCallbacksAndMessages(null)
            stubView.let {
                stubVisibilityHandler.postDelayed({ it.visibility = newStubVisibility }, 300)
            }
        }
        if (newStubVisibility == View.VISIBLE) {
            stubVisibilityHandler.removeCallbacksAndMessages(null)
        }
    }
}