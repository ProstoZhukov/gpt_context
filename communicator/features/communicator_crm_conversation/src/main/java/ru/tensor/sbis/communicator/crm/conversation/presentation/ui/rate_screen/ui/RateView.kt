package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import java.util.UUID

/**
 * Контракт view компонента MVI оценка работы оператора.
 *
 * @author dv.baranov
 */
internal interface RateView : MviView<RateView.Model, RateView.Event> {

    /** @SelfDocumented */
    sealed interface Event {

        /** Изменился фокус поле ввода комментария. */
        data class OnCommentFocusChanged(val isFocused: Boolean) : Event

        /** Нажали кнопку отправки. */
        data class SendButtonClicked(
            val messageUuid: UUID,
            val rateType: String,
            val disableComment: Boolean,
        ) : Event

        /** Изменили рейтинг. */
        data class OnRatingChanged(val rating: Int) : Event

        /** Изменился текст в поле ввода комментария. */
        data class OnTextChanged(val newText: CharSequence) : Event
    }

    /** @SelfDocumented */
    data class Model(
        val currentRating: Int = 0,
        val comment: String = "",
        val showValidationStatus: Boolean = false,
    )

    /** @SelfDocumented */
    fun interface Factory : (View) -> RateView
}