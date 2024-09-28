package ru.tensor.sbis.design.retail_views.bonus_button

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import ru.tensor.sbis.design.retail_models.BonusValues
import ru.tensor.sbis.design.retail_models.utils.isMoreZero
import ru.tensor.sbis.design.retail_models.utils.isZero
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.databinding.BonusButtonViewBinding
import ru.tensor.sbis.design.retail_views.tooltip.Tooltip
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.retail_views.utils.getBigDecimalAmount
import ru.tensor.sbis.design.retail_views.utils.intAmountFormat
import ru.tensor.sbis.design.theme.Position
import ru.tensor.sbis.design.utils.LONG_CLICK_DELAY
import ru.tensor.sbis.design.utils.extentions.preventDoubleClickListener
import java.math.BigDecimal
import ru.tensor.sbis.design.R as RDesign

/**
 * Кнопка для управления бонусами
 * https://online.sbis.ru/doc/38a0fbe4-4e67-4ab7-9d56-11b7ff27ac02
 */
class BonusButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_bonus_button_theme,
    @StyleRes defStyleRes: Int = R.style.RetailViewsBonusButtonStyle_Light
) : ConstraintLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val PRICE_SCALE = 2
    }

    private val binding = BonusButtonViewBinding.inflate(LayoutInflater.from(getContext()), this, true)
        .apply {
            setBackgroundResource(R.drawable.bonus_button_bg)
            retailViewsBonusEmblem.preventDoubleClickListener(LONG_CLICK_DELAY) { onBonusEmblemClicked() }
            retailViewsBonusCount.preventDoubleClickListener(LONG_CLICK_DELAY) { onBonusCountClicked() }
            retailViewsBonusCountInput.setupGravityForHintCaretRightAlignment()
        }

    private val spendingBonuses get() = getBigDecimalAmount(binding.retailViewsBonusCount.text.toString(), PRICE_SCALE)
    private var maxBonusCount: BigDecimal? = null
    private val maxBonusCountText
        get() = maxBonusCount?.let { intAmountFormat.format(it) }.orEmpty()

    private var onActivatedAction: ((BigDecimal) -> Boolean?)? = null
    private var onDeactivatedAction: (() -> Unit)? = null

    /**
     * Поле ввода бонусов к списанию
     */
    val bonusCountInput = binding.retailViewsBonusCountInput

    /**
     * Установка данных о бонусах
     *
     * @param bonuses см. [BonusValues]
     */
    fun setBonuses(bonuses: BonusValues) {
        maxBonusCount = bonuses.availableBonusesForDecrement

        isActivated = bonuses.alreadyDecrementedBonuses.isMoreZero()

        with(binding) {
            retailViewsBonusCount.text =
                intAmountFormat.format(
                    when {
                        bonuses.alreadyDecrementedBonuses.isMoreZero() -> bonuses.alreadyDecrementedBonuses
                        else -> bonuses.availableBonusesForDecrement
                    }
                )

            retailViewsBonusCountInput.apply {
                hint = maxBonusCountText
                filters = arrayOf(InputFilter.LengthFilter(maxBonusCountText.length))
                updateLayoutParams<LayoutParams> {
                    val measureTextValue =
                        paint.measureText(maxBonusCountText, 0, maxBonusCountText.length).toInt()
                    matchConstraintMinWidth = totalPaddingStart + measureTextValue + totalPaddingEnd
                }
            }
        }
    }

    /**
     * Установить локализацию кнопки.
     * На данный момент от локализации зависит только эмблема кнопки.
     * */
    fun setLanguage(language: BonusButtonViewLanguage) {
        binding.retailViewsBonusEmblem.text = context.getString(when (language) {
            BonusButtonViewLanguage.ENGLISH -> RDesign.string.design_mobile_icon_bonus_null_eng
            BonusButtonViewLanguage.RUSSIAN -> RDesign.string.design_mobile_icon_bonus_null
        })
    }

    /**
     * Установка действия на активацию бонусов
     */
    fun setOnActivatedListener(onActivatedListener: (BigDecimal) -> Boolean?) {
        onActivatedAction = onActivatedListener
    }

    /**
     * Установка действия на деактивацию бонусов
     */
    fun setOnDeactivatedListener(onDeactivatedListener: (() -> Unit)?) {
        onDeactivatedAction = onDeactivatedListener
    }

    private fun onBonusCountClicked() {
        isActivated = false
        setBonusCountEditingMode(true)
        onDeactivatedAction?.invoke()
    }

    private fun setBonusCountEditingMode(bonusCountInputEnabled: Boolean) {
        with(binding) {
            if (retailViewsBonusCountInput.isVisible == bonusCountInputEnabled) return // уже в подходящем режиме

            retailViewsBonusCount.isVisible = !bonusCountInputEnabled
            retailViewsBonusCountInput.isInvisible = !bonusCountInputEnabled

            if (bonusCountInputEnabled) {
                retailViewsBonusCountInput.apply {
                    setText("")
                    requestFocus()
                }
            } else {
                retailViewsBonusCount.text = retailViewsBonusCountInput.text.toString().ifBlank { maxBonusCountText }
            }
        }
    }

    private fun onBonusEmblemClicked() {
        if (!canActivate()) return

        if (!isActivated) {
            setBonusCountEditingMode(false)
            val setActivated = onActivatedAction?.invoke(spendingBonuses)
            isActivated = setActivated ?: true
        } else {
            isActivated = false
            binding.retailViewsBonusCount.text = maxBonusCountText
            onDeactivatedAction?.invoke()
        }
    }

    private fun canActivate(): Boolean {
        if (bonusCountInput.isVisible && bonusCountInput.text.isNotEmpty()) {
            val inputBonuses = getBigDecimalAmount(bonusCountInput.text.toString(), PRICE_SCALE)
            when {
                inputBonuses.isZero() -> {
                    showErrorTooltip(R.string.retail_views_bonus_button_view_zero_value_error_text)
                    return false
                }
                inputBonuses > maxBonusCount -> {
                    showErrorTooltip(R.string.retail_views_bonus_button_view_exceeded_value_error_text)
                    return false
                }
            }
        }
        return true
    }

    private fun showErrorTooltip(@StringRes errorText: Int) {
        post {
            Tooltip.on(
                this,
                false
            ).apply {
                setPosition(Position.BOTTOM)
                setState(Tooltip.State.ERROR)
                setAccentBorderColor(R.attr.retail_views_main_red)
                setText(context.getString(errorText))
                show()
            }
        }
    }

    private fun EditText.setupGravityForHintCaretRightAlignment() {
        doAfterTextChanged { newText ->
            gravity = when {
                newText.isNullOrEmpty() -> Gravity.CENTER_VERTICAL or Gravity.END
                else -> Gravity.CENTER
            }
        }
    }
}