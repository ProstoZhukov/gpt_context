package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Описывает действия ([Intent]), состояния ([State]) и сайд-эффекты ([Label]) для оценки качества работы оператора.
 *
 * @author dv.baranov
 */
internal interface RateStore : Store<RateStore.Intent, RateStore.State, RateStore.Label> {

    /** @SelfDocumented */
    sealed interface Intent {
        // region Comment Field
        /** Действие на изменения фокуса поля ввода сообщения. */
        data class OnCommentFocusChanged(val isFocused: Boolean) : Intent

        /** Действие на изменение текста в поле ввода. */
        data class OnTextChanged(val newText: CharSequence) : Intent

        /** Действие на нажатие кнопки отправки. */
        data class SendButtonClicked(
            val messageUuid: UUID,
            val rateType: String,
            val disableComment: Boolean,
        ) : Intent

        /** Изменили рейтинг. */
        data class OnRatingChanged(val rating: Int) : Intent
        // endregionIntent
    }

    /** @SelfDocumented */
    sealed interface Label {
        /** Завершение работы с экраном оценки. */
        object End : Label
    }

    /** @SelfDocumented */
    @Parcelize
    data class State(
        val currentRating: Int = -1,
        val comment: String = "",
        val showValidationStatus: Boolean = false
    ) : Parcelable
}