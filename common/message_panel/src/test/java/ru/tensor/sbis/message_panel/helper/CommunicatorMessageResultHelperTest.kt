package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.whenever
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.apache.commons.lang3.StringUtils.EMPTY
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.integration.CommunicatorMessageResultHelper

/**
 * Тестирование вспомогательного класса [CommunicatorMessageResultHelper] для работы
 * с результатом микросервиса сообщений
 *
 * @author vv.chekurda
 */
@RunWith(JUnitParamsRunner::class)
class CommunicatorMessageResultHelperTest {

    private val resultHelper: MessageResultHelper<MessageResult, SendMessageResult> = CommunicatorMessageResultHelper()

    @Mock
    private lateinit var messageResult: MessageResult

    @Mock
    private lateinit var sentMessageResult: SendMessageResult

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Test
    @Parameters(
        "SUCCESS",
        "WARNING"
    )
    fun `Consider operation success when message result's error code success or warning`(errorCode: ErrorCode) {
        whenever(messageResult.status).thenReturn(CommandStatus(errorCode, EMPTY))

        assertFalse(resultHelper.isResultError(messageResult))
    }

    @Test
    @Parameters(
        "SUCCESS",
        "WARNING"
    )
    fun `Consider sending success when message sending result's error code success or warning`(errorCode: ErrorCode) {
        whenever(sentMessageResult.status).thenReturn(CommandStatus(errorCode, EMPTY))

        assertFalse(resultHelper.isSentResultError(sentMessageResult))
    }

    @Test
    @Parameters(
        "NETWORK_ERROR",
        "OTHER_ERROR",
        "NOT_AVAILABLE"
    )
    fun `Operation has error when result's error code is not success or warning`(errorCode: ErrorCode) {
        whenever(messageResult.status).thenReturn(CommandStatus(errorCode, EMPTY))

        assertTrue(resultHelper.isResultError(messageResult))
    }

    @Test
    @Parameters(
        "NETWORK_ERROR",
        "OTHER_ERROR",
        "NOT_AVAILABLE"
    )
    fun `Sending has error when result's error code is not success or warning`(errorCode: ErrorCode) {
        whenever(sentMessageResult.status).thenReturn(CommandStatus(errorCode, EMPTY))

        assertTrue(resultHelper.isSentResultError(sentMessageResult))
    }
}
