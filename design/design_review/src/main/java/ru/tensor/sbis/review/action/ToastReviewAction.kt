package ru.tensor.sbis.review.action

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData

/**
 * Предложение оценить приложение в виде тоста, для демо приложения
 *
 * @author ma.kolpakov
 */
internal class ToastReviewAction(
    override val reviewState: MutableLiveData<ReviewState> = MutableLiveData(ReviewState.WAIT),
    private val toastFactory: (Context, Enum<*>) -> Unit = { context, event ->
        Toast.makeText(context, "Оцените наше приложение [событие: ${event.name}]", Toast.LENGTH_SHORT).show()
    }
) : ReviewAction {

    override fun startReview(activity: Activity, event: Enum<*>) {
        reviewState.value = ReviewState.START
        toastFactory(activity, event)
        reviewState.value = ReviewState.FINISH
        reviewState.value = ReviewState.WAIT
    }
}
