package ru.tensor.sbis.design.retail_views.input_view

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods

/**@SelfDocumented */
@InverseBindingMethods(
    InverseBindingMethod(
        type = RetailInputFieldView::class,
        attribute = "android:text",
        method = "getText"
    )
)
object RetailInputFieldViewBinder {

    /**@SelfDocumented */
    @JvmStatic
    @BindingAdapter(value = ["android:textAttrChanged"])
    fun setListener(retailInputFieldView: RetailInputFieldView, listener: InverseBindingListener?) {
        if (listener != null) {
            retailInputFieldView.editableView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) = Unit

                override fun afterTextChanged(editable: Editable) {
                    listener.onChange()
                }
            })
        }
    }

    /**@SelfDocumented */
    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(retailInputFieldView: RetailInputFieldView, text: String?) {
        if (text != retailInputFieldView.getText()) {
            retailInputFieldView.setText(text)
        }
    }
}