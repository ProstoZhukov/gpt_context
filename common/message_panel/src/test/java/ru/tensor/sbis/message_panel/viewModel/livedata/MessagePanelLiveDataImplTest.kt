package ru.tensor.sbis.message_panel.viewModel.livedata

import io.mockk.mockk
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junit.framework.Assert.assertFalse
import org.junit.Test
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * @author vv.chekurda
 * Создан 8/15/2019
 */
class MessagePanelLiveDataImplTest {

    private val vm: MessagePanelViewModel<*, *, *> = mock()
    private val liveData: MessagePanelLiveData = initLiveData(vm)

    @Test
    fun `When recipients not empty, then requireRecipients return false`() {
        liveData.setRecipientsRequired(true)
        liveData.setRecipients(listOf(mockk()))

        assertFalse(liveData.requireRecipients)
    }

    /**
     * Тест отсутствия изменений клавиатуры при отмене цитаты/редакции/ответа на комментарий.
     */
    @Test
    fun `When need to clean, then do not change keyboard state`() {
        val keyboardObserver = liveData.keyboardState.test()
        whenever(vm.shouldHideKeyboardOnClear()).thenReturn(true)
        whenever(vm.recipientsManager).thenReturn(mock())

        liveData.onRecipientClearButtonClick()

        keyboardObserver.assertNoValues()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=7f64615c-1e16-41fa-a55c-6a051daeec48
     */
    @Test
    fun `When not need to clean, then clear recipient shouldn't cancel operation`() {
        val keyboardObserver = liveData.keyboardState.test()
        whenever(vm.shouldHideKeyboardOnClear()).thenReturn(false)
        whenever(vm.recipientsManager).thenReturn(mock())

        liveData.onRecipientClearButtonClick()

        keyboardObserver.assertNoValues()
    }
}