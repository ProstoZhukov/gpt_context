package ru.tensor.sbis.design.contact_data_view.factory

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.contact_data_view.SbisContactPhoneNumberView
import ru.tensor.sbis.design.contact_data_view.R

/**
 * Фабрика по созданию вьюх на [SbisContactDataView]
 */
internal class ViewFactory {

    /** @SelfDocumented **/
    fun createView(context: Context, @StyleRes style: Int): SbisContactPhoneNumberView {
        return SbisContactPhoneNumberView(context, defStyleRes = style).apply {
            id = R.id.design_contact_data_phone_number_view_id
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}