package ru.tensor.sbis.version_checker.ui.recommended

import org.junit.Test
import org.mockito.kotlin.*
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent
import ru.tensor.sbis.version_checker.domain.cache.VersioningLocalCache
import ru.tensor.sbis.version_checker.domain.source.UpdateCommandFactory
import ru.tensor.sbis.version_checker.domain.source.UpdateSourceDetector

internal class RecommendedUpdatePresenterTest {

    private lateinit var presenter: RecommendedUpdatePresenter
    private lateinit var mockCommandFactory: UpdateCommandFactory
    private lateinit var mockAnalytics: Analytics
    private lateinit var mockLocalCache: VersioningLocalCache

    @Test
    fun `On accept update runUpdateCommand and send analytics ClickRecommendedUpdate`() {
        buildPresenter()
        presenter.onAcceptUpdate()
        verify(mockCommandFactory).create(any(), onComplete = any())
        verify(mockAnalytics).send(eq(AnalyticsEvent.ClickRecommendedUpdate()), anyOrNull(), anyOrNull())
    }

    @Test
    fun `On click on postpone button delegate to local cache`() {
        buildPresenter()
        presenter.onPostponeUpdate(true)
        verify(mockLocalCache).postponeUpdateRecommendation(true)
    }

    private fun buildPresenter() {
        mockAnalytics = mock()
        mockLocalCache = mock()
        val mockDetector = mock<UpdateSourceDetector> {
            on { locateAll() } doReturn emptyList()
        }
        mockCommandFactory = spy(UpdateCommandFactory(mock(), mock(), mockDetector, mock()))
        presenter = RecommendedUpdatePresenter("", mockCommandFactory, mockLocalCache, mockAnalytics)
    }
}