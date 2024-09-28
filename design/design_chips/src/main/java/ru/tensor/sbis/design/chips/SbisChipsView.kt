package ru.tensor.sbis.design.chips

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import ru.tensor.sbis.design.chips.api.SbisChipsController
import ru.tensor.sbis.design.chips.api.SbisChipsViewApi
import ru.tensor.sbis.design.chips.list.SbisChipsAdapter
import ru.tensor.sbis.design.chips.list.SbisChipsItemDecorator
import ru.tensor.sbis.design.theme.global_variables.Offset

/**
 * Компонент "Чипсы".
 *
 * [Стандарт](https://www.figma.com/proto/AEK2ocfsXgM31YUalhoMkm/%D0%A7%D0%B8%D0%BF%D1%81%D1%8B-(Web%2C-Mobile%2C-Retail)?page-id=0%3A1&node-id=%3A5582&viewport=1095%2C882%2C0.74&scaling=min-zoom&starting-point-node-id=2138%3A5582&hotspot-hints=0&hide-ui=1)
 *
 * @author ps.smirnyh
 */
class SbisChipsView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: SbisChipsController
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    SbisChipsViewApi by controller {

    @Suppress("UNUSED")
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = ID_NULL,
        defStyleRes: Int = ID_NULL
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        SbisChipsController()
    )

    private val adapter = SbisChipsAdapter().apply {
        clickListener = { id, isSelected ->
            controller.handleSelectedChanged(id, isSelected)
        }
    }
    private val linearLayoutManager = LinearLayoutManager(this.context).apply {
        orientation = LinearLayoutManager.HORIZONTAL
    }
    private val flexboxLayoutManager = FlexboxLayoutManager(this.context).apply {
        alignItems = AlignItems.FLEX_START
    }

    private val itemDecoration = SbisChipsItemDecorator(Offset.M.getDimenPx(this.context))

    private val recyclerView = RecyclerView(this.context).apply {
        adapter = this@SbisChipsView.adapter
        layoutManager = linearLayoutManager
        addItemDecoration(itemDecoration)
        setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate(this))
    }.also {
        addView(it, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    init {
        clipToPadding = false
        clipChildren = false
        controller.attach(this, adapter)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Чтобы не обрезалась тень.
        (parent as? ViewGroup)?.clipChildren = false
    }

    /** Изменить режим многострочного отображения элементов. */
    internal fun changeMultiline(multiline: Boolean) {
        if (multiline) {
            recyclerView.layoutManager = flexboxLayoutManager
        } else {
            recyclerView.layoutManager = linearLayoutManager
        }
    }
}