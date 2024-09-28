package ru.tensor.sbis.communicator.core.views.conversation_views.base

import android.content.Context
import android.graphics.Canvas
import android.widget.LinearLayout
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.communicator.common.data.theme.ConversationButton
import ru.tensor.sbis.communicator.common.themes_registry.DialogListActionsListener
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_iAmAuthorPaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_iAmAuthorText
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_messagePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_serviceMessagePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_serviceTypePaint
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeExactlySpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeMeasure
import ru.tensor.sbis.richtext.view.RichTextView
import kotlin.math.max
import kotlin.math.min

/**
 * Layout для отображения области контента в ячейке реестра диалогов/каналов.
 * Содержит разметку текста сообщения, сервисные сообщения, события социальных сетей,
 * а также вложения, и счетчик количества непрочитанных сообщения.
 *
 * Схема контентной области для реестра каналов:
 * <.....................[layoutWidth]........................>
 * [messageLayout] [unreadCountLeftPadding] [unreadCountLayout]
 *
 * Схема контентной области для реестра диалогов:
 * <..................................[layoutWidth].....................................>
 * [documentLayout].........................|[unreadCountLeftPadding] [unreadCountLayout]
 * [dialogTitleLayout]......................|............................................
 * [iAmAuthorLayout]|....[messageLayout]....|............................................
 * .................|..[serviceTypeLayout]..|............................................
 * .................|[socnetThirdLineLayout]|............................................
 * .................|[attachmentsTopPadding]|............................................
 * .................|...[attachmentsView]...|............................................
 *
 * Общие параметры диалогов/каналов:
 * @property layoutWidth ширина разметки контентной области.
 * @property messageText текст релевантного сообщения.
 * @param formattedUnreadCount строка со счетчиком непрочитанных сообщений.
 * @param isUnreadCountGray true, если счетчик непрочитанных должен быть серым.
 *
 * Параметры для контента диалогов:
 * @property isChatRegistryView передать false, если контент для реестра диалогов.
 * @property isChat true, если контент для канала в реестре диалогов.
 * @property messageHighlights модель для выделения текста сообщения при поиске.
 * @property isImSender true, если текущий пользователь является отправителем сообщения.
 * @property serviceText текст сервисного сообщения.
 * @property isSocnetEvent true, если сообщения является событием социальной сети.
 * @property documentIconText текст иконки документа, к которому привязан диалог.
 * @property documentName текст названия документа, к которому привязан диалог.
 * @property documentHighlights модель для выделения текста названия документа при поиске.
 * @property attachmentsView view списка вложений релевантного сообщения диалога.
 * @property richText вью для отображения текста уведомлений.
 *
 * @author vv.chekurda
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class ConversationItemContentLayout(
    private val context: Context,
    private val layoutWidth: Int,
    private val messageText: CharSequence?,
    formattedUnreadCount: String?,
    isUnreadCountGray: Boolean,
    private val dialogTitle: String? = null,
    private val isChatRegistryView: Boolean = true,
    private val isChat: Boolean = true,
    private val messageHighlights: TextHighlights? = null,
    private val isImSender: Boolean = false,
    private val serviceText: CharSequence? = null,
    private val isInviteInGroup: Boolean = false,
    private val isSocnetEvent: Boolean = false,
    private val documentIconText: String? = null,
    private val documentName: CharSequence? = null,
    private val documentHighlights: TextHighlights? = null,
    private val dialogNameHighlights: TextHighlights? = null,
    private val attachmentsView: AttachmentsView? = null,
    private val buttonGroup: LinearLayout? = null,
    private val buttonsData: List<ConversationButton>? = null,
    private val dialogActionsListener: DialogListActionsListener? = null,
    private val isViewForSharing: Boolean = false,
    private val richText: RichTextView? = null,
    private val unreadCountLayout: UnreadCountLayout
) {

    /**
     * Список дочерних разметок.
     */
    private val children = mutableListOf<TextLayout>()

    /** Разметка текста сообщения. */
    val messageLayout = TextLayout(theme_messagePaint) {
        isVisibleWhenBlank = false
    }.also(children::add)

    /** Разметка текста "Я:" - признака исходящих сообщений по диалогу. */
    var iAmAuthorLayout = TextLayout(theme_iAmAuthorPaint) {
        text = theme_iAmAuthorText
        padding = TextLayout.TextLayoutPadding(end = CommunicatorTheme.offset3XS)
        isVisible = false
    }.also(children::add)

    /** Разметка текста сервисного сообщения. */
    var serviceTypeLayout = TextLayout(theme_serviceTypePaint) {
        isVisibleWhenBlank = false
    }.also(children::add)

    /**
     * Разметка дополнительной третьей строки текста для отображения контента событий социальных сетей.
     * Строчка может понадобиться для названия источника, откуда пришло событие,
     * например, название новости, из которой пришла реакция на комментарий,
     * при этом [messageLayout] и [serviceTypeLayout] будут заняты другим однострочным текстом.
     */
    var socnetThirdLineLayout = TextLayout(theme_serviceMessagePaint) {
        isVisibleWhenBlank = false
    }.also(children::add)

    /** Левый padding счетчика непрочитанных [unreadCountLayout]. */
    private val unreadCountLeftPadding = CommunicatorTheme.offsetM

    /**
     * Разметка строки с иконкой и названием документа, к которому привязан диалог.
     * @see ConversationDocumentLayout
     */
    var documentLayout: ConversationDocumentLayout? = null
        private set

    /**
     * Разметка строки с иконкой и названием диалога, к которому привязан диалог.
     * @see ConversationDocumentLayout
     */
    var dialogTitleLayout: ConversationDialogTitleLayout? = null
        private set

    /**
     * Разметка кнопок.
     * @see ConversationItemButtonsLayout
     */
    var buttonsLayout: ConversationItemButtonsLayout? = null

    /** Доступная ширина текста сообщения [messageLayout]. */
    private var messageAvailableWidth = 0

    /**
     * Высота разметки области контента.
     * Точное значение будет известно сразу после создания объекта.
     */
    var height: Int = 0

    init {
        unreadCountLayout.setData(formattedUnreadCount, isUnreadCountGray)

        if (isChatRegistryView) {
            buildChannelRegistryContent()
        } else {
            buildDialogRegistryContent()
        }
        measureLayout()
    }

    /**
     * Создать контент для ячейки реестра каналов.
     * @see ConversationItemContentLayout
     */
    private fun buildChannelRegistryContent() {
        val messageLayoutWidth = layoutWidth - (unreadCountLayout.takeIf { it.isVisible }
            ?.let { it.width + unreadCountLeftPadding } ?: 0)
        messageLayout.configure {
            text = messageText ?: StringUtils.EMPTY
            layoutWidth = messageLayoutWidth
            maxLines = CONVERSATION_MESSAGE_MAX_LINES
            highlights = messageHighlights
        }
    }

    /**
     * Создать контент для ячейки реестра диалогов.
     * @see ConversationItemContentLayout
     */
    private fun buildDialogRegistryContent() {
        iAmAuthorLayout.configure {
            isVisible = isImSender
        }

        val titleAvailableWidth = layoutWidth - (unreadCountLayout.takeIf { it.isVisible }
            ?.let { unreadCountLeftPadding + it.width } ?: 0)
        if (!dialogTitle.isNullOrBlank()) {
            dialogTitleLayout = ConversationDialogTitleLayout(
                titleAvailableWidth,
                dialogTitle,
                documentIconText,
                dialogNameHighlights
            )
        }
        // По-хорошему тут должен быть if else, но за день до релиза страшно модифицировать.
        if (!isSocnetEvent && !documentName.isNullOrBlank() && !documentIconText.isNullOrBlank()) {
            documentLayout = ConversationDocumentLayout(
                titleAvailableWidth,
                documentIconText,
                documentName,
                documentHighlights,
                isChat
            )
        }

        if (buttonGroup != null && !buttonsData.isNullOrEmpty() && !isViewForSharing) {
            buttonsLayout = ConversationItemButtonsLayout(buttonGroup, dialogActionsListener!!)
            buttonsLayout!!.data = buttonsData
        }

        buildDialogMessageContentLayouts()
    }

    /**
     * Создать один из типов разметки контента сообщения реестра диалогов.
     * @see buildSimpleDialogMessageLayout
     * @see buildServiceMessageLayout
     * @see buildSocnetMessage
     */
    private fun buildDialogMessageContentLayouts() {
        val secondLineAvailableWidth = layoutWidth - iAmAuthorLayout.width
        messageAvailableWidth = secondLineAvailableWidth - (unreadCountLayout.takeIf { it.isVisible }
            ?.let { unreadCountLeftPadding + it.width } ?: 0)
        val socnetLines = if (isSocnetEvent) {
            splitToSocnetLines(messageText!!)
        } else {
            emptyList()
        }
        val isSingleLineMessage = socnetLines.size >= 2 || !documentName.isNullOrBlank() ||
            !messageText.isNullOrBlank() && !serviceText.isNullOrBlank() ||
            attachmentsView != null
        val messageLinesCount = if (isSingleLineMessage) 1 else CONVERSATION_MESSAGE_MAX_LINES
        when {
            richText != null -> richText.safeMeasure(makeExactlySpec(messageAvailableWidth), makeUnspecifiedSpec())
            isSocnetEvent -> {
                val socnetMessageLinesCount = if (isInviteInGroup) Int.MAX_VALUE else messageLinesCount
                buildSocnetMessage(socnetLines, messageAvailableWidth, secondLineAvailableWidth, socnetMessageLinesCount)
            }
            else -> {
                if (!messageText.isNullOrBlank()) buildSimpleDialogMessageLayout(messageText, messageAvailableWidth, messageLinesCount)
                if (!serviceText.isNullOrBlank()) buildServiceMessageLayout(serviceText, messageAvailableWidth, messageLinesCount)
            }
        }
    }

    /**
     * Создать разметку для сообщения по обычному диалогу.
     *
     * @param messageText текст сообщения.
     * @param availableWidth доступная ширина текста, учитывающая наличие счетчика непрочитанных,
     * и признака исходящего сообщения "Я:".
     * @param messageLinesCount доступное количество строк для текста сообщения,
     * зависит от наличия документа или вложений.
     */
    private fun buildSimpleDialogMessageLayout(
        messageText: CharSequence,
        availableWidth: Int,
        messageLinesCount: Int
    ) {
        messageLayout.configure {
            text = messageText
            layoutWidth = availableWidth
            maxLines = messageLinesCount
            highlights = messageHighlights
        }
    }

    /**
     * Создать разметку для сервисного сообщения по диалогу.
     *
     * @param serviceText текст сервисного сообщения.
     * @param availableWidth доступная ширина текста, учитывающая наличие счетчика непрочитанных,
     * и признака исходящего сообщения "Я:".
     * @param messageLinesCount доступное количество строк для текста сообщения,
     * зависит от наличия документа или вложений.
     */
    private fun buildServiceMessageLayout(
        serviceText: CharSequence,
        availableWidth: Int,
        messageLinesCount: Int
    ) {
        serviceTypeLayout.configure {
            text = serviceText
            layoutWidth = availableWidth
            maxLines = messageLinesCount
        }
    }

    /**
     * Создать разметку для сообщения по событиям социальной сети.
     *
     * @param socnetLines список строк для построчного отображения.
     * @param firstLineAvailableWidth доступная ширина для первой строки, учитывающая наличие счетчика непрочитанных
     * и признака исходящего сообщения "Я:".
     * @param secondLineAvailableWidth доступная для второй и третьей строки,
     * учитывающая только признак исходящего сообщения "Я:".
     * @param messageLinesCount доступное количество строк для текста сообщения,
     * зависит от наличия документа и вложений.
     */
    private fun buildSocnetMessage(
        socnetLines: List<CharSequence>,
        firstLineAvailableWidth: Int,
        secondLineAvailableWidth: Int,
        messageLinesCount: Int
    ) {
        messageLayout.configure {
            text = socnetLines[0]
            layoutWidth = firstLineAvailableWidth
            maxLines = messageLinesCount
            highlights = messageHighlights
        }

        if (socnetLines.size > 1) {
            serviceTypeLayout.configure {
                text = socnetLines[1]
                layoutWidth = secondLineAvailableWidth
                maxLines = messageLinesCount
            }

            if (socnetLines.size > 2) {
                socnetThirdLineLayout.configure {
                    text = socnetLines[2]
                    layoutWidth = secondLineAvailableWidth
                    maxLines = messageLinesCount
                }
            }
        }
    }

    /**
     * Разделить текст сообщения на список строк для отображения событий социальной сети.
     */
    private fun splitToSocnetLines(messageText: CharSequence, limit: Int = 3): List<CharSequence> {
        val strings = messageText.split("\n", limit = limit)
        return if (strings.size > 1) {
            val result = mutableListOf<CharSequence>()
            var index = 0
            for (i in 0 until min(limit, strings.size)) {
                result.add(messageText.subSequence(index, index + strings[i].length))
                index += strings[i].length + 1
            }
            result
        } else {
            listOf(messageText)
        }
    }

    /**
     * Померить всю разметку для определений высоты всего контента ячейки диалога/канала.
     */
    private fun measureLayout() {
        height = if (isChatRegistryView) {
            max(messageLayout.height, unreadCountLayout.height)
        } else {
            measureAttachmentsView()
            measureButtonsLayout()
            val contentHeight = (
                (dialogTitleLayout?.height ?: 0) + getMessageTextHeight() + (documentLayout?.height ?: 0) +
                    (attachmentsView?.measuredHeight ?: 0) +
                    (buttonsLayout?.height ?: 0)
                )
            max(contentHeight, unreadCountLayout.height)
        }
    }

    private fun getMessageTextHeight() =
        richText?.measuredHeight ?: (messageLayout.height + serviceTypeLayout.height + socnetThirdLineLayout.height)

    /**
     * Померить view списка вложений [attachmentsView].
     */
    fun measureAttachmentsView() {
        attachmentsView?.measure(
            makeExactlySpec(messageAvailableWidth),
            makeUnspecifiedSpec()
        )
    }

    fun measureButtonsLayout() {
        buttonsLayout?.measure(makeExactlySpec(messageAvailableWidth))
    }

    /**
     * Разместить всю разметку контента ячейки диалогов/каналов [ConversationItemContentLayout]
     * по левой [left] и верхней [top] позициям, определяемой родителем.
     */
    fun layout(left: Int, top: Int) {
        documentLayout?.layout(left, top)
        dialogTitleLayout?.layout(left, top)
        val authorAndMessageTop = documentLayout?.documentNameLayout?.bottom ?: (dialogTitleLayout?.dialogTitleLayout?.bottom ?: top)
        if (richText != null) {
            richText.safeLayout(left, authorAndMessageTop)
        } else {
            iAmAuthorLayout.layout(left, authorAndMessageTop)
            messageLayout.layout(iAmAuthorLayout.right, authorAndMessageTop)
            serviceTypeLayout.layout(messageLayout.left, messageLayout.bottom)
            socnetThirdLineLayout.layout(messageLayout.left, serviceTypeLayout.bottom)
            layoutAttachmentsView()
            val buttonsLayoutTop = attachmentsView?.bottom ?: socnetThirdLineLayout.bottom
            buttonsLayout?.layout(left + layoutWidth - buttonsLayout!!.width, buttonsLayoutTop)
        }
        unreadCountLayout.takeIf { it.isVisible }?.let {
            val unreadCountToBaseLine = when {
                messageLayout.isVisible -> messageLayout.baseline
                serviceTypeLayout.isVisible -> serviceTypeLayout.baseline
                else -> 0
            }
            // Если отстутствует текст сообщений, но присутствует вложение - выравниваем по верхней границе вложений
            val unreadCountTop =
                if (unreadCountToBaseLine == 0 && attachmentsView != null) {
                    socnetThirdLineLayout.bottom
                } else {
                    top
                }
            it.layout(
                left + layoutWidth - it.width,
                unreadCountTop,
                unreadCountToBaseLine
            )
        }
    }

    /**
     * Разместить view списка вложений [attachmentsView],
     * с учетом позиций, переданных ранее родителем в [layout].
     */
    fun layoutAttachmentsView() {
        attachmentsView?.let {
            it.layout(
                messageLayout.left,
                socnetThirdLineLayout.bottom,
                messageLayout.left + it.measuredWidth,
                socnetThirdLineLayout.bottom + it.measuredHeight
            )
        }
    }

    /**
     * Нарисовать разметку области контента ячейки диалогов/каналов [ConversationItemContentLayout].
     *
     * @param canvas canvas родительской view, в которой будет рисоваться разметка.
     */
    fun draw(canvas: Canvas) {
        children.forEach { it.draw(canvas) }
        dialogTitleLayout?.draw(canvas)
        unreadCountLayout.draw(canvas)
        documentLayout?.draw(canvas)
    }
}

/** Максимальное количество строк для отображения обычного сообщения. */
private const val CONVERSATION_MESSAGE_MAX_LINES = 2

/**
 * Данные области контента ячейки реестра диалогов/каналов.
 */
internal sealed interface ConversationItemContentData {

    /**
     * Текст сообщения.
     */
    val messageText: CharSequence?

    /**
     * Счетчик непрочитанных после форматирования.
     */
    val formattedUnreadCount: String?

    /**
     * Модель для выделения текста сообщения при поиске.
     */
    val messageHighlights: TextHighlights?

    /**
     * Данные области контента ячеек реестра диалогов.
     *
     * @property isChat true, если контент для канала в реестре диалогов.
     * @property isImSender true, если текущий пользователь является отправителем сообщения.
     * @property serviceText текст сервисного сообщения.
     * @property isSocnetEvent true, если сообщения является событием социальной сети.
     * @property dialogName тема диалога.
     * @property dialogNameHighlights модель для выделения текста темы диалога при поиске.
     * @property documentIconText текст иконки документа, к которому привязан диалог.
     * @property documentName текст названия документа, к которому привязан диалог.
     * @property documentHighlights модель для выделения текста названия документа при поиске.
     * @property isInviteInGroup true, если контент - приглашение в группу.
     * @property isSharing true, если ячейка отображается в режиме шаринга.
     * @property buttonsData список кнопок ячейки.
     */
    data class DialogRegistryItemContentData(
        override val messageText: CharSequence?,
        override val formattedUnreadCount: String?,
        override val messageHighlights: TextHighlights? = null,
        val isChat: Boolean = true,
        val isImSender: Boolean = false,
        val serviceText: CharSequence? = null,
        val isSocnetEvent: Boolean = false,
        val dialogName: String? = null,
        val dialogNameHighlights: TextHighlights? = null,
        val documentIconText: String? = null,
        val documentName: CharSequence? = null,
        val documentHighlights: TextHighlights? = null,
        val isSharing: Boolean = false,
        val isInviteInGroup: Boolean = false,
        val buttonsData: List<ConversationButton>? = null
    ) : ConversationItemContentData

    /**
     * Данные области контента ячеек реестра каналов.
     *
     * @property isUnreadCountGray true, если счетчик непрочитанных должен быть серым.
     */
    data class ChannelRegistryItemContentData(
        override val messageText: CharSequence?,
        override val messageHighlights: TextHighlights? = null,
        override val formattedUnreadCount: String?,
        val isUnreadCountGray: Boolean
    ) : ConversationItemContentData
}
