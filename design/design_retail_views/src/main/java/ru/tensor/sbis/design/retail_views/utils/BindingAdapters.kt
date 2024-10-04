package ru.tensor.sbis.design.retail_views.utils

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.AttrRes
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Установка заголовка в кнопку "Назад".
 * Когда дана и строка и id ресурса строки, приоритет отдаем строке.
 * Если данные для заголовка отсутствуют, заголовок скрывается.
 *
 * @param view @SelfDocumented
 * @param text @SelfDocumented
 * @param resId @SelfDocumented
 */
@BindingAdapter(
    "text",
    "resId"
)
fun setBackButtonTitle(view: SbisTextView, text: CharSequence?, resId: Int?) {
    view.visibility = View.VISIBLE

    if (!text.isNullOrBlank()) {
        view.text = text
        return
    }

    if (resId != null && resId != 0) {
        view.setText(resId)
        return
    }

    view.text = ""
    view.visibility = View.GONE
}

/**
 * Установка цвета фона в CardView.
 *
 * @param pressedStateAttrColor цвет нажатой кнопки
 * @param defaultStateAttrColor цвет кнопки по умолчанию
 */
@BindingAdapter(
    "setPressedStateAttrColor",
    "setDefaultStateAttrColor"
)
fun setCardViewBackgroundColor(
    view: CardView,
    @AttrRes pressedStateAttrColor: Int,
    @AttrRes defaultStateAttrColor: Int
) {
    // Список состояний.
    val statesList = mutableListOf<IntArray>()
    // Список цветов.
    val colorsList = mutableListOf<Int>()

    val activeColor = view.context.getThemeColorInt(pressedStateAttrColor)
    val contrastColor = view.context.getThemeColorInt(defaultStateAttrColor)

    /**
     * Цвет нажатой кнопки.
     */
    statesList.add(intArrayOf(android.R.attr.state_pressed))
    colorsList.add(activeColor)

    /**
     * Цвет не нажатой кнопки.
     */
    statesList.add(intArrayOf(-android.R.attr.state_pressed))
    colorsList.add(contrastColor)

    /**
     * Цвет активной кнопки.
     */
    statesList.add(intArrayOf(android.R.attr.state_enabled))
    colorsList.add(activeColor)

    /**
     * Цвет кнопки по умолчанию.
     * Используется на "старых" устрайствах без поддержки ColorStateList <=22 API.
     */
    colorsList.add(contrastColor)

    val stateListColor = ColorStateList(statesList.toTypedArray(), colorsList.toIntArray())
    view.setCardBackgroundColor(stateListColor)
}