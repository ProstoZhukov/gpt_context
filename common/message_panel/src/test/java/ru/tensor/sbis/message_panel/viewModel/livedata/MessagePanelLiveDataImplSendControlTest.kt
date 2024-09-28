package ru.tensor.sbis.message_panel.viewModel.livedata

import io.mockk.mockk
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.BackpressureStrategy
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.custom.combined.CombinedParameters
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.EMPTY_RESULT_CODE
import ru.tensor.sbis.common.testing.NOT_EMPTY_RESULT_CODE
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.testing.dataListParamMapper
import ru.tensor.sbis.common.testing.stringParamMapper
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.message_panel.helper.SendButtonActivationByRecipientsTest
import ru.tensor.sbis.message_panel.helper.getSendButtonActivated
import ru.tensor.sbis.message_panel.helper.sendButtonActiveByRecipients
import ru.tensor.sbis.message_panel.model.CoreConversationInfo

/**
 * Тестирование условий активации кнопки отправки
 *
 * Состояния отправки и цитирования должны быть взаимоисключающими. Тесты с обеспечением изоляции:
 * - [forcedRestrictions]
 * - [externalRestrictions]
 * - [recipientRequirements]
 *
 * @author vv.chekurda
 * Создан 8/16/2019
 */
@RunWith(JUnitParamsRunner::class)
class MessagePanelLiveDataImplSendControlTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val observer = TestObserver<Boolean>()

    private val info: CoreConversationInfo = mock()
    private val isEnabledSubject = PublishSubject.create<Boolean>()
    private val isSendingSubject = PublishSubject.create<Boolean>()
    private val isQuotingSubject = PublishSubject.create<Boolean>()
    private lateinit var liveData: MessagePanelLiveData

    /**
     * Перевод в состояние, при котором [sendButtonActiveByRecipients] зависит только от наличия получателей.
     * Работоспособность [sendButtonActiveByRecipients] проверяется в [SendButtonActivationByRecipientsTest].
     * Наиболее узкое правило
     */
    @Before
    fun setUp() {
        whenever(info.isChat).thenReturn(true)
        info.mockHasPermission(false)
    }

    @Test
    fun `Send button inactive by default`() {
        liveData = initLiveData()
        liveData.observe()

        observer.assertValue(false)
    }

    @Test
    @CombinedParameters("true,false", "true,false", "0,1,2", "1,2", "1,2", "true,false", "true,false")
    fun `When message panel disabled, then send button inactive (not quoting state)`(
        isSending: Boolean,
        coreRestrictions: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        restrictedByInfo: Boolean
    ) = disabledMessagePanelTestTemplate(
        isSending, false, coreRestrictions, textCode, attachmentsCode, recipientsCode, inviteSupported, restrictedByInfo
    )

    @Test
    @CombinedParameters("true,false", "0,1,2", "1,2", "1,2", "true,false", "true,false")
    fun `Send button inactive if has forced restrictions in (not quoting state)`(
        isSending: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        restrictedByInfo: Boolean
    ) = forcedRestrictions(
        isSending, false, textCode, attachmentsCode, recipientsCode, inviteSupported, restrictedByInfo
    )

    @Test
    @CombinedParameters("true,false", "true,false", "0,1,2", "1,2", "1,2", "true,false", "true,false")
    fun `When message panel disabled, then send button inactive (not sending state)`(
        isQuoting: Boolean,
        coreRestrictions: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        restrictedByInfo: Boolean
    ) = disabledMessagePanelTestTemplate(
        false, isQuoting, coreRestrictions, textCode, attachmentsCode, recipientsCode, inviteSupported, restrictedByInfo
    )

    @Test
    @CombinedParameters("true,false", "0,1,2", "1,2", "1,2", "true,false", "true,false")
    fun `Send button inactive if has forced restrictions in (not sending state)`(
        isQuoting: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        restrictedByInfo: Boolean
    ) = forcedRestrictions(
        false, isQuoting, textCode, attachmentsCode, recipientsCode, inviteSupported, restrictedByInfo
    )

    @Test
    @CombinedParameters("true,false", "0,1,2", "1,2", "1,2", "true,false", "true,false")
    fun `Send button inactive if has external restrictions (not quoting state)`(
        isSending: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        hasForcedRestrictions: Boolean
    ) = externalRestrictions(
        isSending, false, textCode, attachmentsCode, recipientsCode, inviteSupported, hasForcedRestrictions
    )

    @Test
    @CombinedParameters("true,false", "0,1,2", "1,2", "1,2", "true,false", "true,false")
    fun `Send button inactive if has external restrictions (not sending state)`(
        isQuoting: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        hasForcedRestrictions: Boolean
    ) = externalRestrictions(
        false, isQuoting, textCode, attachmentsCode, recipientsCode, inviteSupported, hasForcedRestrictions
    )

    @Test
    @CombinedParameters("0,1,2", "1,2", "1,2", "true,false")
    fun `Send button inactive in sending state`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean
    ) {
        whenever(info.inviteSupported).thenReturn(inviteSupported)
        liveData = initLiveData(
            info = info,
            isSending = isSendingSubject.toFlowable(BackpressureStrategy.ERROR),
            isQuoting = isQuotingSubject.toFlowable(BackpressureStrategy.ERROR)
        )
        liveData.observe()
        isSendingSubject.onNext(true)
        // цитирование и редактирвоание взаимоисключают друг друга
        isQuotingSubject.onNext(false)

        liveData.setMessageText(stringParamMapper(textCode))
        liveData.setAttachments(dataListParamMapper(attachmentsCode, mock())!!)
        liveData.setRecipients(dataListParamMapper(recipientsCode, mockk())!!)

        observer.assertNever(true)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=57d04291-db90-4152-89d5-1fffe7e5233f
     */
    @Test
    @Ignore("https://online.sbis.ru/opendoc.html?guid=0510dad3-b3fe-43e5-9d9c-777cb7f2032a")
    @CombinedParameters("0,1,2", "1,2", "true,false", "true,false")
    fun `Send button inactive if recipients required but panel hasn't recipients  (not quoting state)`(
        textCode: Int,
        attachmentsCode: Int,
        inviteSupported: Boolean,
        isSending: Boolean
    ) = recipientRequirements(textCode, attachmentsCode, inviteSupported, isSending, false)

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=57d04291-db90-4152-89d5-1fffe7e5233f
     */
    @Test
    @Ignore("https://online.sbis.ru/opendoc.html?guid=0510dad3-b3fe-43e5-9d9c-777cb7f2032a")
    @CombinedParameters("0,1,2", "1,2", "true,false", "true,false")
    fun `Send button inactive if recipients required but panel hasn't recipients  (not sending state)`(
        textCode: Int,
        attachmentsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = recipientRequirements(textCode, attachmentsCode, inviteSupported, false, isQuoting)

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=7c5a4165-cd6f-4f4d-8426-4a9ce5f618b9
     */
    @Test
    @CombinedParameters("0,1,2", "1,2", "1,2", "true,false", "true")
    fun `Send button active if panel in quoting state`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = activationTest(textCode, attachmentsCode, recipientsCode, inviteSupported, isQuoting, true)

    @Test
    @CombinedParameters("2", "1,2", "1,2", "true,false", "true,false")
    fun `Send button active if panel has text`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = activationTest(textCode, attachmentsCode, recipientsCode, inviteSupported, isQuoting, true)

    @Test
    @CombinedParameters("0,1,2", "2", "1,2", "true,false", "true,false")
    fun `Send button active if panel has attachments`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = activationTest(textCode, attachmentsCode, recipientsCode, inviteSupported, isQuoting,true)

    @Test
    @CombinedParameters("0,1,2", "1,2", "2", "true", "true,false")
    fun `Send button active if panel has recipients and invite supported`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = activationTest(textCode, attachmentsCode, recipientsCode, inviteSupported, isQuoting, true)

    @Test
    @CombinedParameters("0", "1", "2", "false", "false")
    fun `Send button inactive if panel has recipients but invite is not supported`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = activationTest(textCode, attachmentsCode, recipientsCode, inviteSupported, isQuoting, false)

    @Test
    @CombinedParameters("0", "1", "1", "true", "false")
    fun `Send button inactive if invite supported but panel hasn't recipients`(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean
    ) = activationTest(textCode, attachmentsCode, recipientsCode, inviteSupported, isQuoting, false)

    @Test
    @CombinedParameters("1, 2")
    fun `Send button inactive when text is required and text is empty`(
        attachmentsCode: Int
    ) = activationTest(
        isTextRequired = true,
        shouldBeActive = false,
        textCode = EMPTY_RESULT_CODE,
        attachmentsCode = attachmentsCode,
        recipientsCode = NOT_EMPTY_RESULT_CODE,
        inviteSupported = false,
        isQuoting = false
    )

    /**
     * Проверка активности кнопки отправки в различных комбинациях внутренних состояний
     */
    private fun disabledMessagePanelTestTemplate(
        isSending: Boolean,
        isQuoting: Boolean,
        hasForcedRestrictions: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        restrictedByInfo: Boolean
    ) {
        // проверка изоляции состояний
        require(!(isSending && isQuoting))

        // внешние ограничения
        info.mockHasPermission(false)

        whenever(info.inviteSupported).thenReturn(inviteSupported)
        liveData = initLiveData(
            info = info,
            isEnabled = isEnabledSubject.toFlowable(BackpressureStrategy.ERROR),
            isSending = isSendingSubject.toFlowable(BackpressureStrategy.ERROR),
            isQuoting = isQuotingSubject.toFlowable(BackpressureStrategy.ERROR)
        )
        liveData.observe()

        liveData.setSendCoreRestrictions(hasForcedRestrictions)
        isEnabledSubject.onNext(false)
        isSendingSubject.onNext(isSending)
        isQuotingSubject.onNext(isQuoting)
        liveData.setMessageText(stringParamMapper(textCode))
        liveData.setAttachments(dataListParamMapper(attachmentsCode, mock())!!)
        liveData.setRecipients(dataListParamMapper(recipientsCode, mockk())!!)

        observer.assertNever(true)
    }

    /**
     * Метод тестирования явного ограничения
     */
    private fun forcedRestrictions(
        isSending: Boolean,
        isQuoting: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        restrictedByInfo: Boolean
    ) {
        // проверка изоляции состояний
        require(!(isSending && isQuoting))

        whenever(info.inviteSupported).thenReturn(inviteSupported)
        // ветка для тестирования отсутствия влияния внешних ограничений при наличии явных
        if (!restrictedByInfo) {
            info.clearCoreRestrictions()
        }
        liveData = initLiveData(
            info = info,
            isSending = isSendingSubject.toFlowable(BackpressureStrategy.ERROR),
            isQuoting = isQuotingSubject.toFlowable(BackpressureStrategy.ERROR)
        )
        liveData.observe()

        // установка явного ограничения на активацию кнопки отправки
        liveData.setSendCoreRestrictions(true)

        isSendingSubject.onNext(isSending)
        isQuotingSubject.onNext(isQuoting)
        liveData.setMessageText(stringParamMapper(textCode))
        liveData.setAttachments(dataListParamMapper(attachmentsCode, mock())!!)
        liveData.setRecipients(dataListParamMapper(recipientsCode, mockk())!!)

        observer.assertNever(true)
    }

    /**
     * Тестирование внешних ограничений
     */
    private fun externalRestrictions(
        isSending: Boolean,
        isQuoting: Boolean,
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        hasForcedRestrictions: Boolean
    ) {
        // проверка изоляции состояний
        require(!(isSending && isQuoting))

        // внешние ограничения
        info.mockHasPermission(false)

        whenever(info.inviteSupported).thenReturn(inviteSupported)
        liveData = initLiveData(
            info = info,
            isSending = isSendingSubject.toFlowable(BackpressureStrategy.ERROR),
            isQuoting = isQuotingSubject.toFlowable(BackpressureStrategy.ERROR)
        )
        liveData.observe()

        liveData.setSendCoreRestrictions(hasForcedRestrictions)
        isSendingSubject.onNext(isSending)
        isQuotingSubject.onNext(isQuoting)
        liveData.setMessageText(stringParamMapper(textCode))
        liveData.setAttachments(dataListParamMapper(attachmentsCode, mock())!!)
        liveData.setRecipients(dataListParamMapper(recipientsCode, mockk())!!)

        observer.assertNever(true)
    }

    /**
     * Тестирование требования получателей
     */
    private fun recipientRequirements(
        textCode: Int,
        attachmentsCode: Int,
        inviteSupported: Boolean,
        isSending: Boolean,
        isQuoting: Boolean
    ) {
        // проверка изоляции состояний
        require(!(isSending && isQuoting))

        whenever(info.inviteSupported).thenReturn(inviteSupported)
        liveData = initLiveData(
            info = info,
            isSending = isSendingSubject.toFlowable(BackpressureStrategy.ERROR),
            isQuoting = isQuotingSubject.toFlowable(BackpressureStrategy.ERROR)
        )
        // Установка ключевого параметра. Требуем получателей. Без них кнопку не активируем
        liveData.setRecipientsRequired(true)

        liveData.observe()
        isSendingSubject.onNext(isSending)
        isQuotingSubject.onNext(isQuoting)

        liveData.setMessageText(stringParamMapper(textCode))
        liveData.setAttachments(dataListParamMapper(attachmentsCode, mock())!!)
        liveData.setRecipients(emptyList())

        // достаточно проверить последний
        MatcherAssert.assertThat(observer.values().last(), equalTo(false))
    }

    private fun activationTest(
        textCode: Int,
        attachmentsCode: Int,
        recipientsCode: Int,
        inviteSupported: Boolean,
        isQuoting: Boolean,
        shouldBeActive: Boolean,
        isTextRequired: Boolean = false
    ) {
        whenever(info.inviteSupported).thenReturn(inviteSupported)
        info.clearCoreRestrictions()

        liveData = initLiveData(
            info = info,
            isSending = isSendingSubject.toFlowable(BackpressureStrategy.ERROR),
            isQuoting = isQuotingSubject.toFlowable(BackpressureStrategy.ERROR)
        )
        liveData.observe()
        // нет явных ограничений - проверяем только контент
        liveData.setSendCoreRestrictions(false)
        isSendingSubject.onNext(false)
        isQuotingSubject.onNext(isQuoting)

        liveData.setMessageText(stringParamMapper(textCode))
        liveData.setAttachments(dataListParamMapper(attachmentsCode, mock())!!)
        liveData.setRecipients(dataListParamMapper(recipientsCode, mockk())!!)
        liveData.setIsTextRequired(isTextRequired)

        // достаточно проверить последний
        MatcherAssert.assertThat(observer.values().last(), equalTo(shouldBeActive))
    }

    private fun MessagePanelLiveData.observe() {
        sendControlActivated.map {
            // удобно остановитсья для отладки
            it.value
        }.subscribe(observer)
    }

    /**
     * Снятие запрета на активацию по ограничениям из info
     *
     * @see getSendButtonActivated
     */
    private fun CoreConversationInfo.clearCoreRestrictions() {
        mockHasPermission(true)
    }

    private fun CoreConversationInfo.mockHasPermission(value: Boolean) {
        whenever(chatPermissions).thenReturn(Permissions().apply {
            canSendMessage = value
        })
    }
}