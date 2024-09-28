package ru.tensor.sbis.design.list_header

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.doOnNextLayout
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.view_ext.R as RViewExt

/**
 * Визуальный компонент для отображения даты в шапке списка
 *
 * @author ra.petrov
 */
class HeaderDateView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val delegate: DateViewDelegate
) : SbisTextView(ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(), attrs, defStyleAttr),
    BaseDateView by delegate {

    /**
     * Можно ли отобразить заголовок.
     * Внутри ListDateViewUpdater определяем видимость по некоторым правилам, но если заголовок нужно скрыть по
     * каким-то причинам для всех случаев, можно использовть методы show() и hide()
     */
    private var visible = true

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.listHeaderDateTheme,
        @StyleRes defStyleRes: Int = RViewExt.style.ItemDateHeaderDateViewDefaultTheme
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        DateViewDelegate(R.styleable.HeaderDateView[R.styleable.HeaderDateView_HeaderDateView_dateViewMode])
    ) {
        delegate.init(this, attrs, defStyleAttr, defStyleRes)
    }

    override var text: CharSequence?
        get() = super.text
        set(value) {
            // TODO На некоторых диалогах не происходит перерасчета ширины view после установки текста https://online.sbis.ru/opendoc.html?guid=2b3b6873-65b5-474b-9421-5c24f3f65db2
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && value != text && !value.isNullOrBlank()) {
                doOnNextLayout { post { requestLayout() } }
            }
            super.text = value
        }

    /**
     * Показать заголовок если до этого он был скрыт
     */
    fun show() {
        visible = true
        if (!text.isNullOrBlank())
            visibility = VISIBLE
    }

    /**
     * Скрыть заголовок. Он не будет отображаться до вызова show() даже если текст будет установлен
     */
    fun hide() {
        visibility = INVISIBLE
        visible = false
    }

    /**
     * Устанавливаем видимость с учётом visible
     */
    override fun setVisibility(visibility: Int) {
        if (visible) {
            super.setVisibility(visibility)
        }
    }
}