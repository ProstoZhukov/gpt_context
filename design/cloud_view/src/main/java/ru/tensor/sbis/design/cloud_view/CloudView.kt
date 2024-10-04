package ru.tensor.sbis.design.cloud_view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.children
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.cloud_view.CloudViewStylesProvider.activateResourceCacheForRecycler
import ru.tensor.sbis.design.cloud_view.content.link.LinkClickListener
import ru.tensor.sbis.design.cloud_view.content.utils.MessageBlockTextHolder
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool
import ru.tensor.sbis.design.cloud_view.layout.CloudViewIncomeLayout
import ru.tensor.sbis.design.cloud_view.layout.CloudViewLayout
import ru.tensor.sbis.design.cloud_view.layout.CloudViewOutcomeLayout
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.model.*
import ru.tensor.sbis.design.cloud_view.utils.CloudThemeContextWrapper
import ru.tensor.sbis.design.cloud_view.utils.showPreview
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteBehavior
import ru.tensor.sbis.design.cloud_view.utils.swipe.CloudSwipeToQuoteBehaviorImpl
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.attachments.ui.view.clickhandler.AttachmentUploadActionsHandler
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessageExpandableContainer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.design.cloud_view.listener.AuthorAvatarClickListener
import ru.tensor.sbis.design.cloud_view.listener.AuthorNameClickListener
import ru.tensor.sbis.design.utils.getDimenPx
import java.util.Date

/**
 * Ячейка-облако.
 * ViewGroup поддерживает добавление дочерних View,
 * которые будут размещены внутри облачка по принципу вертикального LinearLayout.
 *
 * @author ma.kolpakov
 */
class CloudView private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int,
    private val swipeToQuoteBehavior: CloudSwipeToQuoteBehaviorImpl
) : ViewGroup(
    CloudThemeContextWrapper(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    MessageSwipeToQuoteBehavior by swipeToQuoteBehavior,
    MediaMessageExpandableContainer {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.incomeCloudViewTheme,
        @StyleRes defStyleRes: Int = R.style.DefaultCloudViewTheme_Income
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        CloudSwipeToQuoteBehaviorImpl(context)
    )

    /**
     * Разметка ячейки-облака.
     * Имеет 2 конфигурации:
     * [CloudViewOutcomeLayout] - исходящее сообщение.
     * [CloudViewIncomeLayout] - входящее сообщение.
     */
    private val cloudViewLayout: CloudViewLayout

    /**
     * Метка о том, что сообщение является исходящим.
     */
    val outcome: Boolean

    /**
     * Метка о том, персенифицированное ли входящее сообщение (есть ли аватарка автора возле текста сообщения).
     */
    var isPersonal: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                cloudViewLayout.personView?.run {
                    isVisible = field
                    if (field) {
                        author?.personData?.let(this::setData)
                        lazyAuthorAvatarClickListener?.let(::setOnAuthorAvatarClickListener)
                        setHasActivityStatus(true)
                    }
                }
            }
        }

    /**
     * Автор сообщения.
     */
    var author: PersonModel? = null
        set(value) {
            field = value
            cloudViewLayout.titleView.data = CloudTitleView.CloudTitleData(value, receiverInfo)
            if (isPersonal && value?.personData != null) {
                cloudViewLayout.personView?.setData(value.personData)
            }
        }

    /**
     * Информация о получателе.
     */
    var receiverInfo: ReceiverInfo? = null
        set(value) {
            field = value
            cloudViewLayout.titleView.data = CloudTitleView.CloudTitleData(author, value)
        }

    /**
     * Форматированные дата и время получения/отправки. Применяется для интеграции с компонентом "Разделитель с датой".
     *
     * @see ListDateViewUpdater
     * @see ListDateFormatter
     */
    var dateTime: FormattedDateTime = FormattedDateTime(StringUtils.EMPTY, StringUtils.EMPTY)
        set(value) {
            field = value
            cloudViewLayout.run {
                timeView.text = value.time
                dateView.text = value.date
            }
        }

    /**
     * Дата получения/отправки.
     */
    var date: Date? = null
        set(value) {
            field = value
            cloudViewLayout.dateView.date = value
        }

    /**
     * Время получения/отправки.
     */
    var time: Date? = null
        set(value) {
            field = value
            cloudViewLayout.timeView.date = value
        }

    /**
     * Состояние отправки.
     */
    var sendingState: SendingState? = null
        set(value) {
            field = value
            cloudViewLayout.statusView.data = CloudStatusView.CloudStatusData(value, edited)
        }

    /**
     * Метка о редактировании сообщения.
     */
    var edited: Boolean = false
        set(value) {
            field = value
            cloudViewLayout.statusView.data = CloudStatusView.CloudStatusData(sendingState, value)
        }

    /**
     * Содержимое облачка.
     *
     * @exception IllegalStateException перед вызовом метода необходимо установить [MessagesViewPool] для генерации
     * элементов облачка.
     *
     * @see setViewPool
     */
    var data: CloudViewData = DefaultCloudViewData()
        set(value) {
            field = value
            cloudViewLayout.apply {
                setMessageUuid(value.messageUuid)
                checkAttachments(value)
                titleView.isAuthorBlocked = value.isAuthorBlocked
                contentView.setMessage(value, outcome)
            }
        }

    /**
     * Обработчик нажатий на ссылки в компоненте RichText.
     */
    var linkClickListener: LinkClickListener? = null
        set(value) {
            field = value
            cloudViewLayout.contentView.setLinkClickListener(value)
        }

    /**
     * Обработчик нажатий на номер телефона в компоненте RichText.
     */
    var phoneNumberClickListener: PhoneNumberClickListener? = null
        set(value) {
            field = value
            cloudViewLayout.contentView.setPhoneNumberClickListener(value)
        }

    /**
     * Обработчик долгих нажатий на контентную область.
     */
    var contentLongClickListener: OnLongClickListener? = null
        set(value) {
            field = value
            cloudViewLayout.contentView.setOnLongClickListener(value)
        }

    /**
     * Установить обработчик действий загрузки вложений.
     */
    var attachmentsUploadActionsHandler: AttachmentUploadActionsHandler? = null
        set(value) {
            field = value
            cloudViewLayout.contentView.setAttachmentUploadActionsHandler(value)
        }

    /**
     * Отображение прогресса отклонения подписи / доступа к файлу.
     */
    var showRejectProgress: Boolean = false
        set(value) {
            field = value
            cloudViewLayout.contentView.showRejectProgress(value)
        }

    /**
     * Отображение прогресса разрешения доступа к файлу.
     */
    var showAcceptProgress: Boolean = false
        set(value) {
            field = value
            cloudViewLayout.contentView.showAcceptProgress(value)
        }

    /**
     * Максимальное количество отображаемых вложений.
     * Остальные будут скрыты под счетчиком.
     */
    var maxVisibleAttachmentsCount: Int
        get() = cloudViewLayout.contentView.maxVisibleAttachmentsCount
        set(value) {
            cloudViewLayout.contentView.maxVisibleAttachmentsCount = value
        }

    /**
     * Показывать прогресс загрузки отображаемых вложений.
     */
    var showAttachmentsUploadProgress: Boolean
        get() = cloudViewLayout.contentView.showAttachmentsUploadProgress
        set(value) {
            cloudViewLayout.contentView.showAttachmentsUploadProgress = value
        }

    /**
     * Максимальное количество строк для текста.
     */
    var textMaxLines: Int = Int.MAX_VALUE
        set(value) {
            cloudViewLayout.contentView.setTextMaxLines(value)
        }

    private var lazyAuthorAvatarClickListener: AuthorAvatarClickListener? = null
    private var onClickListener: OnClickListener? = null

    init {
        var maxVisibleAttachmentsCount = -1
        var showAttachmentsUploadProgress = false
        with(context.theme.obtainStyledAttributes(attrs, R.styleable.CloudView, defStyleAttr, defStyleRes)) {
            outcome = getBoolean(R.styleable.CloudView_CloudView_outcome, false)
            maxVisibleAttachmentsCount = getInteger(
                R.styleable.CloudView_CloudView_maxVisibleAttachmentsCount,
                maxVisibleAttachmentsCount
            )
            showAttachmentsUploadProgress = getBoolean(
                R.styleable.CloudView_CloudView_showAttachmentsUploadProgress,
                showAttachmentsUploadProgress
            )
            recycle()
        }
        cloudViewLayout =
            if (outcome) CloudViewOutcomeLayout(this)
            else CloudViewIncomeLayout(this)
        if (maxVisibleAttachmentsCount > 0) {
            this.maxVisibleAttachmentsCount = maxVisibleAttachmentsCount
        }
        this.showAttachmentsUploadProgress = showAttachmentsUploadProgress
        swipeToQuoteBehavior.init(this, outcome)
        swipeToQuoteBehavior.attachView(cloudViewLayout)

        updatePadding(
            top = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_xs),
            right = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_3xs)
        )

        if (isInEditMode) showPreview()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        activateResourceCacheForRecycler(this)
        swipeToQuoteBehavior.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        swipeToQuoteBehavior.onDetachedFromWindow()
    }

    /**
     * Задаёт обработчик нажатия на фото автора сообщения.
     */
    fun setOnAuthorAvatarClickListener(authorClickListener: AuthorAvatarClickListener?) {
        if (isPersonal && authorClickListener != null) {
            cloudViewLayout.personView?.setOnClickListener {
                author?.let(authorClickListener::onAvatarClicked)
            }
        }
        lazyAuthorAvatarClickListener = authorClickListener
    }

    /**
     * Задаёт обработчик нажатия на имя автора сообщения.
     */
    fun setOnAuthorNameClickListener(authorClickListener: AuthorNameClickListener?) {
        if (authorClickListener != null) {
            cloudViewLayout.titleView.setOnClickListener {
                author?.let(authorClickListener::onNameClicked)
            }
        } else {
            cloudViewLayout.titleView.setOnClickListener(onClickListener)
        }
    }

    /**
     * Задать обработчик нажатий на статус сообщения.
     */
    fun setOnStatusClickListener(onStatusViewClick: (() -> Unit)?) {
        cloudViewLayout.statusView.setOnClickListener { onStatusViewClick?.invoke() }
    }

    //region Специфичные для Android методы. Не требуют включения в API

    /**
     * Установка [MessagesViewPool] для повторного использования контента [CloudView].
     *
     * @see recycleViews
     */
    fun setViewPool(viewPool: MessagesViewPool) {
        cloudViewLayout.setViewPool(viewPool)
    }

    /**
     * Установка [MessageBlockTextHolder] для создания и настройки текста и его контейнера [CloudView].
     */
    fun setTextHolder(textHolder: MessageBlockTextHolder) {
        cloudViewLayout.contentView.setMessageBlockTextHolder(textHolder)
    }

    /**
     * Установка [MediaPlayer] для проигррывания аудиосообщений.
     */
    fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        cloudViewLayout.contentView.setMediaPlayer(mediaPlayer)
    }

    /**
     * Установить цвета фона облачка.
     */
    fun setCloudBackgroundColor(@ColorInt color: Int) {
        cloudViewLayout.setCloudBackgroundColor(color)
    }

    /**
     * Освобождение использованного контента для повторного использования в других [CloudView].
     *
     * @exception IllegalStateException если не установлен [MessagesViewPool].
     *
     * @see setViewPool
     */
    fun recycleViews() {
        cloudViewLayout.contentView.recycleViews()
    }

    /**
     * Обработчик жестов для [RichTextView].
     */
    fun setGestureDetector(gestureDetector: GestureDetector) {
        cloudViewLayout.contentView.setGestureDetector(gestureDetector)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        onClickListener = listener
        cloudViewLayout.setOnMessageClickListener(listener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wrappedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val layoutWidthSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight,
            MeasureSpec.EXACTLY
        )
        cloudViewLayout.measure(layoutWidthSpec, wrappedSpec)

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            cloudViewLayout.measuredHeight + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        cloudViewLayout.layout(
            changed,
            paddingLeft,
            paddingTop,
            paddingLeft + cloudViewLayout.measuredWidth,
            paddingTop + cloudViewLayout.measuredHeight
        )
    }

    override fun setOnLongClickListener(listener: OnLongClickListener?) {
        super.setOnLongClickListener(listener)
        children.forEach { it.setOnLongClickListener(listener) }
    }

    override fun hasOverlappingRendering(): Boolean = false
    //endregion
}

/**
 * Поставщик закешированных стилей для компонента ячейка-облако.
 */
internal object CloudViewStylesProvider : CanvasStylesProvider()