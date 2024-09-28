package ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui

import androidx.core.view.isVisible
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.communicator.crm.conversation.databinding.CommunicatorCrmRateBinding
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.data.LOWEST_RATING
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.rate_icons_view.RateIconsView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.rate_screen.ui.rate_icons_view.RateIconsViewFactoryImpl
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.EmojiType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.StarType
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ThumbType
import ru.tensor.sbis.design.rating.SbisRatingView
import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingIconType
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import java.util.UUID
import ru.tensor.sbis.design.R as RDesign

/**
 * Реализация View содержимого экрана оценки качества работы оператора.
 *
 * @author dv.baranov
 */
internal class RateViewImpl(
    private val binding: CommunicatorCrmRateBinding,
    private val consultationRateType: ConsultationRateType,
    private val messageUuid: UUID,
    private val disableComment: Boolean,
) : BaseMviView<RateView.Model, RateView.Event>(), RateView {

    private var currentRating = 0
    private var showValidationStatus = false

    init {
        prepareIconsView()
        binding.communicatorCrmRateSendButton.apply {
            isEnabled = false
            setOnClickListener {
                dispatch(
                    RateView.Event.SendButtonClicked(
                        messageUuid,
                        consultationRateType.toType(),
                        disableComment
                    )
                )
            }
        }
        binding.communicatorCrmRateCommentField.apply {
            if (disableComment) {
                isVisible = false
            } else {
                isVisible = true
                onValueChanged = { view, value ->
                    if (view.hasFocus()) {
                        dispatch(RateView.Event.OnTextChanged(value))
                    }
                }
            }
        }
    }

    private fun prepareIconsView() {
        binding.communicatorCrmRateIconsContainer.removeAllViews()
        binding.communicatorCrmRateIconsContainer.addView(
            createIconsView()
        )
    }

    private fun createIconsView() = if (consultationRateType is StarType) {
        SbisRatingView(binding.root.context).apply {
            maxValue = MAX_STARS_COUNT
            iconSize = IconSize.X3L
            iconType = SbisRatingIconType.STARS
            emptyIconFilledMode = SbisRatingFilledMode.BORDERED
            colorsMode = SbisRatingColorsMode.STATIC
            value = 0.0
            allowUserToResetRating = false
            onRatingSelected = { rate: Double ->
                dispatch(RateView.Event.OnRatingChanged(rate.toInt()))
            }
        }
    } else {
        RateIconsViewFactoryImpl(
            binding.root.context,
        ) { rate: Int -> dispatch(RateView.Event.OnRatingChanged(rate)) }
            .createIconsView(consultationRateType)
    }

    private fun ConsultationRateType.toType() = when (this) {
        is EmojiType -> "smile"
        is StarType -> "stars"
        is ThumbType -> "thumbs"
    }

    override val renderer: ViewRenderer<RateView.Model> =
        diff {
            diff(
                get = RateView.Model::currentRating,
                set = { handleNewRating(it) },
            )
            diff(
                get = RateView.Model::comment,
                set = { handleEnteredComment(it) },
            )
            diff(
                get = RateView.Model::showValidationStatus,
                set = { handleShowValidationStatus(it) },
            )
        }

    private fun handleNewRating(rate: Int) {
        currentRating = rate
        changeViewByValidationChanges(binding.communicatorCrmRateCommentField.value.toString(), rate)
        val container = binding.communicatorCrmRateIconsContainer
        if (container.childCount > 0) {
            container.getChildAt(0).castTo<RateIconsView>()?.setRate(rate)
        }
        binding.communicatorCrmRateSendButton.isEnabled = rate > 0
    }

    private fun handleEnteredComment(text: String) {
        changeViewByValidationChanges(text, currentRating)
    }

    private fun changeViewByValidationChanges(comment: String, rate: Int) {
        val userMustEnterComment = rate in LOWEST_RATING..LOW_RATING_LIMIT && comment.isEmpty()
        val errorMessage = binding.communicatorCrmRateCommentField.context.getString(R.string.communicator_crm_rate_validation_error)
        binding.communicatorCrmRateCommentField.validationStatus = if (userMustEnterComment && showValidationStatus) {
            ValidationStatus.Error(errorMessage)
        } else {
            ValidationStatus.Default(StringUtils.EMPTY)
        }
    }

    private fun handleShowValidationStatus(show: Boolean) {
        showValidationStatus = show
        val comment = binding.communicatorCrmRateCommentField.value.toString()
        changeViewByValidationChanges(comment, currentRating)
    }

    fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        setRootBottomPadding(keyboardHeight)
        return true
    }

    fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        setRootBottomPadding(keyboardHeight)
        return true
    }

    private fun setRootBottomPadding(padding: Int) {
        binding.root.let {
            val defaultPadding = it.context.getDimenPx(RDesign.attr.offset_l)
            it.setBottomPadding(padding + defaultPadding)
        }
    }
}

internal const val LOW_RATING_LIMIT = 2
private const val MAX_STARS_COUNT = 5
