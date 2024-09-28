package ru.tensor.sbis.review.action

import android.app.Activity
import androidx.lifecycle.MutableLiveData

/**
 * Пустое действие при срабатывании события показа оценки
 *
 * @author ma.kolpakov
 */
internal class NoneReviewAction(
    override val reviewState: MutableLiveData<ReviewState> = MutableLiveData(ReviewState.WAIT)
) : ReviewAction {

    override fun startReview(activity: Activity, event: Enum<*>) {
        reviewState.value = ReviewState.START
        reviewState.value = ReviewState.FINISH
        reviewState.value = ReviewState.WAIT
    }

}
