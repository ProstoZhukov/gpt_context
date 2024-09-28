package ru.tensor.sbis.design.sbis_text_view

import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.utils.getDimen

/**
 * Биндинг-адаптеры для компонента [SbisTextView].
 *
 * @author vv.chekurda
 */
object BindingAdapters {

    /**
     * Установить цвет текста для [view] - SbisTextView при помощи ссылки на атрибут [attrResId].
     */
    @JvmStatic
    @BindingAdapter("textColorAttr")
    fun setTextColorAttr(view: SbisTextView, @AttrRes attrResId: Int) {
        if (attrResId != 0) {
            val typedValue = TypedValue()
            val theme = view.context.theme
            theme.resolveAttribute(attrResId, typedValue, true)
            view.setTextColor(typedValue.data)
        }
    }

    /**
     * Установить размер текста для [SbisTextView] при помощи ссылки на атрибут [attrResId].
     */
    @JvmStatic
    @BindingAdapter("textSizeAttr")
    fun SbisTextView.setTextSizeAttr(@AttrRes attrResId: Int) {
        if (attrResId != 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getDimen(attrResId))
        }
    }

    /**
     * Установить стиль шрифта для [textView] - SbisTextView при помощи ресурса стиля [fontStyle].
     */
    @JvmStatic
    @BindingAdapter("fontStyle")
    fun setFontStyle(textView: SbisTextView, fontStyle: Int) {
        textView.setTypeface(null, fontStyle)
    }

    /**
     * Установить цвет текста для [view] - SbisTextView при помощи ресурса цвета [colorResId].
     */
    @JvmStatic
    @BindingAdapter("textColorRes")
    fun setTextColorRes(
        view: SbisTextView,
        @ColorRes colorResId: Int
    ) {
        if (colorResId != 0) {
            view.setTextColor(ContextCompat.getColor(view.context, colorResId))
        }
    }

    /**
     * Устанавливает видимость SbisTextView в зависимости от текста.
     * Если text=null, то видимость будет установлена в View.GONE
     */
    @JvmStatic
    @BindingAdapter("visibleOrGone")
    fun SbisTextView.visibleOrGone(text: String?) {
        visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    /**@SelfDocumented */
    @JvmStatic
    @BindingAdapter("textSizeRes")
    fun SbisTextView.textSizeRes(resId: Int) = setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        resources.getDimension(resId)
    )

    /**@SelfDocumented */
    @JvmStatic
    @BindingAdapter("textRes")
    fun SbisTextView.setTextRes(@StringRes text: Int) {
        setText(text)
    }

    /**
     * Установить текст в SbisTextView при помощи строкового ресусра [text], если text=0 спрятать.
     */
    @JvmStatic
    @BindingAdapter("textResOrGone")
    fun SbisTextView.setTextResOrGone(@StringRes text: Int) {
        if (text != 0) {
            setText(text)
        }
        isVisible = text != 0
    }

    /**
     * Установить текст в SbisTextView при помощи последовательности символов [text], если text=null спрятать.
     */
    @JvmStatic
    @BindingAdapter("textAndVisibility")
    fun SbisTextView.setTextAndVisibility(text: CharSequence?) {
        this.text = text
        val visible = text != null && text.isNotEmpty()
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * Установить подсветку текста [text] исходя из [highlightedRanges].
     * Адаптер создан для поддержки внедрения SbisTextView.
     * Оригинальный адаптер по пути [common/common-business/src/main/java/ru/tensor/sbis/business/common/ui/bind_adapter/BindingAdapters.kt]
     */
    @JvmStatic
    @BindingAdapter("textWithHighlightedRanges", "highlightedRanges")
    fun SbisTextView.setTextWithHighlightedRanges(
        text: String,
        highlightedRanges: List<IntRange>
    ) {
        setTextWithHighlightRanges(text, highlightedRanges)
    }

    /**
     * Установить иконку [icon] в SbisTextView.
     */
    @JvmStatic
    @BindingAdapter("mobileIcon")
    fun SbisTextView.setMobileIcon(icon: SbisMobileIcon.Icon) {
        text = icon.character.toString()
    }

}
