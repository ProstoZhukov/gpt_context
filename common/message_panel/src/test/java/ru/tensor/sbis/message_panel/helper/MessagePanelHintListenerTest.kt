package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import ru.tensor.sbis.message_panel.R as RecorderR

/**
 * Тестирование отображение подсказки об аудиозаписи в панели ввода
 *
 * @author vv.chekurda
 * Создан 8/5/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelHintListenerTest {

    private val newDialogModeEnabled = BehaviorSubject.createDefault(false)

    @Mock
    private lateinit var liveData: MessagePanelLiveData
    @Mock
    private lateinit var disposer: DisposableContainer

    private lateinit var listener: MessagePanelHintListener

    @Before
    fun setUp() {
        whenever(liveData.newDialogModeEnabled).thenReturn(newDialogModeEnabled)

        listener = MessagePanelHintListener(liveData, disposer)
    }

    @Test
    fun `Show record hint`() {
        listener.onShowHint(true)

        verify(liveData).setHint(RecorderR.string.recorder_hint_message)
        verify(liveData).forceHideRecipientsPanel(true)
        verify(liveData).forceChangeAttachmentsButtonVisibility(false)
    }

    @Test
    fun `Hide record hint and show default`() {
        listener.onShowHint(false)

        verify(liveData).resetHint()
        verify(liveData).forceHideRecipientsPanel(false)
        verify(liveData).forceChangeAttachmentsButtonVisibility(true)
    }
}