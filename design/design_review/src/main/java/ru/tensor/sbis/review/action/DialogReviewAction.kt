package ru.tensor.sbis.review.action

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation

/**
 * Предложение оценить приложение в виде диалога, для демо приложения
 *
 * @author ma.kolpakov
 */
internal class DialogReviewAction : ReviewAction, LifecycleObserver {

    /**
     * Подписка на состояние сервиса оценок. Реализация не поддерживает повороты экрана и публикует
     * событие завершения [ReviewState.FINISH] при любом закрытии диалога
     */
    override val reviewState = MutableLiveData(ReviewState.WAIT)

    override fun startReview(activity: Activity, event: Enum<*>) {
        reviewState.value = ReviewState.START
        if (activity !is FragmentActivity) return
        val dialog = PopupConfirmation
            .newMessageInstance(0, "Оцените наше приложение\nПозитивный сценарий: ${event.name}")
            .requestPositiveButton("OK")
        dialog.lifecycle.addObserver(this)
        dialog.show(activity.supportFragmentManager, REVIEW_TAG)
    }

    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDialogCreated() {
        reviewState.value = ReviewState.FINISH
        reviewState.value = ReviewState.WAIT
    }
}

private const val REVIEW_TAG = "REVIEW_DIALOG_TAG"
