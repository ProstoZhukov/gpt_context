package ru.tensor.sbis.pin_code.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import io.mockk.every as mockkEvery
import io.mockk.verify as mockkVerify

/**
 * Тест для [SmsRetrieverLiveData] и [SmsBroadcastReceiver].
 *
 * @author as.stafeev
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SmsRetrieverTest {

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private val code = "99509"
    private val sms = "Ваш код подтверждения СБИС: $code"
    private val codeLength = 5

    private val mockStatus = mock<Status> {
        on { statusCode }.thenReturn(CommonStatusCodes.SUCCESS)
    }
    private val mockBundle = mock<Bundle> {
        on { get(SmsRetriever.EXTRA_STATUS) }.thenReturn(mockStatus)
        on { getString(SmsRetriever.EXTRA_SMS_MESSAGE) }.thenReturn(sms)
    }
    private val mockIntent = mock<Intent> {
        on { action }.thenReturn(SmsRetriever.SMS_RETRIEVED_ACTION)
        on { extras }.thenReturn(mockBundle)
    }
    private val mockContext = mock<Context>()
    private val mockSmsRetrieverClient = mock<SmsRetrieverClient>()
    private val mockSmsRetrieverProducer = { mockSmsRetrieverClient }

    @Before
    fun setUp() {
        mockkStatic(ContextCompat::class)
        mockkEvery {
            ContextCompat.registerReceiver(
                any(),
                any(),
                any(),
                any()
            )
        } returns Intent()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `When broadcast event is received, then code is extracted`() {
        val mockLiveData = mock<SmsRetrieverLiveData>()
        val mockReceiver = SmsBroadcastReceiver(codeLength, mockLiveData)

        mockReceiver.onReceive(mock(), mockIntent)

        verify(mockLiveData).value = code
    }

    @Test
    fun `When live data observe is called, then receiver and retriever are registered`() {
        val liveData = SmsRetrieverLiveData(mockContext, codeLength, mockSmsRetrieverProducer)
        liveData.observeForever(mock())

        mockkVerify(exactly = 1) {
            ContextCompat.registerReceiver(
                any(),
                any(),
                any(),
                any()
            )
        }
        verify(mockSmsRetrieverClient).startSmsRetriever()
    }

    @Test
    fun `When live data remove observer is called, then receiver is unregistered`() {
        val liveData = SmsRetrieverLiveData(mockContext, codeLength, mockSmsRetrieverProducer)
        val mockObserver = mock<Observer<String>>()

        liveData.observeForever(mockObserver)
        liveData.removeObserver(mockObserver)

        verify(mockContext).unregisterReceiver(any())
    }
}