package ru.tensor.sbis.design.view.input.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.selection.ValueSelectionInputView

/**
 * Двусторонние биндинг адаптеры полей ввода.
 *
 * @author ps.smirnyh
 */
@InverseBindingMethods(
    InverseBindingMethod(
        type = BaseInputView::class,
        attribute = "value",
        method = "getValue"
    )
)
internal object TwoWayInputViewBindings {
    @JvmStatic
    @BindingAdapter(value = ["valueAttrChanged"])
    fun setListener(
        view: BaseInputView,
        listener: InverseBindingListener?
    ) {
        if (listener != null) {
            view.onValueChanged = { _, _ -> listener.onChange() }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["valueAttrChanged", "onValueChanged"])
    fun setListener(
        view: BaseInputView,
        listener: InverseBindingListener?,
        onValueChanged: ((BaseInputView, String) -> Unit)?
    ) {
        if (listener != null) {
            view.onValueChanged = { v, value ->
                listener.onChange()
                onValueChanged?.invoke(v, value)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("value")
    fun setValue(view: BaseInputView, text: CharSequence?) {
        val stringText = text ?: ""
        if (stringText != view.value) {
            view.value = stringText
        }
    }

    @JvmStatic
    @BindingAdapter("value")
    fun setValue(view: BaseInputView, text: String?) {
        setValue(view, text as? CharSequence)
    }

    @JvmStatic
    @BindingAdapter("value")
    fun setValue(view: BaseInputView, @StringRes text: Int?) =
        setValue(view, text?.let { view.resources.getString(it) })

    @JvmStatic
    @InverseBindingAdapter(attribute = "value")
    fun getValue(view: BaseInputView): String =
        view.value.toString()
}

/**
 * Адаптер для привязки слушателя клика по полю ввода. Стандартное поведение при кликах - слушатель получения фокуса
 * при первом клике, слушатель клика при втором - нужно передать [onFocusReceives] передать false. Чтобы слушатель
 * клика сработал сразу при первом и последующих кликах нужно [onFocusReceives] передать true, при этом слушатель
 * получения фокуса будет измененён.
 * @param onFieldClick обработчик клика по полю ввода, null для отписки.
 * @param onFocusReceives true если нужно тоже обрабатывать клик при получении фокуса, false как обычно.
 */
@BindingAdapter(
    value = ["onFieldClickListener", "onFocusReceives"],
    requireAll = false
)
internal fun BaseInputView.setOnFieldClickListener(
    onFieldClick: ((View) -> Unit)?,
    onFocusReceives: Boolean = false
) {
    onFieldClickListener = onFieldClick?.let { fireEvent -> View.OnClickListener { fireEvent(it) } }
    if (onFocusReceives) {
        onFocusChangeListener = onFieldClick?.let { fireEvent ->
            View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) fireEvent(v) }
        }
    }
}

/**
 * Адаптер для привязки слушателя клика по полю ввода. Стандартное поведение при кликах - слушатель получения фокуса
 * при первом клике, слушатель клика при втором - нужно передать [onFocusReceives] передать false. Чтобы слушатель
 * клика сработал сразу при первом и последующих кликах нужно [onFocusReceives] передать true, при этом слушатель
 * получения фокуса будет измененён.
 * @param onFieldClick обработчик клика по полю ввода, null для отписки.
 * @param onFocusReceives true если нужно тоже обрабатывать клик при получении фокуса, false как обычно.
 */
@BindingAdapter(
    value = ["onFieldClickListener", "onFocusReceives"],
    requireAll = false
)
internal fun BaseInputView.setOnFieldClickListener(
    onFieldClick: (() -> Unit)?,
    onFocusReceives: Boolean = false
) {
    onFieldClickListener = onFieldClick?.let { fireEvent -> View.OnClickListener { fireEvent() } }
    if (onFocusReceives) {
        onFocusChangeListener = onFieldClick?.let { fireEvent ->
            View.OnFocusChangeListener { _, hasFocus -> if (hasFocus) fireEvent() }
        }
    }
}

/**
 * Адаптер для привязки слушателя клика по полю ввода, см. [setOnFieldClickListener]. Стандартное поведение при кликах -
 * слушатель получения фокуса при первом клике, слушатель клика при втором - нужно передать [onFocusReceives] передать
 * false. Чтобы слушатель клика сработал сразу при первом и последующих кликах нужно [onFocusReceives] передать true,
 * при этом слушатель получения фокуса будет измененён. Чтобы иконка для выбора значения подписалась на тот же самый
 * слушатель клика необходимо [onFocusReceivesOnIconClick] передать true, false если не нужно.
 * @param onFieldClick обработчик клика по полю ввода, null для отписки.
 * @param onFocusReceives true если нужно тоже обрабатывать клик при получении фокуса, false как обычно.
 * @param onFocusReceivesOnIconClick true если нужно получать фокус при клике на иконку, при этом будет вызван
 * слушатель клика по полю вместе с получением фокуса полем, false - будет произведена отписка от клика по иконке.
 */
@BindingAdapter("onFieldClickListener", "onFocusReceives", "onFocusReceivesOnIconClick")
fun ValueSelectionInputView.setOnFieldClickListener(
    onFieldClick: ((View) -> Unit)?,
    onFocusReceives: Boolean = false,
    onFocusReceivesOnIconClick: Boolean = false
) {
    (this as BaseInputView).setOnFieldClickListener(onFieldClick, onFocusReceives)
    onListIconClickListener = if (onFocusReceivesOnIconClick) {
        { onFieldClick?.invoke(it) }
    } else {
        null
    }
}

/**
 * Адаптер для привязки слушателя клика по полю ввода, см. [setOnFieldClickListener]. Стандартное поведение при кликах -
 * слушатель получения фокуса при первом клике, слушатель клика при втором - нужно передать [onFocusReceives] передать
 * false. Чтобы слушатель клика сработал сразу при первом и последующих кликах нужно [onFocusReceives] передать true,
 * при этом слушатель получения фокуса будет измененён. Чтобы иконка для выбора значения подписалась на тот же самый
 * слушатель клика необходимо [onFocusReceivesOnIconClick] передать true, false если не нужно.
 * @param onFieldClick обработчик клика по полю ввода, null для отписки.
 * @param onFocusReceives true если нужно тоже обрабатывать клик при получении фокуса, false как обычно.
 * @param onFocusReceivesOnIconClick true если нужно получать фокус при клике на иконку, при этом будет вызван
 * слушатель клика по полю вместе с получением фокуса полем, false - будет произведена отписка от клика по иконке.
 */
@BindingAdapter("onFieldClickListener", "onFocusReceives", "onFocusReceivesOnIconClick")
fun ValueSelectionInputView.setOnFieldClickListener(
    onFieldClick: (() -> Unit)?,
    onFocusReceives: Boolean = false,
    onFocusReceivesOnIconClick: Boolean = false
) {
    (this as BaseInputView).setOnFieldClickListener(onFieldClick, onFocusReceives)
    onListIconClickListener = if (onFocusReceivesOnIconClick) {
        { onFieldClick?.invoke() }
    } else {
        null
    }
}