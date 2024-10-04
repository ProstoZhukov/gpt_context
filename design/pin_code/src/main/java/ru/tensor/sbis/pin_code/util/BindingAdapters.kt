package ru.tensor.sbis.pin_code.util

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.view.input.password.PasswordInputView
import ru.tensor.sbis.pin_code.R
import ru.tensor.sbis.pin_code.decl.PinCodePeriod
import ru.tensor.sbis.pin_code.view.BubbleLimitedInputView
import java.text.DecimalFormat
import ru.tensor.sbis.design.R as RDesign

/**
 * Приводит время в вид необходимый для отображения на UI.
 *
 * @author mb.kruglova
 */
@BindingAdapter("time")
internal fun SbisTextView.time(timeInSeconds: Long) {
    val formatter = DecimalFormat("00")
    val oneMinute = 60
    val minutes = timeInSeconds / oneMinute
    val seconds = timeInSeconds % oneMinute

    text = resources.getString(
        R.string.pin_code_timer_label,
        "${formatter.format(minutes)}:${formatter.format(seconds)}"
    )
}

@BindingAdapter(value = ["isMaskedCode", "isNumericKeyboard", "maxLength"], requireAll = true)
internal fun setInputTypeAndMaxLength(
    editText: BubbleLimitedInputView,
    isMaskedCode: Boolean,
    isNumericKeyboard: Boolean,
    maxLength: Int
) {
    editText.setInputTypeAndMaxLength(isMaskedCode, isNumericKeyboard, maxLength)
}

/**
 * Устанавливает текст и видимость "Не запрашивать ..." в соответствии с переданным типом периода действия пин-кода.
 *
 * @author mb.kruglova
 */
@BindingAdapter("periodField", "periodFieldAction")
internal fun AppCompatTextView.periodField(period: PinCodePeriod?, action: () -> Unit) =
    when (period) {
        is PinCodePeriod.Session -> {
            setPeriodFieldText(R.string.pin_code_period_field_session, action)
            visibility = View.VISIBLE
        }

        is PinCodePeriod.QuarterHour -> {
            setPeriodFieldText(R.string.pin_code_period_field_quarter_hour, action)
            visibility = View.VISIBLE
        }

        is PinCodePeriod.HalfHour -> {
            setPeriodFieldText(R.string.pin_code_period_field_half_hour, action)
            visibility = View.VISIBLE
        }

        is PinCodePeriod.Hour -> {
            setPeriodFieldText(R.string.pin_code_period_field_hour, action)
            visibility = View.VISIBLE
        }

        null -> visibility = View.GONE
    }

// Не заменять на SbisTextView, т.к. movementMethod не поддержан
private fun AppCompatTextView.setPeriodFieldText(periodTextRes: Int, action: () -> Unit) {
    text = SpannableStringBuilder()
        .append(resources.getString(R.string.pin_code_do_not_request))
        .append(StringUtils.SPACE)
        .append(
            SpannableString(resources.getString(periodTextRes)).apply {
                setSpan(
                    object : ClickableSpan() {
                        override fun updateDrawState(textPaint: TextPaint) {
                            textPaint.color = resources.getColor(RDesign.color.colorLink1)
                        }

                        override fun onClick(p0: View) = action.invoke()
                    },
                    0,
                    length,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                )
                movementMethod = LinkMovementMethod.getInstance()
            }
        )
}

@BindingAdapter(value = ["text", "custom:AttrChanged", "maxLengthReached"], requireAll = false)
internal fun PasswordInputView.text(
    text: String,
    listener: InverseBindingListener,
    maxLengthReachedListener: () -> Unit
) {
    if (this.value != text) {
        value = text
    }
    this.onValueChanged = { _, value ->
        listener.onChange()
        if (value.length == maxLength) {
            maxLengthReachedListener.invoke()
        }
    }
}

@InverseBindingAdapter(attribute = "text", event = "custom:AttrChanged")
fun PasswordInputView.getText() = this.value.toString()