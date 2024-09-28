package ru.tensor.sbis.version_checker.ui.mandatory

import android.os.Build
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent.ClickCriticalUpdate
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent.ShowCriticalScreen
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory
import ru.tensor.sbis.version_checker.domain.source.UpdateSourceDetector

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R])
internal class RequiredUpdatePresenterTest {

    private lateinit var presenter: RequiredUpdatePresenter
    private lateinit var mockCommandFactory: UpdateCommandFactory
    private lateinit var mockAnalytics: Analytics

    @Test
    fun `On accept update runUpdateCommand and send analytics ClickCriticalUpdate`() {
        buildPresenter()
        presenter.onAcceptUpdate()
        verify(mockCommandFactory).create(any(), onComplete = any())
        verify(mockAnalytics).send(eq(ClickCriticalUpdate()), anyOrNull(), anyOrNull())
    }

    @Test
    fun `On sendAnalytics send analytics showCriticalScreen`() {
        buildPresenter()
        presenter.sendAnalytics()
        verify(mockAnalytics).send(eq(ShowCriticalScreen()), anyOrNull(), anyOrNull())
    }

    private fun buildPresenter() {
        mockAnalytics = mock()
        val mockDetector = mock<UpdateSourceDetector> {
            on { locateAll() } doReturn emptyList()
        }
        mockCommandFactory = spy(UpdateCommandFactory(mock(), mock(), mockDetector, mock()))
        presenter = RequiredUpdatePresenter("", mockCommandFactory, mockAnalytics)
    }

}