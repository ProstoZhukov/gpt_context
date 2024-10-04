package ru.tensor.sbis.design.text_span.text

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import android.text.Editable
import android.text.TextWatcher

/**@SelfDocumented**/
@InverseBindingMethods(
    InverseBindingMethod(
        type = InputTextBox::class,
        attribute = "android:text",
        method = "getText"
    )
)

@Suppress("unused")
object InputTextBoxBinder {
    /**@SelfDocumented**/
    @JvmStatic
    @BindingAdapter(value = ["android:textAttrChanged"])
    fun setListener(inputTextBox: InputTextBox, listener: InverseBindingListener?) {
        if (listener != null) {
            inputTextBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    listener.onChange()
                }
            })
        }
    }

    /**@SelfDocumented**/
    @JvmStatic
    @BindingAdapter("android:text")
    fun setText(inputTextBox: InputTextBox, text: CharSequence?) {
        if (text.toString() != inputTextBox.text.toString()) {
            inputTextBox.text = text
        }
    }
}