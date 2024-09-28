package ru.tensor.sbis.message_panel.viewModel.livedata

import org.mockito.kotlin.mock
import io.reactivex.observers.TestObserver
import org.junit.Test

/**
 * @author vv.chekurda
 * Создан 8/16/2019
 */
class MessagePanelQuoteDataTest {

    private val data = MessagePanelQuoteDataImpl()

    @Test
    fun `Quote panel become visible on text changed`() {
        val title: CharSequence = mock()
        val titleObserver = TestObserver<CharSequence>()
        val text: CharSequence = mock()
        val textObserver = TestObserver<CharSequence>()
        val visibilityObserver = TestObserver<Boolean>()

        data.originalMessageTitle.subscribe(titleObserver)
        data.originalMessageText.subscribe(textObserver)
        data.quotePanelVisible.subscribe(visibilityObserver)

        data.setQuoteText(title, text)

        titleObserver.assertValue(title)
        textObserver.assertValue(text)
        visibilityObserver.assertValues(false, true)
    }
}