package ru.tensor.sbis.review.action

import android.app.Activity
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.review.Analytics
import ru.tensor.sbis.review.NEXT_ALLOWED_TIME_KEY

/**
 * @author ma.kolpakov
 */
internal class StatisticReviewAction(
    private val analytics: Analytics,
    val storageProvider: () -> SharedPreferences,
    override val reviewState: MutableLiveData<ReviewState> = MutableLiveData(ReviewState.WAIT)
) : ReviewAction {

    override fun startReview(activity: Activity, event: Enum<*>) {
        reviewState.value = ReviewState.START
        val storage = storageProvider()
        val isFirst = storage.getLong(NEXT_ALLOWED_TIME_KEY, 0) == 0L
        val eventString = event.name
        val eventClassString = event::class.java.name

        analytics.reportReviewEvent(eventClassString, eventString, isFirst)

        reviewState.value = ReviewState.FINISH
        reviewState.value = ReviewState.WAIT
    }

}
