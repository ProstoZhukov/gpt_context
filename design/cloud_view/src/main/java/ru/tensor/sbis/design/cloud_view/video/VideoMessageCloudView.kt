package ru.tensor.sbis.design.cloud_view.video

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessageExpandableContainer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.cloud_view.content.quote.Quote
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickSpan
import ru.tensor.sbis.design.cloud_view.content.utils.MessageBlockTextHolder
import ru.tensor.sbis.design.cloud_view.content.video.CloudVideoMessageView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudStatusView
import ru.tensor.sbis.design.cloud_view.layout.children.CloudTitleView
import ru.tensor.sbis.design.cloud_view.listener.AuthorAvatarClickListener
import ru.tensor.sbis.design.cloud_view.listener.AuthorNameClickListener
import ru.tensor.sbis.design.cloud_view.model.PersonModel
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent
import ru.tensor.sbis.design.cloud_view.model.ReceiverInfo
import ru.tensor.sbis.design.cloud_view.model.SendingState
import ru.tensor.sbis.design.cloud_view.utils.CloudThemeContextWrapper
import ru.tensor.sbis.design.cloud_view.utils.swipe.MessageSwipeToQuoteBehavior
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.cloud_view.video.VideoMessageCloudViewStylesProvider.activateResourceCacheForRecycler
import ru.tensor.sbis.design.cloud_view.video.layout.VideoMessageCloudViewLayout
import ru.tensor.sbis.design.cloud_view.video.layout.VideoMessageIncomeLayout
import ru.tensor.sbis.design.cloud_view.video.layout.VideoMessageOutcomeLayout
import ru.tensor.sbis.design.cloud_view.video.model.DefaultVideoMessageViewData
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageCloudViewData
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageContent
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageMediaContent
import ru.tensor.sbis.design.cloud_view.video.model.VideoMessageQuoteContent
import ru.tensor.sbis.design.cloud_view.video.utils.VideoMessageSwipeToQuoteBehaviorImpl
import java.util.Date
import ru.tensor.sbis.design.cloud_view.R as RCloudView

/**
 * Ячейка для отображения видеосообщения.
 * ViewGroup поддерживает добавление дочерних View,
 * которые будут размещены внутри ячейки по принципу вертикального LinearLayout.
 *
 * @author da.zhukov
 */
class VideoMessageCloudView private constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    private val swipeToQuoteBehavior: VideoMessageSwipeToQuoteBehaviorImpl
) : ViewGroup(
    CloudThemeContextWrapper(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
),
    CloudVideoMessageView,
    MessageSwipeToQuoteBehavior by swipeToQuoteBehavior,
    MediaMessageExpandableContainer {

    @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = RCloudView.attr.incomeCloudViewTheme,
        @StyleRes defStyleRes: Int = RCloudView.style.DefaultCloudViewTheme_Income
    ) : this(
        context,
        attrs,
        defStyleAttr,
        defStyleRes,
        VideoMessageSwipeToQuoteBehaviorImpl(context)
    )

    /**
     * Разметка ячейки видеосообщения.
     * Имеет 2 конфигурации:
     * [VideoMessageOutcomeLayout] - исходящее сообщение.
     * [VideoMessageIncomeLayout] - входящее сообщение.
     */
    private val videoMessageLayout: VideoMessageCloudViewLayout
    private var onClickListener: OnClickListener? = null

    /**
     * Метка о том, что сообщение является исходящим.
     */
    val outcome: Boolean

    /**
     * Содержимое ячейки видеосообщения.
     */
    var data: VideoMessageCloudViewData = DefaultVideoMessageViewData()
        set(value) {
            val changed = field != value
            field = value
            if (changed) {
                videoMessageLayout.videoMessageView.data =
                    value.content
                        .filterIsInstance<VideoMessageMediaContent>()
                        .firstOrNull()
                        ?.data
                setUpRichTextView(value)
                safeRequestLayout()
            }
        }

    /**
     * Метка о том, персенифицированное ли входящее сообщение (есть ли аватарка автора возле сообщения).
     */
    var isPersonal: Boolean = false
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                videoMessageLayout.personView?.run {
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
            videoMessageLayout.titleView.data = CloudTitleView.CloudTitleData(value, receiverInfo)
            if (isPersonal && value?.personData != null) {
                videoMessageLayout.personView?.setData(value.personData)
            }
        }

    /**
     * Информация о получателе.
     */
    var receiverInfo: ReceiverInfo? = null
        set(value) {
            field = value
            videoMessageLayout.titleView.data = CloudTitleView.CloudTitleData(author, value)
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
            videoMessageLayout.apply {
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
            videoMessageLayout.dateView.date = value
        }

    /**
     * Время получения/отправки.
     */
    var time: Date? = null
        set(value) {
            field = value
            videoMessageLayout.timeView.date = value
        }

    /**
     * Состояние отправки.
     */
    var sendingState: SendingState? = null
        set(value) {
            field = value
            videoMessageLayout.statusView.data = CloudStatusView.CloudStatusData(value, edited)
        }

    /**
     * Метка о редактировании сообщения.
     */
    var edited: Boolean = false
        set(value) {
            field = value
            videoMessageLayout.statusView.data = CloudStatusView.CloudStatusData(sendingState, value)
        }

    /**
     * Обработчик долгих нажатий на контентную область.
     */
    var contentLongClickListener: OnLongClickListener? = null
        set(value) {
            field = value
            videoMessageLayout.videoMessageView.setOnLongClickListener { view ->
                field?.let {
                    // post нужен для показа меню после обработки лонг клика, иначе при показе меню сработает и onClick
                    view.post { it.onLongClick(view) }
                    true
                } ?: false
            }
        }

    override val videoMessageView: View
        get() = videoMessageLayout.videoMessageView

    private var lazyAuthorAvatarClickListener: AuthorAvatarClickListener? = null

    init {
        with(context.theme.obtainStyledAttributes(attrs, RCloudView.styleable.CloudView, defStyleAttr, defStyleRes)) {
            outcome = getBoolean(RCloudView.styleable.CloudView_CloudView_outcome, false)
            recycle()
        }
        videoMessageLayout =
            if (outcome) VideoMessageOutcomeLayout(this)
            else VideoMessageIncomeLayout(this)
        videoMessageLayout.videoMessageView.outcome = outcome
        swipeToQuoteBehavior.init(this, outcome)
        swipeToQuoteBehavior.attachView(videoMessageLayout)

        updatePadding(
            top = context.getDimenPx(RDesign.attr.offset_xs),
            right = context.getDimenPx(RDesign.attr.offset_3xs)
        )
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
            videoMessageLayout.personView?.setOnClickListener {
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
            videoMessageLayout.titleView.setOnClickListener {
                author?.let(authorClickListener::onNameClicked)
            }
        } else {
            videoMessageLayout.titleView.setOnClickListener(onClickListener)
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        onClickListener = listener
        videoMessageLayout.setOnMessageClickListener { listener?.onClick(this) }
    }

    /**
     * Установить плеер.
     */
    fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        videoMessageLayout.videoMessageView.setMediaPlayer(mediaPlayer)
    }

    /**
     * Установить слушателя действий.
     */
    fun setMediaActionListener(listener: MediaMessage.ActionListener?) {
        videoMessageLayout.videoMessageView.actionListener = listener
    }

    /**
     * Установка [MessageBlockTextHolder] для создания и настройки текста.
     */
    fun setTextHolder(textHolder: MessageBlockTextHolder) {
        videoMessageLayout.setTextHolder(textHolder)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wrappedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val layoutWidthSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight,
            MeasureSpec.EXACTLY
        )
        videoMessageLayout.measure(layoutWidthSpec, wrappedSpec)

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            videoMessageLayout.measuredHeight + paddingTop + paddingBottom
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        videoMessageLayout.layout(
            changed,
            paddingLeft,
            paddingTop,
            paddingLeft + videoMessageLayout.measuredWidth,
            paddingTop + videoMessageLayout.measuredHeight
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpRichTextView(data: VideoMessageCloudViewData) {
        val text = data.text
        if (!text.isNullOrBlank()) {
            videoMessageLayout.apply {
                backgroundView.isVisible = true
                messageLayout.isVisible = true
                quoteMarkerView.isVisible = true
                setQuoteClickSpanIfExists(text, data.content)
                textHolder.setText(text)
            }
        } else {
            videoMessageLayout.apply {
                backgroundView.isVisible = false
                messageLayout.isVisible = false
                quoteMarkerView.isVisible = false
            }
        }
    }

    private fun setQuoteClickSpanIfExists(text: Spannable, content: List<VideoMessageContent>) {
        val quoteContent: MutableList<VideoMessageQuoteContent> = ArrayList()
        for (contentItem in content) {
            if (contentItem is VideoMessageQuoteContent) {
                quoteContent.add(contentItem)
            }
        }
        if (quoteContent.isEmpty()) return

        // Убираем предыдущие спаны, перед добавлением новых
        val quoteClickSpans = text.getSpans(0, text.length, QuoteClickSpan::class.java)
        for (quoteClickSpan in quoteClickSpans) {
            text.removeSpan(quoteClickSpan)
        }
        val cloudQuote = quoteContent.map { it.toCloudQuoteContent() }
        videoMessageLayout.textHolder.setQuoteClickSpan(text, cloudQuote)
    }

    private fun VideoMessageQuoteContent.toCloudQuoteContent() = QuoteCloudContent(
        quote as Quote,
        actionListener
    )
}

/**
 * Поставщик закешированных стилей для компонента ячейка-облако.
 */
internal object VideoMessageCloudViewStylesProvider : CanvasStylesProvider()
