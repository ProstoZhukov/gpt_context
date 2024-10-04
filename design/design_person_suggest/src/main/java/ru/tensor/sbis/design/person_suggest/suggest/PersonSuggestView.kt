package ru.tensor.sbis.design.person_suggest.suggest

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.person_suggest.R
import ru.tensor.sbis.design.person_suggest.suggest.contract.PersonSuggestViewApi
import ru.tensor.sbis.design.person_suggest.suggest.controller.PersonSuggestViewController

/**
 * Панель выбора персоны.
 * @see PersonSuggestViewApi
 *
 * @author vv.chekurda
 */
class PersonSuggestView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: PersonSuggestViewController
) : FrameLayout(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
), PersonSuggestViewApi by controller {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.personSuggestViewTheme,
        @StyleRes defStyleRes: Int = R.style.PersonSuggestViewDefaultStyle
    ) : this(context, attrs, defStyleAttr, defStyleRes, PersonSuggestViewController())

    private val recycler = RecyclerView(getContext()).apply {
        id = R.id.design_person_suggest_recycler_id
        itemAnimator = null
    }

    init {
        addView(recycler, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        controller.apply {
            resolveTheme(getContext(), attrs, defStyleAttr, defStyleRes)
            attach(this@PersonSuggestView, recycler)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        controller.onAttachedToWindow()
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        controller.defaultLayoutParams

    override fun setVisibility(visibility: Int) {
        val isChanged = this.visibility != visibility
        super.setVisibility(visibility)
        if (isChanged) controller.onVisibilityChanged(visibility)
    }
}