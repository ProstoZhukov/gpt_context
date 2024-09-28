package ru.tensor.sbis.communicator.core.views.conversation_views.base

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Layout.Alignment.ALIGN_CENTER
import android.view.View
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.common.data.theme.ConversationUnreadIconType
import ru.tensor.sbis.communicator.common.data.theme.ConversationUnreadIconType.*
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_authorBlockTitlePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_draftIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_draftIconText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_errorIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_errorSendingIconText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_personCompanyIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_personCompanyIconText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_sendingClockColor
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_sendingIconText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_timePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_titlePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_unreadIconPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_unreadIconText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_unviewedTimePaint
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.TextLayout.TextLayoutPadding
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.view_ext.drawable.ClockDrawable

/**
 * Layout для отображения области заголовка в ячейке реестра диалогов/каналов.
 * Содержит разметку заголовка, постфикса с количеством участников, иконку персоны внутри компании,
 * статус непрочитанности, дату и время отправки сообщения.
 *
 * Схема области заголовка для реестра каналов:
 * <........... layoutWidth ............>
 * [titleLayout] [dateLayout] [timeLayout]
 *
 * Схема области заголовка для реестра диалогов:
 * <............................................. layoutWidth .................................................>
 * [titleLayout] [titlePostfixLayout] [inMyCompanyIconLayout] [unreadStatusIconLayout] [dateLayout] [timeLayout]
 *
 * @author vv.chekurda
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class ConversationItemTitleLayout(private val parent: View) {

    private var measuredWidth: Int = 0
    private var measuredHeight: Int = 0

    private var clockDrawable: ClockDrawable? = null
    private val lazyClockDrawable: ClockDrawable by lazy(LazyThreadSafetyMode.NONE) {
        ClockDrawable(parent.context).apply {
            callback = parent
            size = CommunicatorTheme.iconSize2XS
            color = theme_sendingClockColor
        }
    }

    /**
     * Список дочерних разметок.
     */
    private val children = mutableListOf<TextLayout>()

    /**
     * Разметка названия заголовка.
     */
    val titleLayout = TextLayout(theme_titlePaint)
        .also { children.add(it) }

    /**
     * Разметка постфикса названия [titleLayout] с количеством участников диалога формата (+15).
     * Используется только для отображения в ячейках диалогов.
     */
    private var titlePostfixLayout: TextLayout? = null

    private val lazyTitlePostfixLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout(theme_titlePaint) {
            padding = TextLayoutPadding(end = CommunicatorTheme.offset2XS)
            isVisibleWhenBlank = false
        }.also { children.add(it) }
    }

    /**
     * Разметка иконки персоны внутри компании для входящих сообщений по диалогу.
     * Используется только для отображения в ячейках диалогов.
     */
    private var inMyCompanyIconLayout: TextLayout? = null

    /**
     * Разметка иконки персоны внутри компании для входящих сообщений по диалогу.
     * Используется только для отображения в ячейках диалогов.
     */
    private val lazyInMyCompanyIconLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout(theme_personCompanyIconPaint) {
            text = theme_personCompanyIconText
            alignment = ALIGN_CENTER
            padding = TextLayoutPadding(end = CommunicatorTheme.offset2XS)
        }.also { children.add(it) }
    }

    /**
     * Разметка иконки статуса непрочитанности сообщения (глаз, ошибка отправки, часики).
     * Используется только для отображения в ячейках диалогов.
     */
    var unreadStatusIconLayout: TextLayout? = null
        private set

    private val lazyUnreadStatusIconLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout(theme_unreadIconPaint) {
            alignment = ALIGN_CENTER
            padding = TextLayoutPadding(end = CommunicatorTheme.offset2XS)
            isVisibleWhenBlank = false
        }.also { children.add(it) }
    }

    /**
     * Разметка времени релеватного сообщения.
     */
    var timeLayout: TextLayout? = null
        private set

    private val lazyTimeLayout by lazy(LazyThreadSafetyMode.NONE) {
        TextLayout(if (isViewed) theme_timePaint else theme_unviewedTimePaint) {
            isVisibleWhenBlank = false
        }.also { children.add(it) }
    }

    /**
     * Разметка даты релеватного сообщения.
     */
    val dateLayout = TextLayout(theme_timePaint) {
        isVisibleWhenBlank = false
    }.also { children.add(it) }

    var data: ConversationItemTitleData? = null
        set(value) {
            val isChanged = value != field
            field = value
            if (isChanged) {
                configureLayout()
                parent.safeRequestLayout()
            }
        }

    var formattedDateTime: FormattedDateTime? = null
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) { configureDateTime() }
        }

    var isViewed: Boolean = true
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                timeLayout?.configure {
                    paint = if (value) theme_timePaint else theme_unviewedTimePaint
                }
                dateLayout.configure {
                    paint = if (value) theme_timePaint else theme_unviewedTimePaint
                }
            }
        }

    val width: Int
        get() = measuredWidth

    /**
     * Получить высоту разметки заголовка.
     * Точное значение размера известно только после измерения.
     */
    val height: Int
        get() = measuredHeight

    private fun configureLayout() {
        configureTitle()
        configureTitlePostfix()
        configureIsInMyCompany()
        configureUnreadStatus()
        configureDateTime()
    }

    private fun configureTitle() {
        val data = data ?: return
        titleLayout.configure {
            text = data.title
            paint = if (data.isAuthorBlocked && !data.isChatRegistryView) {
                theme_authorBlockTitlePaint
            } else {
                theme_titlePaint
            }
            highlights = data.titleHighlights
        }
        titleLayout.updatePadding(
            end = if (data.isChatRegistryView) CommunicatorTheme.offsetS else 0
        )
    }

    private fun configureTitlePostfix() {
        val data = data ?: return
        val showLayout = !data.isChatRegistryView
        titlePostfixLayout = if (showLayout) lazyTitlePostfixLayout else titlePostfixLayout
        titlePostfixLayout?.configure {
            text = data.titlePostfix.orEmpty()
            paint = if (data.isAuthorBlocked) theme_authorBlockTitlePaint else theme_titlePaint
            isVisible = showLayout
        }
    }

    private fun configureIsInMyCompany() {
        val data = data ?: return
        val isIconVisible = !data.isChatRegistryView && data.isInMyCompany
        inMyCompanyIconLayout = if (isIconVisible) lazyInMyCompanyIconLayout else inMyCompanyIconLayout
        inMyCompanyIconLayout?.configure { isVisible = isIconVisible }
    }

    private fun configureUnreadStatus() {
        val data = data ?: return
        val unreadIconType = data.unreadIconType

        if (clockDrawable != null) {
            lazyClockDrawable.setVisible(false, false)
            clockDrawable = null
        }

        val showUnreadStatus = unreadIconType != null && !data.isChatRegistryView
        unreadStatusIconLayout = if (showUnreadStatus) lazyUnreadStatusIconLayout else unreadStatusIconLayout
        unreadStatusIconLayout?.configure { isVisible = showUnreadStatus }
        if (!showUnreadStatus) return

        val (iconText, paint) = when (unreadIconType!!) {
            UNREAD -> theme_unreadIconText to theme_unreadIconPaint
            ERROR -> theme_errorSendingIconText to theme_errorIconPaint
            DRAFT -> theme_draftIconText to theme_draftIconPaint
            SENDING -> theme_sendingIconText to theme_unreadIconPaint.also {
                // Фиктивно, исключительно для автотестов, вместо иконки отображается clockDrawable.
                unreadStatusIconLayout?.configure { isVisible = false }
                lazyClockDrawable.setVisible(true, false)
                clockDrawable = lazyClockDrawable
            }
        }
        unreadStatusIconLayout?.configure {
            this.paint = paint
            text = iconText
        }
    }

    private fun configureDateTime() {
        val data = data ?: return
        val showTime = !data.formattedDateTime?.time.isNullOrBlank()
        timeLayout = if (showTime) lazyTimeLayout else timeLayout
        timeLayout?.configure { text = data.formattedDateTime?.time.orEmpty() }

        dateLayout.configure { text = if (showTime) StringUtils.EMPTY else data.formattedDateTime?.date.orEmpty() }
        dateLayout.updatePadding(
            end = if (timeLayout?.isVisible == true) CommunicatorTheme.offset2XS else 0
        )
    }

    /**
     * Измерить разметку.
     */
    fun measure(availableWidth: Int) {
        val data = data ?: return

        titleLayout.configure {
            if (data.isChatRegistryView) {
                // Для реестра каналов title размещается, как MATCH_PARENT
                layoutWidth = availableWidth - (timeLayout?.width ?: 0) - dateLayout.width
            } else {
                // Для реестра диалогов title размещается, как WRAP_CONTENT c maxWidth
                val clockWidth = clockDrawable?.let {
                    it.intrinsicWidth + CommunicatorTheme.offset2XS
                } ?: 0
                val otherContentWidth =
                    clockWidth.plus(titlePostfixLayout?.width ?: 0)
                        .plus(inMyCompanyIconLayout?.width ?: 0)
                        .plus(unreadStatusIconLayout?.width ?: 0)
                        .plus(dateLayout.width)
                        .plus(timeLayout?.width ?: 0)
                maxWidth = availableWidth - otherContentWidth
                layoutWidth = null
            }
        }

        measuredWidth = availableWidth
        measuredHeight = titleLayout.height
            .takeIf { it > 0 }
            ?: titleLayout.textPaint.textHeight
    }

    /**
     * Разместить разметку по левой [left] и верхней [top] позициям, которые задет родитель.
     */
    fun layout(left: Int, top: Int) {
        titleLayout.layout(left, top)
        val titleBaseLine = titleLayout.let { it.top + it.baseline }
        val timeLeft = left + measuredWidth - (timeLayout?.width ?: 0)
        val timeTop = titleBaseLine - (timeLayout?.baseline ?: dateLayout.baseline)
        timeLayout?.layout(
            timeLeft,
            titleBaseLine - timeLayout!!.baseline
        )
        dateLayout.layout(
            timeLeft - dateLayout.width,
            timeTop
        )
        if (data?.isChatRegistryView == true) return

        if (clockDrawable != null) {
            clockDrawable!!.let {
                val clockLeft = dateLayout.left - it.intrinsicWidth - CommunicatorTheme.offset2XS
                val clockTop = titleBaseLine - it.baseline
                it.setBounds(
                    clockLeft,
                    clockTop,
                    clockLeft + it.intrinsicWidth,
                    clockTop + it.intrinsicHeight
                )
            }
        } else {
            unreadStatusIconLayout?.layout(
                dateLayout.left - unreadStatusIconLayout!!.width,
                titleBaseLine - unreadStatusIconLayout!!.baseline
            )
        }

        titlePostfixLayout?.layout(
            titleLayout.right,
            titleBaseLine - titlePostfixLayout!!.baseline
        )
        inMyCompanyIconLayout?.layout(
            titlePostfixLayout?.right ?: titleLayout.right,
            titleBaseLine - inMyCompanyIconLayout!!.baseline
        )
    }

    /**
     * Нарисовать разметку области заголовка ячейки диалогов/каналов [ConversationItemTitleLayout].
     *
     * @param canvas canvas родительской view, в которой будет рисоваться разметка.
     */
    fun draw(canvas: Canvas) {
        children.forEach { it.draw(canvas) }
        clockDrawable?.draw(canvas)
    }

    /**
     * Проверить [drawable] на необходимость перерисовки.
     */
    fun verifyDrawable(drawable: Drawable) =
        drawable == clockDrawable
}

/**
 * Данные шапки ячейки реестра диалогов/каналов.
 *
 * Общие параметры:
 * @property title название диалога/канала.
 * @property formattedDateTime модель даты и времени релевантного сообщения.
 * @property isChatRegistryView true, если заголовок для ячейки реестра каналов.
 * @property titleHighlights модель для выделения текста заголовка при поиске.
 *
 * Параметры для заголовков диалогов:
 * @property isInMyCompany true, если релевантное сообщение является входящим по диалогу
 * от сотрудника компании текущего пользователя.
 * @property titlePostfix постфикс с количеством участников диалога.
 * @property unreadIconType тип иконки статуса непрочитанности для диалога.
 * @property isAuthorBlocked заблокирован ли пользователь, который отображается в заголовке.
 */
internal data class ConversationItemTitleData(
    val title: String,
    val formattedDateTime: FormattedDateTime?,
    val isChatRegistryView: Boolean,
    val titleHighlights: TextHighlights? = null,
    val isInMyCompany: Boolean = false,
    val titlePostfix: String? = null,
    val unreadIconType: ConversationUnreadIconType? = null,
    val isAuthorBlocked: Boolean = false
)