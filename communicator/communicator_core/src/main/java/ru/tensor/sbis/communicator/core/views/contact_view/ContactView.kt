package ru.tensor.sbis.communicator.core.views.contact_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import org.json.JSONObject
import ru.tensor.sbis.communicator.common.util.json.stringify
import ru.tensor.sbis.communicator.core.R
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams.TextStyle
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParamsProvider
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * View контакта.
 *
 * @author rv.krohalev
 */
class ContactView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes styleRes: Int = R.style.ContactViewStyle,
    styleParamsProvider: StyleParamsProvider<TextStyle>? = null
) : ViewGroup(context, attrs, defStyleAttr, styleRes) {

    init {
        id = R.id.communicator_contact_view_id
    }

    constructor(context: Context, canvasStylesProvider: CanvasStylesProvider) :
        this(context, styleParamsProvider = canvasStylesProvider.textStyleProvider)

    /**@SelfDocumented*/
    val personView: PersonView = PersonView(context).apply {
        updatePadding(left = Offset.X2S.getDimenPx(context))
        setSize(PhotoSize.M)
        id = R.id.communicator_contact_view_person_view_id
    }

    private val children: MutableList<TextLayout> = mutableListOf()

    val contactTitle = TextLayout.createTextLayoutByStyle(
        context,
        R.style.ContactTitleStyle,
        styleParamsProvider
    ) {
        isVisibleWhenBlank = false
    }

    private val contactSubtitle = TextLayout.createTextLayoutByStyle(
        context,
        R.style.ContactSubtitleStyle,
        styleParamsProvider
    ) {
        isVisibleWhenBlank = false
    }

    private val contactSubtitleSecondLine = TextLayout.createTextLayoutByStyle(
        context,
        R.style.ContactSubtitleSecondStyle,
        styleParamsProvider
    ) {
        isVisibleWhenBlank = false
    }

    private val contactRoleIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.ContactRoleIconStyle,
        styleParamsProvider
    )

    /**@SelfDocumented*/
    val contactVideoCallIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.ContactVideoCallIconStyle,
        styleParamsProvider
    ).apply { makeClickable(this@ContactView) }

    /**@SelfDocumented*/
    val contactConversationIcon = TextLayout.createTextLayoutByStyle(
        context,
        R.style.ContactConversationIconStyle,
        styleParamsProvider
    ).apply { makeClickable(this@ContactView) }

    private val touchManager = TextLayoutTouchManager(
        this@ContactView,
        contactVideoCallIcon,
        contactConversationIcon
    )

    private var titleLeftSpacing: Int = 0
    private var titleRightSpacing: Int = 0

    private var showSeparator: Boolean = false
    private val heightChanged: Boolean
        get() = measuredHeight != currentHeight
    private val currentHeight: Int
        get() = maxOf(personView.measuredHeight, contactTitle.height + contactSubtitle.height + contactSubtitleSecondLine.height + contactConversationIcon.height) + paddingTop + paddingBottom

    private val separatorSize: Float
    private val separatorPaint: Paint

    private var data: ContactViewModel? = null

    init {
        setWillNotDraw(false)
        addView(personView)
        children.addAll(
            listOf(
                contactTitle,
                contactSubtitle,
                contactSubtitleSecondLine,
                contactRoleIcon,
                contactConversationIcon,
                contactVideoCallIcon
            )
        )

        val topAndBottomPadding = Offset.XS.getDimenPx(context)
        updatePadding(
            top = topAndBottomPadding,
            bottom = topAndBottomPadding,
            left = Offset.X2S.getDimenPx(context)
        )
        background = ResourcesCompat.getDrawable(resources, RDesign.drawable.selectable_item_bg_white, null)
        titleLeftSpacing = resources.getDimensionPixelSize(RDesign.dimen.list_person_photo_view_margin_right)
        titleRightSpacing = Offset.M.getDimenPx(context)

        separatorSize = resources.getDimension(RDesign.dimen.common_separator_size)
        separatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = separatorSize
            color = context.getThemeColorInt(RDesign.attr.borderColor)
        }
        accessibilityDelegate = AutoTestsAccessHelper()

        if (isInEditMode) {
            bindData(
                ContactViewModel(
                    PersonData(null, "", null),
                    "Title",
                    "Subtitle",
                    "Subtitle second",
                    SbisMobileIcon.Icon.smi_CrownFill.character.toString(),
                    needShowContactIcon = true
                )
            )
        }
    }

    /**@SelfDocumented*/
    fun bindData(data: ContactViewModel) {
        this.data = data
        if (!isLayoutRequested && measuredWidth != 0) {
            configureLayout(measuredWidth)
        }
        // Если ячейка может изменяться по размерам
        // Нужно сделать configureLayout(), посмотреть высоту, если она изменилась - позвать requestLayout()
        if (heightChanged) {
            requestLayout()
        } else if (!isLayoutRequested) {
            internalLayout()
        }
        invalidate()
    }

    /**@SelfDocumented*/
    fun showSeparator(show: Boolean) {
        showSeparator = show
    }

    private fun configureLayout(sizeWidth: Int) {
        val model = data!!
        contactRoleIcon.configure {
            text = model.roleIcon.orEmpty()
        }

        contactVideoCallIcon.configure {
            isVisible = model.needShowContactIcon && model.needShowVideoCallIcon
        }
        contactConversationIcon.configure {
            isVisible = model.needShowContactIcon
        }

        if (!isInEditMode) {
            personView.setData(model.photoData)
        }

        val contentAvailableWidth = sizeWidth - paddingLeft - paddingRight - personView.measuredWidth - titleLeftSpacing - titleRightSpacing
        val contactIconWidth = contactVideoCallIcon.width + contactConversationIcon.width
        contactTitle.configure {
            text = model.title
            maxWidth = contentAvailableWidth - contactRoleIcon.width - contactIconWidth
            model.titleParamsConfigurator?.invoke(this)
        }
        contactSubtitle.configure {
            text = model.subtitle
            layoutWidth = contentAvailableWidth - contactIconWidth
            model.subtitleParamsConfigurator?.invoke(this)
        }
        contactSubtitleSecondLine.configure {
            isVisible = !model.needShowContactIcon
            text = model.subtitleSecond
            layoutWidth = contentAvailableWidth - contactIconWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(
            personView,
            makeUnspecifiedSpec(),
            makeUnspecifiedSpec()
        )

        val width = MeasureSpec.getSize(widthMeasureSpec)
        configureLayout(width)

        setMeasuredDimension(width, currentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        internalLayout()
    }

    private fun internalLayout() {
        val leftPos = paddingLeft
        val topPos = paddingTop
        val rightPos = measuredWidth - paddingRight

        personView.layout(leftPos, topPos, leftPos + personView.measuredWidth, topPos + personView.measuredHeight)
        contactTitle.layout(personView.right + titleLeftSpacing, topPos)
        contactSubtitle.layout(contactTitle.left, contactTitle.bottom)
        contactSubtitleSecondLine.layout(contactTitle.left, contactSubtitle.bottom)

        contactRoleIcon.layout(
            contactTitle.left + contactTitle.width + titleRightSpacing,
            contactTitle.top + (contactTitle.height - contactRoleIcon.height) / 2
        )
        contactVideoCallIcon.layout(
            rightPos - titleRightSpacing - contactVideoCallIcon.width,
            contactTitle.bottom
        )
        contactConversationIcon.layout(
            contactVideoCallIcon.left - contactConversationIcon.width,
            contactTitle.bottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        children.forEach { it.draw(canvas) }

        if (showSeparator) {
            val y = measuredHeight - separatorSize
            canvas.drawLine(0f, y, measuredWidth.toFloat(), y, separatorPaint)
        }
    }

    /**@SelfDocumented*/
    override fun hasOverlappingRendering() = false

    /**@SelfDocumented*/
    override fun onTouchEvent(event: MotionEvent): Boolean =
             touchManager.onTouch(this, event) || super.onTouchEvent(event)

    private inner class AutoTestsAccessHelper : AccessibilityDelegate() {

        private val layouts = mapOf(
            R.id.communicator_contact_view_title_id to { contactTitle.text },
            R.id.communicator_contact_view_subtitle_id to { contactSubtitle.text },
            R.id.communicator_contact_view_subtitle_second_line_id to { contactSubtitleSecondLine.text },
            R.id.communicator_contact_view_side_attribute_id to { contactRoleIcon.text },
            R.id.communicator_contact_view_conversation_icon_id to { contactVideoCallIcon.text },
            R.id.communicator_contact_view_video_call_icon_id to { contactVideoCallIcon.text }
        )

        /** Сохранение информации о view в json */
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            val description = mutableMapOf<String, String>()
            layouts.forEach { (layoutId, getter) ->
                getter().let { text ->
                    if (text.isNotBlank()) {
                        description[context.resources.getResourceEntryName(layoutId)] = text.toString()
                    }
                }
            }
            info?.text = JSONObject(description as Map<*, *>).stringify()
        }
    }
}