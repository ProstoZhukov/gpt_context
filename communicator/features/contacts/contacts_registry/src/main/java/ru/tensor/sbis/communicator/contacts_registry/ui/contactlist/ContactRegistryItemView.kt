package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import org.json.JSONObject
import ru.tensor.sbis.communicator.common.util.json.stringify
import ru.tensor.sbis.communicator.common.view.CheckBoxLayout
import ru.tensor.sbis.communicator.core.views.contact_view.ContactView
import ru.tensor.sbis.communicator.core.views.contact_view.ContactViewModel
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutAutoTestsHelper
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.communicator.contacts_registry.R as RContacts
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * View ячейки списка реестра контактов.
 *
 * @author vv.chekurda
 */
internal class ContactRegistryItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    stylesProvider: CanvasStylesProvider? = null
) : ViewGroup(context) {

    private var findMoreSeparator: FindMoreSeparator? = null

    /** @SelfDocumented */
    val contactItemContainer = ContactItemContainerView(context, stylesProvider).apply {
        id = RContacts.id.communicator_contact_item_container
    }

    /** @SelfDocumented */
    val swipeableLayout = SwipeableLayout(context).apply {
        id = RContacts.id.communicator_contact_swipe_layout
        addView(contactItemContainer)
    }

    /** @SelfDocumented */
    val contactView: ContactView
        get() = contactItemContainer.contactView

    /** @SelfDocumented */
    val personView: PersonView
        get() = contactView.personView

    init {
        setWillNotDraw(false)
        addView(swipeableLayout)
        contactItemContainer.background = ContextCompat.getDrawable(context, RDesign.drawable.selectable_item_bg_white)
        contactView.background = null
        if (isInEditMode) changeFindMoreVisibility(true)
    }

    /** @SelfDocumented */
    var isCheckModeEnabled: Boolean
        get() = contactItemContainer.isCheckModeEnabled
        set(value) { contactItemContainer.isCheckModeEnabled = value }

    /** @SelfDocumented */
    var isTopSeparatorVisible: Boolean
        get() = contactItemContainer.isTopSeparatorVisible
        set(value) { contactItemContainer.isTopSeparatorVisible = value }

    /** @SelfDocumented */
    fun changeFindMoreVisibility(isVisible: Boolean) {
        findMoreSeparator = if (isVisible) {
            safeRequestLayout()
            FindMoreSeparator(this)
        } else null
    }

    /** @SelfDocumented */
    fun updateCheckState(checked: Boolean) {
        contactItemContainer.updateCheckState(checked)
    }

    /** @SelfDocumented */
    fun setFormattedDateTime(formattedDateTime: FormattedDateTime?) {
        contactItemContainer.dateView.setFormattedDateTime(formattedDateTime)
    }

    /** @SelfDocumented */
    fun showSeparatorTopMargin(show: Boolean) {
        contactItemContainer.topSeparator.useTopMargin = show
    }

    /** @SelfDocumented */
    fun setHasActivityStatus() {
        personView.setHasActivityStatus(true)
    }

    /** @SelfDocumented */
    fun bindData(data: ContactViewModel) {
        contactView.bindData(data)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        findMoreSeparator?.measure(width)
        measureChild(swipeableLayout, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            swipeableLayout.measuredWidth,
            swipeableLayout.measuredHeight + (findMoreSeparator?.measuredHeight ?: 0)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        findMoreSeparator?.layout(0, 0)
        val swipeableTop = findMoreSeparator?.measuredHeight ?: 0
        swipeableLayout.layout(
            0,
            swipeableTop,
            swipeableLayout.measuredWidth,
            swipeableTop + swipeableLayout.measuredHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        findMoreSeparator?.draw(canvas)
    }

    /**
     * Разделитель "найдено еще в сотрудниках" для отображения при поиске в реестре контактов.
     */
    private class FindMoreSeparator(private val parent: View) {

        private val separator = ContactSeparator(
            parent,
            parent.context.getThemeColorInt(RDesign.attr.borderColor)
        )

        private val title = TextLayout {
            text = parent.resources.getString(RCommunicatorDesign.string.communicator_item_contact_list_segment_divider_label)
            paint.textSize = parent.context.getDimen(RDesign.attr.fontSize_l_scaleOn)
            paint.color = parent.context.getThemeColorInt(RDesign.attr.primaryTextColor)
            padding = TextLayout.TextLayoutPadding(
                start = parent.context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m),
                end = parent.context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_2xs)
            )
        }

        private val titleHeight = parent.context.getDimenPx(RDesign.attr.inlineHeight_xs)

        /** @SelfDocumented */
        var isVisible: Boolean = true
            set(value) {
                field = value
                val isChanged = title.configure { isVisible = value }
                if (isChanged) parent.safeRequestLayout()
            }

        /** @SelfDocumented */
        var measuredWidth: Int = 0
            private set

        /** @SelfDocumented */
        var measuredHeight: Int = 0
            private set

        /** @SelfDocumented */
        fun measure(width: Int) {
            separator.width = width
            measuredWidth = if (isVisible) width else 0
            measuredHeight = if (isVisible) separator.height + titleHeight else 0
        }

        /** @SelfDocumented */
        fun layout(x: Int, y: Int) {
            if (!isVisible) return
            separator.layout(x, y)
            title.layout(
                x,
                y + separator.height + (titleHeight - title.height) / 2
            )
        }

        /** @SelfDocumented */
        fun draw(canvas: Canvas) {
            if (!isVisible) return
            separator.draw(canvas)
            title.draw(canvas)
        }
    }
}

/**
 * Контейнер view контакта для отображения в реестре контактов.
 *
 * @author vv.chekurda
 */
internal class ContactItemContainerView @JvmOverloads constructor(
    context: Context,
    stylesProvider: CanvasStylesProvider? = null
) : ViewGroup(context) {

    /** @SelfDocumented */
    val contactView = ContactView(
        context,
        styleParamsProvider = stylesProvider?.textStyleProvider
    ).apply {
        id = RContacts.id.communicator_contacts_content
    }

    /** @SelfDocumented */
    val dateView = ContactDateView(context).apply {
        id = RContacts.id.communicator_contact_item_date_header
    }

    /**
     * Разметка чекбокса.
     * @see CheckBoxLayout
     * Используется при массовом выделении ячеек.
     */
    private var checkBoxLayout: CheckBoxLayout? = null

    /** Состояние активированности чекбокса [checkBoxLayout] */
    private var isChecked: Boolean = false
        get() = checkBoxLayout?.isChecked ?: field
        set(value) {
            field = value
            checkBoxLayout?.isChecked = value
        }

    /**
     * Ширина разметки чекбокса [checkBoxLayout].
     * Вернет размер отличный от нуля в случае,
     * если активированности мода отображения множественного выбора [isCheckModeEnabled].
     */
    private val checkBoxLayoutWidth: Int
        get() = checkBoxLayout?.width ?: 0

    /** @SelfDocumented */
    val topSeparator = ContactSeparator(
        this,
        context.getThemeColorInt(RDesign.attr.borderColor),
        context.getDimenPx(RDesign.attr.offset_s)
    )

    init {
        setWillNotDraw(false)
        addView(contactView)
        addView(dateView)
        accessibilityDelegate = AutoTestsAccessHelper()
    }

    /** @SelfDocumented */
    var isTopSeparatorVisible: Boolean = true
        set(value) {
            field = value
            topSeparator.isVisible = value
        }

    /**
     * Состояние включенности мода отображения ячейки для множественного выбора.
     * При изменении вызовет перестроение ячейки под отображение с или без чекбокса [checkBoxLayout].
     */
    var isCheckModeEnabled = false
        set(value) {
            if (value != field) {
                field = value
                safeRequestLayout()
            }
        }

    /**
     * Обновить состояние выбранного чекбокса.
     * Обновление вызовет перерисовку для отображения нового сотояния.
     *
     * @param checked true, если чекбокс должен быть выбран.
     */
    fun updateCheckState(checked: Boolean) {
        if (checked == isChecked) return
        isChecked = checked
        safeRequestLayout()
    }

    /**
     * Создать статичную разметку чекбокса,
     * если активирован мод множественно отображения.
     */
    private fun tryInitCheckBox() {
        if (isCheckModeEnabled && checkBoxLayout == null) {
            checkBoxLayout = CheckBoxLayout(this).also {
                it.isChecked = isChecked
            }
        } else if (!isCheckModeEnabled) {
            checkBoxLayout = null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        tryInitCheckBox()

        measureChild(contactView, makeExactlySpec(width - checkBoxLayoutWidth), makeUnspecifiedSpec())
        measureChild(dateView, makeUnspecifiedSpec(), makeUnspecifiedSpec())
        topSeparator.width = contactView.measuredWidth - dateView.measuredWidth + checkBoxLayoutWidth

        setMeasuredDimension(
            width,
            contactView.measuredHeight + topSeparator.height
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        topSeparator.layout(0, 0)
        dateView.layout(
            measuredWidth - dateView.measuredWidth,
            0,
            measuredWidth,
            dateView.measuredHeight
        )
        contactView.layout(
            checkBoxLayoutWidth,
            topSeparator.bottom,
            checkBoxLayoutWidth + contactView.measuredWidth,
            topSeparator.bottom + contactView.measuredHeight
        )
        layoutCheckBox()
    }

    private fun layoutCheckBox() {
        checkBoxLayout?.let {
            val title = contactView.contactTitle
            val titleTextCenterVertical = (title.bottom - title.textPaint.textHeight / 2) + (dateView.bottom - dateView.height / 2)
            val top = (titleTextCenterVertical ) - it.height / 2
            it.layout(0, top)
        }
    }

    override fun onDraw(canvas: Canvas) {
        topSeparator.draw(canvas)
        checkBoxLayout?.draw(canvas)
    }

    private inner class AutoTestsAccessHelper : AccessibilityDelegate() {
        /**@SelfDocumented */
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            if (isCheckModeEnabled) {
                val description = mutableMapOf<String, String>()

                checkBoxLayout?.let {
                    val checkboxInfo = it.getNodeInfo(context)
                    description[checkboxInfo.first] = checkboxInfo.second
                }
                info.text = JSONObject(description as Map<*, *>).stringify()
            }
        }
    }
}

/**
 * Разделитель для view ячейки контакта.
 *
 * @author vv.chekurda
 */
internal class ContactSeparator(
    private val parent: View,
    @ColorInt separatorColor: Int = RDesign.attr.borderColor,
    @Px private val topMargin: Int = 0
) {

    @Px
    private val separatorHeight: Int =
        parent.resources.getDimensionPixelSize(RCommunicatorDesign.dimen.communicator_contact_list_item_separator_line_height)

    private val paint = Paint().apply {
        isAntiAlias = true
        style = FILL
        color = separatorColor
    }

    private val rect = RectF()

    /**@SelfDocumented */
    var isVisible: Boolean = true
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) parent.safeRequestLayout()
        }

    /**@SelfDocumented */
    var useTopMargin: Boolean = true
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) parent.safeRequestLayout()
        }

    /**@SelfDocumented */
    @get:Px
    val height: Int
        get() = if (isVisible) {
            separatorHeight + if (useTopMargin) topMargin else 0
        } else 0

    /**@SelfDocumented */
    @get:Px
    var width: Int = 0
        get() = if (isVisible) field else 0

    /**@SelfDocumented */
    @get:Px
    val bottom: Int
        get() = if (isVisible) rect.bottom.toInt() else 0

    /**@SelfDocumented */
    fun layout(x: Int, y: Int) {
        if (isVisible) {
            val rectTop = y.toFloat() + if (useTopMargin) topMargin else 0
            rect.set(
                x.toFloat(),
                rectTop,
                x + width.toFloat(),
                rectTop + separatorHeight
            )
        } else {
            rect.set(
                x.toFloat(),
                y.toFloat(),
                x.toFloat(),
                y.toFloat()
            )
        }
    }

    /**@SelfDocumented */
    fun draw(canvas: Canvas) {
        if (!isVisible) return
        canvas.drawRect(rect, paint)
    }
}

/**
 * View для отображения даты в разделителе ячейки контакта.
 *
 * @author vv.chekurda
 */
internal class ContactDateView(context: Context) : View(context) {

    private val dateLayout = TextLayout {
        paint.color = context.getThemeColorInt(RDesign.attr.unaccentedTextColor)
        paint.textSize = context.getDimen(RDesign.attr.fontSize_3xs_scaleOn)
        isVisibleWhenBlank = false
    }.apply {
        updatePadding(
            start = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m),
            end = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m)
        )
    }

    init {
        accessibilityDelegate = TextLayoutAutoTestsHelper(this, dateLayout)
        if (isInEditMode) dateLayout.configure { text = "10.03" }
    }

    /**@SelfDocumented */
    fun setFormattedDateTime(formattedDateTime: FormattedDateTime?) {
        val isChanged = dateLayout.configure { text = formattedDateTime?.date.orEmpty() }
        if (isChanged) safeRequestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(dateLayout.width, dateLayout.height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        dateLayout.layout(0, 0)
    }

    override fun onDraw(canvas: Canvas) {
        dateLayout.draw(canvas)
    }
}