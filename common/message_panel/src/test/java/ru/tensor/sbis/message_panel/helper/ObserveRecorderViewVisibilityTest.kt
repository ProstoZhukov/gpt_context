package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.custom.combined.CombinedParameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.testing.dataListParamMapper
import ru.tensor.sbis.common.testing.stringParamMapper
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData


/**
 * Тестирование правил скрытия панели звукозаписи
 *
 * @author vv.chekurda
 * Создан 8/9/2019
 */
@RunWith(JUnitParamsRunner::class)
class ObserveRecorderViewVisibilityTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    private val messageTextSubject = PublishSubject.create<RxContainer<String>>()
    private val attachmentsSubject = PublishSubject.create<List<AttachmentRegisterModel>>()
    private val isEditingObservable = PublishSubject.create<Boolean>()
    private val hasRecordListener = PublishSubject.create<Boolean>()

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Before
    fun setUp() {
        whenever(liveData.messageText).thenReturn(messageTextSubject)
        whenever(liveData.attachments).thenReturn(attachmentsSubject)
        whenever(liveData.isEditing).thenReturn(isEditingObservable)
    }

    @Test
    @CombinedParameters("0,1", "1")
    fun `Recorder view visible if attachments and text is empty`(
        textCode: Int,
        attachmentsCode: Int
    ) = visibilityTest(textCode, attachmentsCode, false, true, true)

    @Test
    @CombinedParameters("2", "1,2")
    fun `Recorder view invisible if message text present`(
        textCode: Int,
        attachmentsCode: Int
    ) = visibilityTest(textCode, attachmentsCode, false,true, false)

    @Test
    @CombinedParameters("0,1,2", "2")
    fun `Recorder view invisible if attachments present`(
        textCode: Int,
        attachmentsCode: Int
    ) = visibilityTest(textCode, attachmentsCode, false,true, false)

    @Test
    @CombinedParameters("0,1,2", "1")
    fun `Recorder view invisible if attachments present and text is empty at editing message`(
        textCode: Int,
        attachmentsCode: Int
    ) = visibilityTest(textCode, attachmentsCode, true,true, false)

    @Test
    fun `Recorder view invisible if record listener was not set`() =
        visibilityTest(0, 1, false, false, false)

    @Test
    fun `Recorder view visible if record listener was set`() =
        visibilityTest(0, 1, false, true, true)

    @Test
    fun `Recorder view invisible if it's quoting`() =
        visibilityTest(0, 1, false, false, false)

    private fun visibilityTest(
        textCode: Int,
        attachmentsCode: Int,
        isEditing: Boolean,
        hasListeners: Boolean,
        visible: Boolean
    ) {
        val visibility = observeRecorderViewVisibility(liveData, hasRecordListener).test()

        messageTextSubject.onNext(RxContainer(stringParamMapper(textCode)))
        attachmentsSubject.onNext(dataListParamMapper(attachmentsCode, mock())!!)
        isEditingObservable.onNext(isEditing)
        hasRecordListener.onNext(hasListeners)

        visibility.assertValue(visible)
    }
}