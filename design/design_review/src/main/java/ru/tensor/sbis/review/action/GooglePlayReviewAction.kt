package ru.tensor.sbis.review.action

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.mobile_services_google.GoogleApiAvailabilityService

/**
 * Событие запускающее оценку приложения в GooglePlay
 *
 * @author ma.kolpakov
 */
internal class GooglePlayReviewAction(
    override val reviewState: MutableLiveData<ReviewState> = MutableLiveData(ReviewState.WAIT),
    val reviewManagerFactory: (Context) -> ReviewManager = { context ->
        ReviewManagerFactory.create(
            context
        )
    },
    val apiAvailabilityProvider: () -> ApiAvailabilityService = {
        GoogleApiAvailabilityService
    }
) : ReviewAction {

    override fun startReview(activity: Activity, event: Enum<*>) {
        if (!apiAvailabilityProvider().isServicesAvailable(activity)) return

        val reviewManager = reviewManagerFactory(activity)
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener {
            if (it.isSuccessful) {
                reviewState.value = ReviewState.START
                val reviewInfo = it.result
                val reviewTask = reviewManager.launchReviewFlow(activity, reviewInfo)
                reviewTask.addOnCompleteListener {
                    reviewState.value = ReviewState.FINISH
                    reviewState.value = ReviewState.WAIT
                }
            }
        }
    }

}
