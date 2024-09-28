package ru.tensor.sbis.design.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Набор адаптеров для биндинга атрибутов в макеты
 *
 * @author ma.kolpakov
 * Создан 11/28/2018
 */

/**
 * Устанавливает значение и видимость счётчика
 */
@BindingAdapter("counter")
fun TextView.setCounterState(count: Int) {
    text = formatCount(count)
    visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
}

/**
 * Устанавливает видимость view.
 * При значении false видимость view будет установлена в View.INVISIBLE
 */
@BindingAdapter("isVisible")
internal fun View.isVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

/**
 * Устанавливает видимость view.
 * При значении false видимость view будет установлена в View.GONE
 */
@BindingAdapter("visibleOrGone")
internal fun View.visibleOrGone(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Устанавливает видимость TextView в зависимости от текста.
 * Если text=null, то видимость будет установлена в View.GONE
 */
@BindingAdapter("visibleOrGone")
fun TextView.visibleOrGone(text: String?) {
    visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
}

/**
 * Устанавливает заглушку для [SimpleDraweeView]
 * @param placeholder идентификатор drawable-ресурса заглушки.
 */
@BindingAdapter("placeholderImage")
fun SimpleDraweeView.setPlaceholderImage(@DrawableRes placeholder: Int) {
    if (placeholder != 0) {
        hierarchy.setPlaceholderImage(placeholder)
    }
}

/**
 * Устанавливает url изображения для [SimpleDraweeView]
 * @param url ссылка на изображение.
 */
@BindingAdapter("imageUrl")
fun SimpleDraweeView.setImageUrl(url: String?) {
    setImageURI(url)
}

/**
 * Безопасная установка ресурса цвета по id
 */
@BindingAdapter("textColorId")
internal fun TextView.textColorId(@ColorRes textColorId: Int?) {
    textColorId?.let { setTextColor(ContextCompat.getColor(context, it)) }
}

/**
 * Безопасная установка drawable-ресурса по id
 */
@Suppress("unused")
@BindingAdapter("drawableRes")
internal fun ImageView.setDrawableRes(@DrawableRes resId: Int?) {
    resId ?: return
    setImageResource(resId)
}

/**
 * Безопасная установка строкового ресурса по id. Актуально для ресурсов, которые доставляются
 * отложенно и могут отсутствовать в момент "первого" биндинга
 *
 * @param textId идентификатор строкового ресурса. Значения `null` и `0` допустимы, вместо них
 * будет установлена пустая строка
 *
 * @author ma.kolpakov
 * Создан 3/26/2019
 */
@BindingAdapter("textId")
internal fun TextView.textId(@StringRes textId: Int?) {
    text = when(textId) {
        null, 0 -> ""
        else -> context.getString(textId)
    }
}

/**
 * Установка слушателя нажатий с задержкой при клике.
 * Задержка установлена [LONG_CLICK_DELAY]
 */
@BindingAdapter("onClickWithDoubleClickPrevent")
fun View.setClickListenerWithDoubleClickPreventer(action: (view: View?) -> Unit) {
    this.setOnClickListener(preventViewFromDoubleClickWithDelay(LONG_CLICK_DELAY, action))
}

/**
 * Установка слушателя нажатий с задержкой при клике.
 * Задержка установлена [LONG_CLICK_DELAY]
 */
@BindingAdapter("onClickActionWithDoubleClickPrevent")
fun View.setClickActionWithDoubleClickPreventer(action: () -> Unit) {
    setClickListenerWithDoubleClickPreventer { action() }
}
