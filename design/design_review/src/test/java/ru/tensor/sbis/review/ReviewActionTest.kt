package ru.tensor.sbis.review

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.review.action.*

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ReviewActionTest {
    private val validStates = arrayListOf(ReviewState.START, ReviewState.FINISH, ReviewState.WAIT)

    @Test
    fun `When NoneReviewAction start review, then reviewStates is valid`() {
        val liveData = mock<MutableLiveData<ReviewState>>()
        val noneReviewAction = NoneReviewAction(liveData)
        val captor = argumentCaptor<ReviewState>()

        noneReviewAction.startReview(mock(), mock())
        verify(liveData, times(3)).value = captor.capture()

        assertEquals(validStates, captor.allValues)
    }

    @Test
    fun `When ToastReviewAction start review, then reviewStates is valid`() {
        val liveData = mock<MutableLiveData<ReviewState>>()
        val noneReviewAction = ToastReviewAction(liveData) { _, _ -> }
        val captor = argumentCaptor<ReviewState>()

        noneReviewAction.startReview(mock(), mock())
        verify(liveData, times(3)).value = captor.capture()

        assertEquals(validStates, captor.allValues)
    }

    @Test
    fun `When StatisticReviewAction start review, then reviewStates is valid`() {
        val liveData = mock<MutableLiveData<ReviewState>>()
        val storage = mock<SharedPreferences>()
        whenever(storage.getLong(NEXT_ALLOWED_TIME_KEY, 0)).thenReturn(0)
        val noneReviewAction = StatisticReviewAction(mock(), { storage }, reviewState = liveData)
        val captor = argumentCaptor<ReviewState>()

        noneReviewAction.startReview(mock(), mock())
        verify(liveData, times(3)).value = captor.capture()

        assertEquals(validStates, captor.allValues)
    }

    @Test
    fun `When GooglePlayReviewAction start review, then reviewStates is valid`() {
        val liveData = mock<MutableLiveData<ReviewState>>()
        val reviewManager = mock<ReviewManager>()
        val reviewInfo = mock<ReviewInfo>()
        val launchTask = mock<Task<Void>>()
        val requestTask = mock<Task<ReviewInfo>>()
        val activityMock = mock<Activity>()
        val apiAvailabilityService = mock<ApiAvailabilityService>()

        whenever(apiAvailabilityService.isServicesAvailable(activityMock)).thenReturn(true)
        whenever(reviewManager.launchReviewFlow(activityMock, reviewInfo)).thenReturn(launchTask)
        whenever(reviewManager.requestReviewFlow()).thenReturn(requestTask)
        whenever(requestTask.isSuccessful).thenReturn(true)
        whenever(requestTask.result).thenReturn(reviewInfo)

        val noneReviewAction = GooglePlayReviewAction(liveData, { reviewManager }, { apiAvailabilityService })
        val captor = argumentCaptor<ReviewState>()
        val requestTaskCompleteListenerCaptor = argumentCaptor<OnCompleteListener<ReviewInfo>>()
        val launchTaskCompleteListenerCaptor = argumentCaptor<OnCompleteListener<Void>>()

        noneReviewAction.startReview(activityMock, mock())

        verify(requestTask).addOnCompleteListener(requestTaskCompleteListenerCaptor.capture())
        requestTaskCompleteListenerCaptor.firstValue.onComplete(requestTask)

        verify(launchTask).addOnCompleteListener(launchTaskCompleteListenerCaptor.capture())
        launchTaskCompleteListenerCaptor.firstValue.onComplete(launchTask)

        verify(liveData, times(3)).value = captor.capture()

        assertEquals(validStates, captor.allValues)
    }

}