package ru.tensor.sbis.design.message_view.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteBehavior
import ru.tensor.sbis.design.message_view.controller.MessageViewAPI
import ru.tensor.sbis.design.message_view.controller.MessageViewController

/**
 * Компонент, использующийся для отображения всех видов ячеек сообщений.
 *
 * @author dv.baranov
 */
class MessageView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val controller: MessageViewController
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    MessageSwipeToQuoteBehavior by controller.swipeToQuoteBehavior,
    MessageViewAPI by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0,
        @StyleRes defStyleRes: Int = 0
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        MessageViewController()
    )

    init {
        controller.initController(this)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        controller.setOnClickListener(listener)
    }
}
