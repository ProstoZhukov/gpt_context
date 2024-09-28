package ru.tensor.sbis.design.cloud_view.content;

import static ru.tensor.sbis.design.cloud_view.content.utils.ContentKt.hasAttachments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Pair;
import ru.tensor.sbis.attachments.ui.view.clickhandler.AttachmentUploadActionsHandler;
import ru.tensor.sbis.communication_decl.communicator.media.MediaMessage;
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.audio_player_view.view.message.AudioMessageView;
import ru.tensor.sbis.design.cloud_view.BuildConfig;
import ru.tensor.sbis.design.cloud_view.R;
import ru.tensor.sbis.design.cloud_view.content.container.ContainerView;
import ru.tensor.sbis.design.cloud_view.content.grant_access.GrantAccessButtonsView;
import ru.tensor.sbis.design.cloud_view.content.link.LinkClickListener;
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener;
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickSpan;
import ru.tensor.sbis.design.cloud_view.content.signing.SigningButtonsView;
import ru.tensor.sbis.design.cloud_view.content.utils.CloudViewTextColorTypeKt;
import ru.tensor.sbis.design.cloud_view.content.utils.ContentKt;
import ru.tensor.sbis.design.cloud_view.content.utils.MessageBlockTextHolder;
import ru.tensor.sbis.design.cloud_view.content.utils.MessagesViewPool;
import ru.tensor.sbis.design.cloud_view.model.AudioMessageCloudContent;
import ru.tensor.sbis.design.cloud_view.model.CloudContent;
import ru.tensor.sbis.design.cloud_view.model.CloudViewData;
import ru.tensor.sbis.design.cloud_view.model.LinkCloudContent;
import ru.tensor.sbis.design.cloud_view.model.QuoteCloudContent;
import ru.tensor.sbis.design.cloud_view.model.TaskLinkedServiceCloudContent;
import ru.tensor.sbis.design.theme.global_variables.Offset;
import ru.tensor.sbis.design.utils.ThemeUtil;

/**
 * Контейнер для размещения содержимоего сообщения в ячейке-облаке.
 *
 * @author ma.kolpakov
 */
public class MessageBlockView extends LinearLayout {

    @Nullable
    private ContainerView mContainerView;

    @Nullable
    private OnLongClickListener mLongClickListener;
    @Nullable
    private LinkClickListener linkClickListener;

    @Nullable
    private PhoneNumberClickListener phoneNumberClickListener;
    @Nullable
    private OnClickListener messageClickListener;

    @Nullable
    private MediaPlayer mediaPlayer;

    protected CloudViewData mCloudViewData;

    @Nullable
    private MessagesViewPool mViewPool;

    @Nullable
    private MessageBlockTextHolder textHolder;

    @Nullable
    private SigningButtonsView mSigningButtonsView;

    @Nullable
    private GrantAccessButtonsView mGrantAccessButtonsView;

    private boolean mIsOutcome;

    protected boolean mDisabled;

    @Px
    private final int mDefaultTopMargin;

    private int maxVisibleAttachmentsCount = 4;

    private boolean showAttachmentsUploadProgress = false;

    private AttachmentUploadActionsHandler attachmentUploadActionsHandler = null;

    private GestureDetector mGestureDetector;

    @Px
    public final int mMessageWidthWithAttachments;

    public MessageBlockView(@NonNull Context context) {
        this(context, null);
    }

    public MessageBlockView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public MessageBlockView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        mDefaultTopMargin = ThemeUtil.getDimenPx(context, ru.tensor.sbis.design.R.attr.offset_xs);
        mMessageWidthWithAttachments = getResources().getDimensionPixelSize(ru.tensor.sbis.attachments.ui.R.dimen.attachment_preview_default_size);
        initMessageBlockTextHolder();
    }

    /**
     * @SelfDocumented
     */
    public void setMessageBlockTextHolder(MessageBlockTextHolder textHolder) {
        this.textHolder = textHolder;
        initMessageBlockTextHolder();
        setTextColorFromViewPool();
    }

    public CloudViewData getCloudViewData() {
        return mCloudViewData;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMessageBlockTextHolder() {
        if (textHolder == null) return;
        TextView textView = textHolder.getTextView(getContext());
        View textLayoutView = textHolder.getTextLayoutView(getContext());

        textLayoutView.setId(R.id.cloud_view_message_block_rich_view_layout_id);
        textView.setId(R.id.cloud_view_message_block_text_id);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            textView.setTextAppearance(getContext(), ru.tensor.sbis.design.R.style.MessagesListItem_RegularText);
        } else {
            textView.setTextAppearance(ru.tensor.sbis.design.R.style.MessagesListItem_RegularText);
        }
        textView.setIncludeFontPadding(false);
        textView.setTypeface(TypefaceManager.getRobotoRegularFont(getContext()));
    }

    /**
     * Освобождает view элементов для возможности переиспользования
     */
    public void recycleViews() {
        View childView;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            childView = getChildAt(i);
            if (childView instanceof MessageBlockView) {
                final MessageBlockView messageBlockView = (MessageBlockView) childView;
                messageBlockView.recycleViews();
                messageBlockView.setLinkClickListener(null);
            } else if (childView instanceof TextView) {
                childView.setOnClickListener(null);
            }
            childView.setOnLongClickListener(null);
            childView.setOnTouchListener(null);

            if (textHolder != null && childView != textHolder.getTextLayoutView(getContext())) {
                if (mViewPool == null) {
                    throw new IllegalStateException("Unable to recycle views without view pool");
                }
                // Пул не поддерживает RichViewLayout, доп. проверка без подключения модуля rich_text
                if (!childView.getClass().getSimpleName().contains("RichViewLayout")) {
                    mViewPool.addView(childView);
                }
            }
        }
        clearAllListeners();
        removeAllViews();
    }

    /**
     * Устанавливает пул view элементов
     */
    public void setViewPool(@NonNull MessagesViewPool viewPool) {
        mViewPool = viewPool;
        setTextColorFromViewPool();
    }

    private void setTextColorFromViewPool() {
        if (mViewPool == null || textHolder == null) return;
        textHolder.getTextView(getContext())
                .setTextColor(mViewPool.getTextColor(CloudViewTextColorTypeKt.DEFAULT_TEXT));
    }

    /**
     * Устанавливает обработчик нажатий на ссылки
     */
    public void setLinkClickListener(@Nullable LinkClickListener linkClickListener) {
        this.linkClickListener = linkClickListener;
    }

    /**
     * Устанавливает пользовательский обработчик жестов для содержимого
     */
    public void setGestureDetector(@Nullable GestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateTextTouchListener() {
        if (textHolder != null) {
            View textView = textHolder.getTextView(getContext());
            textView.setOnTouchListener((richTextView, event) -> {
                boolean isRichTextHandled = richTextView.onTouchEvent(event);
                if (isRichTextHandled && event.getAction() == MotionEvent.ACTION_UP) {
                    if (linkClickListener != null) {
                        linkClickListener.onLinkClicked();
                    }
                }
                return isRichTextHandled || (mGestureDetector != null && mGestureDetector.onTouchEvent(event));
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clearAllListeners() {
        if (textHolder != null) {
            View textLayoutView = textHolder.getTextLayoutView(getContext());
            View textView = textHolder.getTextView(getContext());
            textLayoutView.setOnClickListener(null);
            textView.setOnClickListener(null);
            textLayoutView.setOnLongClickListener(null);
            textView.setOnLongClickListener(null);
            textLayoutView.setOnTouchListener(null);
            textView.setOnTouchListener(null);
        }
    }

    /**
     * Устанавливает отображаемое содержимое сообщения.
     * Предварительно должен быть задан пул view ({@link #setViewPool(MessagesViewPool)})
     */
    public void setMessage(@NonNull CloudViewData cloudViewData, boolean isOutcome) {
        recycleViews();
        mCloudViewData = cloudViewData;
        mDisabled = cloudViewData.isDisabledStyle();
        mIsOutcome = isOutcome;

        if (cloudViewData.isAuthorBlocked()) {
            setBlockedViewData();
        } else {
            setViewData(cloudViewData, true);
        }
    }

    /**
     * Устанавливает отображаемое содержимое сообщения.
     * Предварительно должен быть задан пул view ({@link #setViewPool(MessagesViewPool)})
     */
    public void setMessage(
        @NonNull CloudViewData cloudViewData,
        boolean isOutcome,
        boolean wrapAttachments
    ) {
        recycleViews();
        mCloudViewData = cloudViewData;
        mDisabled = cloudViewData.isDisabledStyle();
        mIsOutcome = isOutcome;

        if (cloudViewData.isAuthorBlocked()) {
            setBlockedViewData();
        } else {
            setViewData(cloudViewData, wrapAttachments);
        }
    }

    /**
     * Установить максимальное количество строк для текста.
     *
     * @param maxLines максимальное количество строк
     */
    public void setTextMaxLines(int maxLines) {
        TextView textView = Objects.requireNonNull(textHolder).getTextView(getContext());
        textView.setMaxLines(maxLines);
        if (maxLines != Integer.MAX_VALUE) {
            textView.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            textView.setEllipsize(null);
        }
    }

    private void setViewData(@NonNull CloudViewData cloudViewData, boolean wrapAttachments) {
        int messageWidth;
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (wrapAttachments && hasAttachments(cloudViewData)) {
            messageWidth = mMessageWidthWithAttachments;
        } else if (lp.width < 0) {
            messageWidth = lp.width;
        } else {
            messageWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (textHolder != null && !TextUtils.isEmpty(mCloudViewData.getText())) {
            setUpRichTextView(mCloudViewData.getText());
            LayoutParams layoutParams = ContentKt.createLayoutParams(textHolder.getTextLayoutView(getContext()), 0, messageWidth);
            addViewInLayout(textHolder.getTextLayoutView(getContext()), -1, layoutParams, true);
        }

        final List<CloudContent> contentItemList = mCloudViewData.getContent();
        if (!contentItemList.isEmpty()) {
            setMessageEntitiesList(contentItemList, cloudViewData.getRootElements(), messageWidth);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkAvailableTextWidth(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Проверить ширину текста перед измерением, чтобы текст не вылезал за границы MessageBlockView.
     * Локальный фикс для узких экранов, в сценарии входящего группового сообщения,
     * в сценарии отображения текста с вложением,
     * где текст должен быть фиксированной ширины mMessageWidthWithAttachments.
     *
     * @param widthMeasureSpec спека для измерения MessageBlockView
     */
    private void checkAvailableTextWidth(int widthMeasureSpec) {
        if (textHolder != null) {
            int availableWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingStart() - getPaddingEnd();
            View layout = textHolder.getTextLayoutView(getContext());
            ViewGroup.LayoutParams lp = layout.getLayoutParams();
            if (lp != null && availableWidth > 0 && lp.width > availableWidth) {
                layout.getLayoutParams().width = availableWidth;
            }
        }
    }

    private void setBlockedViewData() {
        int messageWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (textHolder != null) {
            Spannable blockedText = new SpannableStringBuilder(
                    getContext().getString(ru.tensor.sbis.communication_decl.R.string.communication_decl_blocked_user_content_text)
            );
            blockedText.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.palette_color_gray9)),
                    0,
                    blockedText.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            );
            setUpRichTextView(blockedText);
            LayoutParams layoutParams = ContentKt.createLayoutParams(textHolder.getTextLayoutView(getContext()), 0, messageWidth);
            addViewInLayout(textHolder.getTextLayoutView(getContext()), -1, layoutParams, true);
        }
    }

    /**
     * Задаёт необходимость отображения прогресса отклонения подписи / доступа к файлу.
     *
     * @param show true если требуется отображать прогресс.
     */
    public void showRejectProgress(boolean show) {
        if (mContainerView != null) {
            mContainerView.showRejectProgress(show);
        } else if (mSigningButtonsView != null) {
            mSigningButtonsView.showRejectProgress(show);
        } else if (mGrantAccessButtonsView != null) {
            mGrantAccessButtonsView.showRejectProgress(show);
        }
    }

    /**
     * Задаёт необходимость отображения прогресса разрешения доступа к файлу.
     *
     * @param show true если требуется отображать прогресс.
     */
    public void showAcceptProgress(boolean show) {
        if (mContainerView != null) {
            mContainerView.showAcceptProgress(show);
        } else if (mGrantAccessButtonsView != null) {
            mGrantAccessButtonsView.showAcceptProgress(show);
        }
    }

    /**
     * Задаёт обработчик нажатий на сообщение.
     */
    public void setOnMessageClickListener(@Nullable OnClickListener onMessageClickListener) {
        messageClickListener = onMessageClickListener;
        if (textHolder != null) {
            View textLayoutView = textHolder.getTextLayoutView(getContext());
            View textView = textHolder.getTextView(getContext());
            textLayoutView.setOnClickListener(messageClickListener);
            textView.setOnClickListener(messageClickListener);
        }
    }

    /**
     * Задаёт обработчик нажатий на номер телефона в сообщении.
     */
    public void setPhoneNumberClickListener(@Nullable PhoneNumberClickListener listener) {
        phoneNumberClickListener = listener;
    }

    /**
     * Задаёт медиаплеер для аудиосообщения.
     */
    public void setMediaPlayer(@Nullable MediaPlayer player) {
        mediaPlayer = player;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener longClickListener) {
        super.setOnLongClickListener(longClickListener);
        mLongClickListener = longClickListener;
        if (textHolder != null) {
            View textLayoutView = textHolder.getTextLayoutView(getContext());
            View textView = textHolder.getTextView(getContext());
            textLayoutView.setOnLongClickListener(mLongClickListener);
            textView.setOnLongClickListener(mLongClickListener);
        }
    }

    @Override
    public int getBaselineAlignedChildIndex() {
        View firstChild = getChildCount() > 0 ? getChildAt(0) : null;
        if (firstChild instanceof TextView || firstChild instanceof AudioMessageView) {
            return 0;
        }
        return -1;
    }

    @Override
    public int getBaseline() {
        if (getBaselineAlignedChildIndex() >= 0) {
            View child = getChildAt(getBaselineAlignedChildIndex());
            int childMarginTop = ((LayoutParams) child.getLayoutParams()).topMargin;
            return childMarginTop + child.getBaseline();
        } else {
            return getTextBaseline();
        }
    }

    public void setMaxVisibleAttachmentsCount(int count) {
        maxVisibleAttachmentsCount = Math.max(count, 0);
    }

    public int getMaxVisibleAttachmentsCount() {
        return maxVisibleAttachmentsCount;
    }

    public void setShowAttachmentsUploadProgress(boolean show) {
        showAttachmentsUploadProgress = show;
    }

    public boolean getShowAttachmentsUploadProgress() {
        return showAttachmentsUploadProgress;
    }

    public void setAttachmentUploadActionsHandler(@Nullable AttachmentUploadActionsHandler handler) {
        attachmentUploadActionsHandler = handler;
    }

    private int getTextBaseline() {
        TextView textView = null;
        if (textHolder != null) {
            textView = textHolder.getTextView(getContext());
        }
        if (textView != null && textView.getText().length() > 0) {
            int baseline = getCorrectTextViewBaseLine(textView);
            if (baseline > 0) {
                return textView.getTop() + baseline;
            }
        }
        return -1;
    }

    /**
     * Учитывает наличие ссылки первым элементом в сообщении.
     * Для правильного выравнивания времени в персональной переписке.
     */
    private int getCorrectTextViewBaseLine(TextView textView) {
        if (mCloudViewData.getRootElements().isEmpty()) return -1;

        List<CloudContent> content = mCloudViewData.getContent();
        CloudContent firstContent = content.get(mCloudViewData.getRootElements().get(0));
        boolean isContentWithLink = firstContent instanceof LinkCloudContent;

        if (isContentWithLink && !((LinkCloudContent) firstContent).isGroupConversation()) {
            int decoratedLinkSpanTopOffset = Offset.XS.getDimenPx(getContext()) + Offset.X2S.getDimenPx(getContext());
            return textView.getBaseline() - decoratedLinkSpanTopOffset;
        }
        return textView.getBaseline();
    }

    /**
     * Отображает заданные элементы.
     * Предварительно должен быть задан пул view ({@link #setViewPool(MessagesViewPool)}) и содержимое сообщения
     * ({@link #setMessage(CloudViewData, boolean)})
     *
     * @param contentList  список элементов
     * @param childIndexes список индексов элементов верхнего уровня
     * @param messageWidth ширина содержимого
     */
    protected void setMessageEntitiesList(@NonNull final List<CloudContent> contentList,
                                          @NonNull List<Integer> childIndexes,
                                          @Px int messageWidth) {
        if (BuildConfig.DEBUG) {
            if (mViewPool == null) {
                throw new IllegalStateException("You must set ViewPool before calling this method.");
            }

            if (mCloudViewData == null) {
                throw new IllegalStateException("You must set Message before calling this method.");
            }
        }

        int index;
        final int childrenCount = childIndexes.size();
        int firstChildIndex = 0;
        for (int i = 0; i < childrenCount; i++) {
            index = childIndexes.get(i);
            boolean isFirstItem = i == firstChildIndex &&
                    StringUtils.isBlank(mCloudViewData.getText());
            int topMargin = isFirstItem ? 0 : mDefaultTopMargin;
            CloudContent cloudContent = contentList.get(index);
            @Nullable Pair<View, LayoutParams> viewParamsPair = ContentKt.toContentView(
                    cloudContent,
                    mCloudViewData,
                    mIsOutcome,
                    contentList,
                    messageClickListener,
                    mLongClickListener,
                    linkClickListener,
                    topMargin,
                    messageWidth,
                    mViewPool,
                    maxVisibleAttachmentsCount,
                    showAttachmentsUploadProgress,
                    attachmentUploadActionsHandler
            );

            if (viewParamsPair != null) {
                View view = viewParamsPair.getFirst();
                if (view instanceof SigningButtonsView) {
                    mSigningButtonsView = (SigningButtonsView) view;
                } else if (view instanceof GrantAccessButtonsView) {
                    mGrantAccessButtonsView = (GrantAccessButtonsView) view;
                } else if (view instanceof ContainerView) {
                    mContainerView = (ContainerView) view;
                }
                // TODO https://online.sbis.ru/opendoc.html?guid=ce62f4a9-802a-412f-b3b1-8bafb7456cb0
                int position = cloudContent instanceof TaskLinkedServiceCloudContent ? 0 : -1;
                view.setOnLongClickListener(mLongClickListener);
                if (cloudContent instanceof AudioMessageCloudContent) {
                    view.setOnClickListener(messageClickListener);
                    if (mediaPlayer != null) {
                        ((MediaMessage) view).setMediaPlayer(mediaPlayer);
                    }
                }
                addViewInLayout(view, position, viewParamsPair.getSecond(), true);
            } else {
                firstChildIndex++;
            }
        }
        forceLayout();
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpRichTextView(@Nullable Spannable message) {
        if (textHolder == null) return;
        if (message != null) {
            setQuoteClickSpanIfExists(message);
            if (phoneNumberClickListener != null) {
                textHolder.setPhoneClickSpan(message, phoneNumberClickListener);
            }
        }
        setOnMessageClickListener(messageClickListener);
        setOnLongClickListener(mLongClickListener);
        updateTextTouchListener();
        textHolder.setText(message);
    }

    // todo возможно, координаты цитаты нужно добавлять в модель в явном виде в MessageMapper
    private void setQuoteClickSpanIfExists(@NotNull Spannable message) {
        if (textHolder == null) return;
        List<QuoteCloudContent> quoteContent = new ArrayList<>();
        for (CloudContent contentItem : mCloudViewData.getContent()) {
            if (contentItem instanceof QuoteCloudContent) {
                quoteContent.add((QuoteCloudContent) contentItem);
            }
        }
        if (quoteContent.isEmpty()) return;

        // Убираем предыдущие спаны, перед добавлением новых
        QuoteClickSpan[] quoteClickSpans = message.getSpans(0, message.length(), QuoteClickSpan.class);
        for (QuoteClickSpan quoteClickSpan : quoteClickSpans) {
            message.removeSpan(quoteClickSpan);
        }

        textHolder.setQuoteClickSpan(message, quoteContent);
    }
}
