package ru.tensor.sbis.communicator.core.views.conversation_views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.tracing.trace
import org.json.JSONObject
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsView
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.themes_registry.DialogListActionsListener
import ru.tensor.sbis.communicator.common.util.json.stringify
import ru.tensor.sbis.communicator.common.view.CheckBoxLayout
import ru.tensor.sbis.communicator.core.R
import ru.tensor.sbis.communicator.core.views.conversation_views.base.ConversationItemContentLayout
import ru.tensor.sbis.communicator.core.views.conversation_views.base.ConversationItemTitleData
import ru.tensor.sbis.communicator.core.views.conversation_views.base.ConversationItemTitleLayout
import ru.tensor.sbis.communicator.core.views.conversation_views.base.UnreadCountLayout
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_itemSeparatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme.theme_messagePaint
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.ConversationItemsViewPool
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.RelevantMessageType
import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.TextHighlights
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.safeLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.profile.personcollage.PersonCollageView
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.richtext.view.RichTextView
import kotlin.math.max
import ru.tensor.sbis.design.R as RDesign

/**
 * View ячейки списка реестра диалогов/каналов для отображения переписки.
 * http://axure.tensor.ru/CommunicatorMobile/%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D1%8F.html#OnLoadVariable=%D0%BE%D1%87%D0%B8%D1%81%D1%82%D0%B8%D1%82%D1%8C%20%D0%BF%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D1%8B%D0%B5&Dialog=%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D1%8F&CSUM=1
 *
 * @author vv.chekurda
 */
@SuppressLint("ViewConstructor")
class ConversationItemView(
    context: Context,
    attrs: AttributeSet? = null,
    private val viewPool: ConversationItemsViewPool
) : ViewGroup(context) {

    init {
        CommunicatorTheme.createThemeItemsResources(context)
    }

    /** Данные ячейки диалогов/каналов. */
    private var data: ConversationModel? = null

    /**
     * Разметка заголовка.
     * @see ConversationItemTitleLayout
     */
    private val titleLayout = ConversationItemTitleLayout(this)

    /** Позиция левого края разметки заголовка [titleLayout]. */
    private var titleLeft = 0

    /**
     * Разметка контента.
     * Имеет ту же позицию левого края, что и заголовок.
     * @see ConversationItemContentLayout
     */
    private var contentLayout: ConversationItemContentLayout? = null

    private val unreadCountLayout = UnreadCountLayout()

    /**
     * View иконки закрепленного канала.
     * @see PinnedIconView
     */
    private var pinnedIconView: PinnedIconView? = null

    /**
     * Разметка чекбокса.
     * @see CheckBoxLayout
     * Используется при массовом выделении ячеек.
     */
    private var checkBoxLayout: CheckBoxLayout? = null

    /** Состояние активированности чекбокса [checkBoxLayout] */
    private var isChecked: Boolean = false

    /**
     * Ширина разметки чекбокса [checkBoxLayout].
     * Вернет размер отличный от нуля в случае,
     * если активированности мода отображения множественного выбора [isCheckModeEnabled].
     */
    private val checkBoxLayoutWidth: Int
        get() = checkBoxLayout?.width ?: 0

    /** Левый padding [ConversationItemView]. */
    private val leftPad = CommunicatorTheme.offset2XS

    /** Правый padding [ConversationItemView]. */
    private val rightPad = CommunicatorTheme.offsetM

    /** Верхний padding [ConversationItemView]. */
    private val topPad = CommunicatorTheme.offsetXS

    /** Нижний padding [ConversationItemView]. */
    private val bottomPad = CommunicatorTheme.offsetM

    /** Правый отступ от коллажа [collageLayout] до заголовка [titleLayout] и контента [contentLayout]. */
    private val collageRightPadding = CommunicatorTheme.offsetS

    /** Отступ правого края иконки закрепленного канала [pinnedIconView] от правого края коллажа [collageLayout]. */
    private val pinnedIconViewOffset = CommunicatorTheme.offset3XS

    /**
     * Ширина скрытого коллажа [collageLayout].
     * Равна ширине PersonView из поисковой строки для выравнинваия контента во время поиска.
     */
    private val collageGoneWidth = dp(CONVERSATION_COLLAGE_GONE_WIDTH_DP)

    /**
     * Ожидаемая ширина view коллажа [collageLayout], учитывающая видимость.
     * Необходима для расчета левого отступа заголовка [titleLeft] без лишних перерасчетов view коллажа.
     *
     * Уменьшенный размер коллажа при скрытии приводит к выравниванию контента по правой границе
     * аватарки из строки поиска реестра.
     */
    private val collageExpectedWidth: Int
        get() = if (collageLayout.isVisible) collageLayout.measuredWidth else collageGoneWidth

    /**
     * Высота view коллажа [collageLayout], учитывающая видимость.
     * Необходима для расчета высоты ячейки [layoutHeight] без лишних перерасчетов view коллажа.
     */
    private val collageHeight: Int
        get() = if (collageLayout.isVisible) collageLayout.measuredHeight else 0

    /**
     * Значение видимости коллажа, которое необходимо поддерживать на [collageLayout].
     * Коллаж скрывается при активации поиска по контакту в реестре диалогов.
     */
    private var isCollageVisible: Boolean = true

    /** Позиция левой границы коллажа [collageLayout]. */
    private var collageLeft = 0

    /** Позиция верхней границы коллажа [collageLayout]. */
    private var collageTop = topPad + CommunicatorTheme.offset3XS

    /**
     * Состояние измеренности текущей кастомной разметки, участвующей в [buildStaticLayout].
     * Поле необходимо для избежания повторных пересозданий статичных разметок, если данные не изменялись.
     * P.S. при изменении конфигурации будет круг жизненного цикла и данные также будут перепривязаны.
     */
    private var isStaticLayoutMeasured = false

    /** Высота всей текущей разметки ячейки для установки в [onMeasure]. */
    private val layoutHeight: Int
        get() = topPad + max((titleLayout.height) + (contentLayout?.height ?: 0), collageHeight) + bottomPad

    /** View списка вложений ячейки диалога. */
    var attachmentsView: AttachmentsView? = null
        private set

    /** View богатого текста ячейки уведомления в реестре диалогов. */
    var richText: RichTextView? = null
        private set

    var buttonGroup: LinearLayout? = null
        private set

    /** Текущий контент [attachmentsView] */
    private var attachmentsContent: List<AttachmentRegisterModel>? = null

    /** Была ли переизмерена view списка вложений [attachmentsView]. */
    private var isAttachmentsRemeasured = false

    /** Хелпер для доступа автотестов к дереву элементов во вью */
    private val autoTestsAccessHelper by lazy { AutoTestsAccessHelper() }

    private val searchColor = CommunicatorTheme.textBackgroundColorDecoratorHighlight

    private val dividerRect = Rect()
    private val dividerHeight = dp(0.5f)
    private val isViewSelectedChannelInSharing
        get() = dialogActionsListener == null
    private val needShowDivider
        get() = !isSharingMode || !isViewSelectedChannelInSharing

    private val useRichText: Boolean
        get() = data?.isNotice == true && data?.messageText != null

    /**
     * Слушатель действий над ячейков диалога/канала.
     */
    var dialogActionsListener: DialogListActionsListener? = null

    /**
     * Признак включенности режима отображения ячеек для шаринга в диалоги/каналы.
     */
    var isSharingMode: Boolean = false

    /** View коллажа с участниками диалога/канала. */
    val collageLayout = PersonCollageView(context).also {
        it.id = R.id.themes_registry_conversation_item_person_view_photo_id
        it.isClickable = true
        it.isFocusable = true
        it.touchscreenBlocksFocus = false
        it.setSize(PhotoSize.M)
    }

    /**
     * Состояние включенности мода отображения ячейки для множественного выбора.
     * При изменении вызовет перестроение ячейки под отображение с или без чекбокса [checkBoxLayout].
     */
    var isCheckModeEnabled = false
        set(value) {
            if (value != field) {
                field = value
                requestLayout()
            }
        }

    init {
        setWillNotDraw(false)
        isFocusable = true
        isClickable = true
        background = ContextCompat.getDrawable(context, RDesign.drawable.selectable_item_bg_white)
        addView(collageLayout)
        setupForAutoTests()
        if (isInEditMode) showPreview()
    }

    /**
     * Привязать данные [ConversationModel] к ячейке.
     * Начинает процесс обновления view.
     */
    fun bind(data: ConversationModel) {
        updateData(data)
        updateViews()
    }

    /**
     * Обновить данные [data].
     * Также: устанавливает список участников во вью коллажа [collageLayout].
     */
    private fun updateData(data: ConversationModel) {
        // После обновления данных необходимо пересоздание статичной разметки
        isStaticLayoutMeasured = false
        val lastCollageData = this.data?.participantsCollage
        this.data = data

        configureTitle(data)

        // Для чатов коллаж отображается всегда, независимо от внешнего воздействия
        if (data.isChatForView) isCollageVisible = true
        if (isInEditMode || lastCollageData == data.participantsCollage) return
        collageLayout.setDataList(data.participantsCollage)
    }

    /**
     * Обновить все view представления ячейки.
     * Если ячейка переиспользуется, то обновим ее самостоятельно без использования [requestLayout],
     * в противном случае пройдем полный цикл.
     */
    private fun updateViews() {
        collageLayout.isVisible = isCollageVisible
        updatePinnedView()
        updateAttachmentsView()
        updateSbisButtonGroupView()
        updateRichText()

        safeRequestLayout()
    }

    /**
     * Обновить view закрепленного канала [pinnedIconView].
     * Иконка отображается только для закрепленных чатов реестра каналов.
     */
    private fun updatePinnedView() = with(requireData()) {
        val showPinnedView = isChatForView && isPinned
        if (showPinnedView && pinnedIconView == null) {
            pinnedIconView = PinnedIconView(context).also(::addView)
        }
        // Видимость влияет на участие в onMeasure и onLayout
        pinnedIconView?.isVisible = showPinnedView
    }

    /**
     * Обновить view кнопок.
     * Кнопки отображаются только для приглашений в группу.
     */
    private fun updateSbisButtonGroupView() = with(requireData()) {
        val isGroupInvitation = !conversationButtons.isNullOrEmpty()
        if (isGroupInvitation && buttonGroup == null) {
            buttonGroup = LinearLayout(context).also(::addView)
        }
        // Видимость влияет на участие в onMeasure и onLayout
        buttonGroup?.isVisible = isGroupInvitation
    }

    /**
     * Обновить view списка вложений [attachmentsView].
     * Представление отображается только для диалогов с вложениями.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun updateAttachmentsView() = with(requireData()) {
        val showAttachmentsView = !isChatForView && !attachmentsRegistryModels.isNullOrEmpty()
        if (showAttachmentsView) {
            var isNewView = false
            if (attachmentsView == null) {
                isNewView = true
                attachmentsView = viewPool.attachmentsView.also {
                    addView(it)
                    it.id = R.id.themes_registry_conversation_item_attachments_id
                    it.updatePadding(top = CommunicatorTheme.offsetXS)
                    it.children.find { child -> child is RecyclerView }?.also { recycler ->
                        recycler.updateLayoutParams { width = MATCH_PARENT }
                        recycler.setOnTouchListener { _, event -> onTouchEvent(event) }
                    }
                    it.isVisible = true
                }
            }
            if (isNewView || attachmentsRegistryModels != attachmentsContent) {
                attachmentsView!!.submitList(attachmentsRegistryModels)
            }
        } else {
            attachmentsView?.let { recycleAttachmentsView() }
        }
        attachmentsContent = attachmentsRegistryModels
    }

    private fun updateRichText() = with(requireData()) {
        if (useRichText) {
            if (richText == null) {
                richText = viewPool.richText.also {
                    it.id = R.id.themes_registry_conversation_item_notice_view_id
                    it.ellipsize = TextUtils.TruncateAt.END
                    val color = ColorUtils.setAlphaComponent(
                        theme_messagePaint.color,
                        theme_messagePaint.alpha
                    )
                    it.setTextColor(color)
                    it.setText(messageText, TextView.BufferType.NORMAL)
                    it.isVisible = true
                    it.maxLines = NOTIFICATION_TEXT_MAX_LINES
                }
                addView(richText)
            }
            richText?.setText(messageText, TextView.BufferType.NORMAL)
        } else {
            richText?.let { recycleRichText() }
        }
    }

    /**
     * Создать статичную разметку ячейки.
     * После создания всех статичных разметок будут определены позиции для onDraw.
     *
     * Позиции и размеры для статичной разметки зависят от расчетов внешних представлений,
     * поэтому корневая ячейка должна иметь рассчитанную высоту и ширину,
     * а также должны быть расчитаны остальные view компоненты, влияющие на позиции и размеры,
     * например, [collageLayout].
     *
     * Последовательность построения статичной разметки должна исходить из логики зависимости от соседних статиков.
     * Для этого необходимо сначала создать и измерить разметку, имеющую "wrap_content" или фиксированные размеры
     * с конкретными позициями относительно parent представления,
     * и только затем все статики, строящиеся по принципу "align" и "match_parent".
     */
    private fun buildStaticLayout(measuredWidth: Int) {
        buildCheckBoxStaticLayout()
        val availableWidthForData = measuredWidth - checkBoxLayoutWidth - leftPad - collageExpectedWidth -
            collageRightPadding - rightPad
        titleLayout.measure(availableWidthForData)
        buildContentStaticLayout(availableWidthForData)

        isStaticLayoutMeasured = true
        compareMeasuredHeightToUpdate()
    }

    /**
     * Создать статичную разметку чекбокса,
     * если активирован мод множественно отображения.
     */
    private fun buildCheckBoxStaticLayout() {
        checkBoxLayout = if (isCheckModeEnabled) {
            CheckBoxLayout(this)
        } else {
            null
        }
        checkBoxLayout?.isChecked = isChecked
    }

    /**
     * Настроить данные заголовка.
     * @see ConversationItemTitleLayout
     */
    private fun configureTitle(data: ConversationModel) =
        with(data) {
            val titleHighlights = TextHighlights(nameHighlights?.map { HighlightSpan(it.start, it.end) }, searchColor)
            val dateTime = formattedDateTime?.takeIf { !isChatForView || !isSharingMode }
            val isViewed = isViewed || isSharingMode
            val unreadIconType = unreadIconType.takeIf { !isSharingMode }
            val titleData = ConversationItemTitleData(
                title = title,
                formattedDateTime = dateTime,
                isChatRegistryView = isChatForView,
                titleHighlights = titleHighlights,
                unreadIconType = unreadIconType,
                titlePostfix = titlePostfix,
                isInMyCompany = isInMyCompany,
                isAuthorBlocked = isAuthorBlocked
            )

            titleLayout.data = titleData
            titleLayout.isViewed = isViewed
        }

    /**
     * Создать статичную разметку контента ячейки.
     * @see ConversationItemContentLayout
     *
     * @param layoutAvailableWidth доступная ширина для разметки контента.
     */
    private fun buildContentStaticLayout(layoutAvailableWidth: Int) = with(requireData()) {
        contentLayout = if (isChatForView) {
            ConversationItemContentLayout(
                context = this@ConversationItemView.context,
                layoutWidth = layoutAvailableWidth,
                messageText = messageText,
                formattedUnreadCount = formattedUnreadCount.takeIf { !isSharingMode },
                isUnreadCountGray = isChatUnreadCounterGray,
                dialogActionsListener = dialogActionsListener,
                unreadCountLayout = unreadCountLayout
            )
        } else {
            val messageHighlights = TextHighlights(searchHighlights?.map { HighlightSpan(it.start, it.end) }, searchColor)
            val documentHighlights = TextHighlights(docsHighlights?.map { HighlightSpan(it.start, it.end) }, searchColor)
            val dialogNameHighlights = TextHighlights(dialogNameHighlights?.map { HighlightSpan(it.start, it.end) }, searchColor)
            ConversationItemContentLayout(
                this@ConversationItemView.context,
                layoutWidth = layoutAvailableWidth,
                messageText = messageText,
                formattedUnreadCount = formattedUnreadCount.takeIf { !isSharingMode },
                isUnreadCountGray = false,
                isChatRegistryView = false,
                isChat = isChatForOperations,
                messageHighlights = messageHighlights,
                isImSender = isOutgoing,
                serviceText = serviceText,
                isInviteInGroup = socnetServiceObject?.isInviteInGroup ?: false,
                isSocnetEvent = isSocnetEvent,
                documentIconText = documentIconText,
                documentName = externalEntityTitle,
                documentHighlights = documentHighlights,
                dialogNameHighlights = dialogNameHighlights,
                attachmentsView = if (!attachments.isNullOrEmpty()) attachmentsView else null,
                dialogTitle = dialogTitle,
                buttonGroup = if (!conversationButtons.isNullOrEmpty()) buttonGroup else null,
                buttonsData = conversationButtons,
                dialogActionsListener = dialogActionsListener,
                richText = richText.takeIf { useRichText },
                unreadCountLayout = unreadCountLayout
            )
        }
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) = trace("ConversationItemView#onMeasure") {
        val measuredWidth =
            if (!isInEditMode) {
                MeasureSpec.getSize(widthMeasureSpec)
            } else {
                CONVERSATION_PREVIEW_WIDTH
            }
        // Измеряем collageLayout первым, так как ширина его представления влияет на позиции статичной разметки,
        // определяемых в buildLayout()
        measureChild(collageLayout, makeUnspecifiedSpec(), makeUnspecifiedSpec())

        if (!isStaticLayoutMeasured) {
            // Если статичная разметка еще не была создана на onBind - создаем, от нее зависит высота ячейки.
            // View вложений рассчитывается непосредственно внутри contentLayout при создании
            buildStaticLayout(measuredWidth)
        } else if (attachmentsView?.isVisible == true) {
            // Если статичная разметка уже создана, то переизмеряем вложения, учитывая видимость,
            // тк onMeasure мог быть вызван измениями дочерних представлений (бейджиков, счетчиков)
            remeasureAttachmentsView()
        }

        // После создания статичной разметки в buildLayout - можем точно определить высоту всей ячейки
        val measuredHeight = layoutHeight

        // Единожды измеряем view иконки закрепленного чата, тк это статичное представление
        if (pinnedIconView?.isVisible == true && pinnedIconView?.measuredHeight == 0) {
            measureChild(pinnedIconView, makeUnspecifiedSpec(), makeUnspecifiedSpec())
        }
        dividerRect.set(0, measuredHeight - dividerHeight, measuredWidth, measuredHeight)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    /**
     * Повторно измерить view списка вложений.
     * Измерение необходимо для обновления дочерних view, таких как бейджики подписей и типов документа.
     */
    private fun remeasureAttachmentsView() {
        contentLayout?.let {
            it.measureAttachmentsView()
            isAttachmentsRemeasured = true
        }
    }

    /**
     * Повторно разместить view списка вложений.
     * Размещение необходимо для обновления дочерних view после [remeasureAttachmentsView].
     */
    private fun relayoutAttachmentsView() {
        contentLayout?.let {
            it.layoutAttachmentsView()
            isAttachmentsRemeasured = false
        }
    }

    /**
     * Сравнить ранее расчитанную высоту ячейки с текущей.
     * В случае несовпадения - обновить.
     */
    private fun compareMeasuredHeightToUpdate() {
        if (layoutHeight != measuredHeight) {
            setMeasuredDimension(measuredWidth, layoutHeight)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) = trace("ConversationItemView#onLayout") {
        if (data == null) return@trace

        // Если layout изменился и статичная разметка не была рассчитана ранее -> создаем
        if (changed && !isStaticLayoutMeasured) buildStaticLayout(measuredWidth)

        // Если был remeasureAttachments() -> повторно размещаем.
        // В ином случае размещение уже было выполнено при создании статичной разметки контента.
        if (isAttachmentsRemeasured) relayoutAttachmentsView()

        collageLeft = checkBoxLayoutWidth + leftPad

        titleLeft = collageLeft + collageExpectedWidth + collageRightPadding
        titleLayout.layout(titleLeft, topPad)

        layoutStaticContent()
        layoutStaticCheckBox()

        val collageRight = collageLeft + collageLayout.measuredWidth
        val pinnedRight = collageRight + pinnedIconViewOffset
        pinnedIconView?.safeLayout(
            pinnedRight - pinnedIconView!!.measuredWidth,
            collageTop
        )

        collageLayout.layout(
            collageLeft,
            collageTop,
            collageLeft + collageLayout.measuredWidth,
            collageTop + collageLayout.measuredHeight
        )
    }

    /**
     * Разместить стичный чекбокс, если он был создан ранее.
     */
    private fun layoutStaticCheckBox() {
        checkBoxLayout?.let {
            val titleTextHeight = titleLayout.titleLayout.textPaint.textHeight
            val titleTextBottom = titleLayout.titleLayout.bottom
            val titleTextCenterVertical = titleTextBottom - titleTextHeight / 2
            val top = titleTextCenterVertical - it.height / 2
            it.layout(0, top)
        }
    }

    /**
     * Разместить статичный контент ячейки.
     *
     * Выравнивание происходит по левой и нижней позициям [titleLayout].
     */
    private fun layoutStaticContent() {
        val contentTop = topPad + titleLayout.height
        contentLayout!!.layout(titleLeft, contentTop)
    }

    override fun onDraw(canvas: Canvas) {
        titleLayout.draw(canvas)
        contentLayout?.draw(canvas)
        checkBoxLayout?.draw(canvas)
        if (needShowDivider) {
            canvas.drawRect(dividerRect, theme_itemSeparatorTheme)
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
        checkBoxLayout?.let {
            it.isChecked = isChecked
            if (!isLayoutRequested) requestLayout()
            invalidate()
        }
    }

    /**
     * Установить включенность мода отображения ячейки при поиске по контакту.
     * Установка должна происходить до вызова onBind холдера, иначе представление не изменится.
     *
     * @param isEnabled true, если мод активирован.
     */
    fun setSearchByContactModeEnabled(isEnabled: Boolean) {
        val previousValue = isCollageVisible
        isCollageVisible = !isEnabled
        if (previousValue != isCollageVisible) {
            collageLayout.isVisible = isCollageVisible
        }
    }

    /**
     * Обновить время и дату в заголовке.
     * Обновление вызовет пересоздание заголовка и отрисовку.
     * Рекомендуется использовать для точечного обновления через payload.
     *
     * @param formattedDateTime модель даты и времени.
     */
    fun updateDateTime(formattedDateTime: FormattedDateTime) {
        data?.formattedDateTime = formattedDateTime
        titleLayout.formattedDateTime = formattedDateTime
        titleLayout.measure(titleLayout.width)
        titleLayout.layout(titleLeft, topPad)
        invalidate()
    }

    /**
     * Очистить дочерние View для переиспользования.
     */
    private fun recycleAttachmentsView() {
        attachmentsView?.also {
            it.recycle()
            it.isVisible = false
            viewPool.recycle(it)
            attachmentsView = null
        }
    }

    private fun recycleRichText() {
        richText?.also {
            it.isVisible = false
            viewPool.recycle(it)
            richText = null
        }
    }

    /** @SelfDocumented */
    override fun hasOverlappingRendering(): Boolean = false

    /** @SelfDocumented */
    override fun verifyDrawable(who: Drawable): Boolean =
        super.verifyDrawable(who) || titleLayout.verifyDrawable(who)

    private fun requireData(): ConversationModel = data!!

    private fun setupForAutoTests() {
        // Для автоматического тестирования определяем id и accessibility text для кастомной вью
        id = R.id.themes_registry_conversation_item_view_id
        accessibilityDelegate = autoTestsAccessHelper
    }

    private inner class AutoTestsAccessHelper : AccessibilityDelegate() {

        private val layouts = mapOf(
            R.id.themes_registry_conversation_item_title_layout_id to { titleLayout.titleLayout },
            R.id.themes_registry_conversation_item_unread_status_icon_layout_id to { titleLayout.unreadStatusIconLayout },
            R.id.themes_registry_conversation_item_date_layout_id to { titleLayout.dateLayout },
            R.id.themes_registry_conversation_item_time_layout_id to { titleLayout.timeLayout },
            R.id.themes_registry_conversation_item_i_am_author_layout_id to { contentLayout?.iAmAuthorLayout },
            R.id.themes_registry_conversation_item_message_layout_id to { contentLayout?.messageLayout },
            R.id.themes_registry_conversation_item_unread_count_layout_id to { unreadCountLayout.countLayout },
            R.id.themes_registry_conversation_item_document_icon_layout_id to { contentLayout?.documentLayout?.documentIconLayout },
            R.id.themes_registry_conversation_item_document_name_layout_id to { contentLayout?.documentLayout?.documentNameLayout },
            R.id.themes_registry_conversation_item_service_type_layout_id to { contentLayout?.serviceTypeLayout },
            R.id.themes_registry_conversation_item_socnet_third_line_layout_id to { contentLayout?.socnetThirdLineLayout }
        )

        /** @SelfDocumented */
        override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            val description = mutableMapOf<String, String>()
            layouts.forEach { (layoutId, getter) ->
                getter()?.let { description[context.resources.getResourceEntryName(layoutId)] = it.text.toString() }
            }
            if (isCheckModeEnabled && checkBoxLayout != null) {
                val checkboxInfo = checkBoxLayout!!.getNodeInfo(context)
                description[checkboxInfo.first] = checkboxInfo.second
            }
            info.text = JSONObject(description as Map<*, *>).stringify()
        }
    }
}

/** Размер скрытого коллажа диалога для выравниваня по правой границе фото персоны из строки поиска при поиске по контакту в dp */
private const val CONVERSATION_COLLAGE_GONE_WIDTH_DP = 26

/** Ширина ячейки для preview в студии. */
private const val CONVERSATION_PREVIEW_WIDTH = 900

/** Максимальное количество строк отображаемого текста уведомления. */
private const val NOTIFICATION_TEXT_MAX_LINES = 2

private fun ConversationItemView.showPreview() {
    bind(
        ConversationModel(
            uuid = UUIDUtils.NIL_UUID,
            title = "Епанчин Иван",
            titlePostfix = "(+200)",
            timestamp = 1,
            favoriteTimestamp = null,
            syncStatus = SyncStatus.OUT_OF_SYNC,
            participantsCollage = arrayListOf(),
            participantsCount = 2,
            participantsUuids = arrayListOf(),
            messageUuid = UUIDUtils.NIL_UUID,
            messageType = RelevantMessageType.MESSAGE,
            null,
            "Stub",
            null,
            0,
            SpannableString("Привет, тут ошибка сборочки подъехала"),
            isOutgoing = false,
            isRead = false,
            isReadByMe = false,
            isForMe = false,
            serviceText = null,
            unreadCount = 400,
            formattedUnreadCount = "+400",
            documentUuid = null,
            documentType = null,
            documentUrl = null,
            documentIconText = resources.getString(RDesign.string.design_mobile_icon_document),
            externalEntityTitle = "Ошибка сборки",
            attachments = null,
            attachmentsRegistryModels = null,
            attachmentCount = 0,
            isChatForView = false,
            isChatForOperations = false,
            isPrivateChat = false,
            isUnreadCountImportant = false,
            isConversationHiddenOrArchived = false,
            canBeMarkedUnread = false,
            canBeMarkedRead = false,
            isSocnetEvent = false,
            socnetServiceObject = null,
            canBeDeleted = false,
            canBeUndeleted = false,
            searchHighlights = null,
            nameHighlights = null,
            docsHighlights = null,
            dialogNameHighlights = null,
            isPinned = false,
            meIsOwner = false,
            isInMyCompany = true,
            chatType = ChatType.UNKNOWN,
            canBeUnhide = false,
            isViewed = false,
            canSendMessage = true,
            isGroupConversation = true
        ).also { it.formattedDateTime = FormattedDateTime("15.12", "16:20") }
    )
}