package ru.tensor.sbis.design.text_span;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Objects;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan;

/**
 * TextView c возможностью сворачивания/разворачивания текста.
 * Некорректно отображает ссылки из-за манипуляций с текстом (обрезает текст, добавляет многоточие и др.).
 * Рекомендуется использовать ru.tensor.sbis.tasks.view.ExpandableTextView или ru.tensor.sbis.tasks.view.ExpandableTextViewWithArrow
 */
@SuppressWarnings({"Convert2Lambda", "deprecation", "unused"})
@Deprecated
public class ExpandableTextView extends AppCompatTextView {

    private static final int DEFAULT_MAX_LINES = 3;
    private static final int TYPE_WITH_TEXT = 1;
    private static final int TYPE_WITH_ICON = 2;

    @SuppressWarnings("RegExpRedundantEscape")
    private static final Pattern WEB_URL = Pattern.compile("[a-z]+:\\/\\/[^ \\n]*");

    private String mEllipsis;
    private String mCollapsedEllipsis;
    private String mExpandedEllipsis;
    private int mTextColor;
    @Nullable
    private Typeface mIconTypeface;

    private int mMaxLines;
    private int mEllipsisType;
    private int mEllipsisIconSize;
    private boolean mIsExpanded;
    private boolean mIsEllipsized;
    private CharSequence mExpandedText;
    private CharSequence mCollapsedText;
    private int mAutoLinkMask;

    private TextUtils.EllipsizeCallback mEllipsizeCallback;

    public ExpandableTextView(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ExpandableTextView(@NonNull Context context,
                              @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ExpandableTextView(@NonNull Context context,
                              @Nullable AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @SuppressWarnings("SameParameterValue")
    private void init(@NonNull Context context,
                      @Nullable AttributeSet attrs,
                      int defStyle,
                      int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyle, defStyleRes);
        mMaxLines = array.getInt(R.styleable.ExpandableTextView_collapsedMaxLines, DEFAULT_MAX_LINES);
        mEllipsisType = array.getInt(R.styleable.ExpandableTextView_ellipsisType, TYPE_WITH_TEXT);
        mEllipsisIconSize = array.getDimensionPixelSize(R.styleable.ExpandableTextView_ellipsisIconSize, (int) getTextSize());
        array.recycle();

        if (mEllipsisType == TYPE_WITH_ICON) {
            mEllipsis = "";
            mCollapsedEllipsis = context.getString(R.string.text_span_task_text_ellipsis);
            mExpandedEllipsis = context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_double_arrow_up);
            mTextColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.color_primary);
            mIconTypeface = TypefaceManager.getSbisMobileIconTypeface(context);
        } else if (mEllipsisType == TYPE_WITH_TEXT) {
            mEllipsis = context.getString(R.string.text_span_task_text_ellipsis);
            mCollapsedEllipsis = context.getString(R.string.text_span_task_text_expand);
            mExpandedEllipsis = context.getString(R.string.text_span_task_text_collapse);
            mTextColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.color_accent);
        } else {
            mEllipsis = context.getString(R.string.text_span_task_text_ellipsis);
            mCollapsedEllipsis = "";
            mExpandedEllipsis = "";
            mTextColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.color_accent);
        }

        mEllipsizeCallback = new TextUtils.EllipsizeCallback() {
            @Override
            public void ellipsized(int start,
                                   int end) {
                mIsEllipsized = start > 0;
            }
        };
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new IllegalStateException(ExpandableTextView.class.getSimpleName() + " doesn't support click listeners");
    }

    /**
     * Переключить состояние свёрнут/развёрнут.
     */
    public void toggle() {
        if (!mIsEllipsized) {
            return;
        }
        if (mIsExpanded) {
            collapse();
        } else {
            expand();
        }
    }

    /**
     * Свернуть текст.
     */
    public void collapse() {
        mIsExpanded = false;
        setText(mCollapsedText);
    }

    /**
     * Развернуть текст.
     */
    public void expand() {
        mIsExpanded = true;
        setText(mExpandedText);
    }

    /**
     * SelfDocumented
     */
    public void setTextForEllipsize(@Nullable CharSequence originalText) {
        releaseTextLabels();
        if (!TextUtils.isEmpty(originalText)) {
            SpannableStringBuilder linksText = addLinks(Objects.requireNonNull(originalText));
            setText(linksText);
            ellipsizeText(linksText);
        } else {
            setText(originalText);
        }
    }

    private void releaseTextLabels() {
        mExpandedText = null;
        mCollapsedText = null;
    }

    @NonNull
    private SpannableStringBuilder addLinks(@NonNull CharSequence text) {
        SpannableStringBuilder textBuilder = SpannableStringBuilder.valueOf(text);
        gatherAutoLinkMask();
        if ((mAutoLinkMask & Linkify.WEB_URLS) != 0) {
            Linkify.addLinks(textBuilder, WEB_URL, null);
        }
        return textBuilder;
    }

    private void gatherAutoLinkMask() {
        int autoLinkMask = getAutoLinkMask();
        if (autoLinkMask != 0) {
            mAutoLinkMask = autoLinkMask;
        }
        setAutoLinkMask(0);
    }

    private void ellipsizeText(@NonNull SpannableStringBuilder originalText) {
        final Layout layout = getLayout();
        if (layout != null) {
            computeEllipsizedText(originalText, layout);
        } else {
            waitForLayout(originalText);
        }
    }

    private void waitForLayout(@NonNull final SpannableStringBuilder originalText) {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (getLayout() != null) {
                    computeEllipsizedText(originalText, getLayout());
                }
            }
        });
    }

    private void computeEllipsizedText(@NonNull SpannableStringBuilder originalText,
                                       @NonNull Layout layout) {
        if (layout.getLineCount() < mMaxLines) {
            mIsEllipsized = false;
            return;
        }
        int lastLineStart = layout.getLineStart(mMaxLines - 1);
        // can return value that larger length if Spannable contains null charter, check when we get u0000 in text
        if (lastLineStart >= originalText.length()) {
            lastLineStart = originalText.length();
        }
        final int lastLineEnd = getLineEnd(originalText, lastLineStart);
        final CharSequence ellipsizedLine = TextUtils.ellipsize(originalText.subSequence(lastLineStart, lastLineEnd),
                getPaint(), getAvailableWidth(), TextUtils.TruncateAt.END, false, mEllipsizeCallback);

        mIsEllipsized |= originalText.length() > lastLineEnd;
        if (!mIsEllipsized) {
            return;
        }

        SpannableStringBuilder ellipsizedText = new SpannableStringBuilder(originalText, 0, lastLineStart);
        ellipsizedText.append(makeRoomForEllipsis(ellipsizedLine));
        ellipsizedText.append(mEllipsis);

        setTextLabels(originalText, ellipsizedText);
        setText(mIsExpanded ? mExpandedText : mCollapsedText);
    }

    private int getLineEnd(@NonNull CharSequence text,
                           int start) {
        int end = text.toString().indexOf("\n", start);
        return end == -1 ? text.length() : end;
    }

    private int getAvailableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @NonNull
    private CharSequence makeRoomForEllipsis(@NonNull CharSequence ellipsizedLine) {
        Paint paint = getPaint();
        int lineLength = ellipsizedLine.length();
        StringBuilder stringBuilder = new StringBuilder(ellipsizedLine);
        stringBuilder.append(mEllipsis);
        stringBuilder.append(mCollapsedEllipsis);
        int availableWidth = getAvailableWidth();
        while (lineLength > 0 && getTextWidth(paint, stringBuilder) > availableWidth) {
            lineLength--;
            stringBuilder.deleteCharAt(lineLength);
        }
        return lineLength > 0 ? ellipsizedLine.subSequence(0, lineLength) : "";
    }

    private int getTextWidth(@NonNull Paint paint,
                             @NonNull CharSequence text) {
        return (int) Math.ceil(paint.measureText(text, 0, text.length()));
    }

    private void setTextLabels(@NonNull SpannableStringBuilder originalText,
                               @NonNull SpannableStringBuilder ellipsizedText) {
        if (mEllipsisType == TYPE_WITH_ICON) {
            mExpandedText = appendIconText(originalText, mExpandedEllipsis);
            mCollapsedText = ellipsizedText.append(mCollapsedEllipsis);
        } else if (mEllipsisType == TYPE_WITH_TEXT) {
            mExpandedText = appendColoredText(originalText, mExpandedEllipsis);
            mCollapsedText = appendColoredText(ellipsizedText, mCollapsedEllipsis);
        } else {
            mExpandedText = originalText;
            mCollapsedText = ellipsizedText;
        }
    }

    @NonNull
    private CharSequence appendIconText(@NonNull SpannableStringBuilder source,
                                        @NonNull String icon) {
        source.append("\r\n");
        int coloredTextStart = source.length();
        source.append(icon);
        if (mIconTypeface != null) {
            source.setSpan(new CustomTypefaceSpan(mIconTypeface), coloredTextStart, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        source.setSpan(new ForegroundColorSpan(mTextColor), coloredTextStart, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), coloredTextStart, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        source.setSpan(new AbsoluteSizeSpan(mEllipsisIconSize), coloredTextStart, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return source;
    }

    @NonNull
    private CharSequence appendColoredText(@NonNull SpannableStringBuilder source,
                                           @NonNull String postfix) {
        int coloredTextStart = source.length();
        source.append(postfix);
        source.setSpan(new ForegroundColorSpan(mTextColor), coloredTextStart, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return source;
    }

    //region SavedState
    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.isExpanded = mIsExpanded;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mIsExpanded = savedState.isExpanded;
    }

    static final class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean isExpanded;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in) {
            super(in);
            isExpanded = in.readInt() != 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest,
                                  int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isExpanded ? 1 : 0);
        }
    }
    //endregion
}
