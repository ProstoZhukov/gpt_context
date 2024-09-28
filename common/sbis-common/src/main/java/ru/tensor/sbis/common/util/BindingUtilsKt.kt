package ru.tensor.sbis.common.util

import android.content.res.Resources
import androidx.databinding.BindingAdapter
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible

@BindingAdapter("onClick")
fun View.setActionOnClick(action: (() -> Unit)?) {
    setOnClickListener { action?.invoke() }
}

@BindingAdapter("onCheckboxClick")
fun CheckBox.setActionOnCheckboxClick(action: ((Boolean) -> Unit)) {
    setOnClickListener {
        action.invoke(isChecked)
    }
}

@BindingAdapter("isNotGone")
fun View.setNotGone(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("isNotInvisible")
fun View.setNotInvisible(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("textResOrGone")
fun TextView.setTextResOrGone(@StringRes text: Int) {
    if (text != 0) {
        setText(text)
    }
    isVisible = text != 0
}

@BindingAdapter("textOrGone")
internal fun TextView.textOrGone(text: CharSequence?) {
    @Suppress("LiftReturnOrAssignment")
    if (text.isNullOrEmpty()) {
        visibility = View.GONE
    } else {
        setText(text)
        visibility = View.VISIBLE
    }
}

@BindingAdapter("textOrInvisible")
internal fun TextView.setTextOrInvisible(text: CharSequence?) {
    @Suppress("LiftReturnOrAssignment")
    if (text.isNullOrEmpty()) {
        visibility = View.INVISIBLE
    } else {
        setText(text)
        visibility = View.VISIBLE
    }
}

@BindingAdapter("drawableResOrGone")
fun ImageView.setDrawableResOrGone(@DrawableRes drawable: Int?) {
    if (drawable == null || drawable == Resources.ID_NULL) {
        isVisible = false
        return
    }
    setImageResource(drawable)
    isVisible = true
}

@BindingAdapter("tintColor")
fun ImageView.setTintColor(@ColorInt color: Int?) {
    color?.let {
        setColorFilter(it, android.graphics.PorterDuff.Mode.MULTIPLY)
    }
}