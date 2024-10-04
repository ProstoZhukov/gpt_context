package ru.tensor.sbis.design.cloud_view_integration

import android.text.style.ClickableSpan
import android.view.View
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.richtext.span.LongClickSpan

/**
 * Реализация ClickableSpan для обработки нажатий на номер телефона.
 *
 * @author da.zhukov
 */
internal class PhoneNumberLongClickSpan(
    private val phoneNumber: String,
    private val listener: PhoneNumberClickListener
) : ClickableSpan(), LongClickSpan {

    override fun onClick(widget: View) {
        listener.onPhoneNumberClicked(phoneNumber)
    }

    override fun onLongClick(widget: View) {
        listener.onPhoneNumberLongClicked(phoneNumber)
    }
}