package ru.tensor.sbis.design.text_span;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.design.utils.FormatUtils;
import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan;

/**
 * Легаси код
 * SelfDocumented
 */
@SuppressWarnings("unused")
public class TextViewWithPostfix extends androidx.appcompat.widget.AppCompatTextView {

    @NonNull
    private CharSequence mBaseText;
    @NonNull
    private CharSequence mPostfixText;
    private float mFullTextWidth;
    private final Paint mPaint;
    private final Paint mPostfixPaint;
    private String space = StringUtils.SPACE;
    @NonNull
    private final AbsoluteSizeSpan mPostfixTextSizeSpan;
    @NonNull
    private final ForegroundColorSpan mPostfixTextColorSpan;

    public TextViewWithPostfix(Context context) {
        this(context, null);
    }

    public TextViewWithPostfix(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewWithPostfix(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEllipsize(null);
        setMaxLines(1);
        Typeface fontType = TypefaceManager.getRobotoRegularFont(context);
        mPaint = getPaint();
        mPostfixPaint = new Paint(mPaint);
        mPaint.setTypeface(fontType);
        mPostfixPaint.setTypeface(fontType);
        int postfixColor = getCurrentTextColor();
        int postfixTextSize = (int) getTextSize();

        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextViewWithPostFix, 0, 0);
            try {
                postfixColor = typedArray.getColor(R.styleable.TextViewWithPostFix_postfix_text_color, postfixColor);
                postfixTextSize = typedArray.getDimensionPixelSize(R.styleable.TextViewWithPostFix_postfix_text_size, postfixTextSize);
                setSpace(typedArray.getString(R.styleable.TextViewWithPostFix_space));
            } finally {
                typedArray.recycle();
            }
        }

        mPostfixPaint.setTextSize(postfixTextSize);
        mPostfixPaint.setColor(postfixColor);
        mPostfixText = StringUtils.EMPTY;
        mBaseText = StringUtils.EMPTY;
        mPostfixTextSizeSpan = new AbsoluteSizeSpan(postfixTextSize);
        mPostfixTextColorSpan = new ForegroundColorSpan(postfixColor);
    }

    private void setSpace(@Nullable String space) {
        if (space == null) {
            this.space = StringUtils.SPACE;
        } else {
            this.space = space;
        }
    }

    /**
     * SelfDocumented
     */
    public void setTextWithPostfix(@Nullable CharSequence baseText, @Nullable CharSequence postfixText, boolean styled) {
        boolean isBaseTextEmpty = TextUtils.isEmpty(baseText);
        boolean isPostfixTextEmpty = TextUtils.isEmpty(postfixText);

        mBaseText = isBaseTextEmpty ? StringUtils.EMPTY : Objects.requireNonNull(baseText);
        mPostfixText = isPostfixTextEmpty ?
                StringUtils.EMPTY :
                !isBaseTextEmpty ? space + postfixText : Objects.requireNonNull(postfixText);

        SpannableStringBuilder fullText = new SpannableStringBuilder();
        fullText.append(mBaseText);
        if (mPostfixText.length() > 0) {
            SpannableStringBuilder postfixSpannable = generatePostfixSpannableString(mPostfixText, false);
            fullText.append(postfixSpannable);
        }

        if (getEllipsize() == null) {
            mFullTextWidth = mPaint.measureText(mBaseText.toString()) + mPostfixPaint.measureText(mPostfixText.toString());
        } else {
            mFullTextWidth = Float.MIN_VALUE;
        }

        if (styled) {
            fullText.setSpan(new CustomTypefaceSpan(TypefaceManager.getRobotoRegularFont(getContext())), 0, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(fullText);
    }

    /**
     * SelfDocumented
     */
    public void setTextWithPostfix(@Nullable CharSequence baseText, @Nullable CharSequence postfixText) {
        setTextWithPostfix(baseText, postfixText, true);
    }

    /**
     * SelfDocumented
     */
    public void setTextWithPostfix(@Nullable CharSequence text, int countPostfix) {
        setTextWithPostfix(
                text,
                FormatUtils.formatCount(countPostfix),
                true
        );
    }

    /**
     * SelfDocumented
     */
    public void setTextWithPostfix(@Nullable CharSequence text, int countPostfix, boolean styled) {
        setTextWithPostfix(
                text,
                FormatUtils.formatCount(countPostfix),
                styled
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth() - getCompoundPaddingRight() - getCompoundPaddingLeft();

        if (measuredWidth > 0 && measuredWidth < mFullTextWidth) {
            trimTextToWidth(measuredWidth);
        }
    }

    private void trimTextToWidth(float viewWidth) {
        if (!TextUtils.isEmpty(mBaseText)) {
            SpannableStringBuilder ellipsizedStringBuilder = new SpannableStringBuilder(mBaseText);
            ellipsizedStringBuilder.append("...");
            for (int i = mBaseText.length() - 1; i > 1 && viewWidth < mFullTextWidth; i--) {
                ellipsizedStringBuilder.delete(i, i + 1);
                mFullTextWidth = mPaint.measureText(ellipsizedStringBuilder, 0, ellipsizedStringBuilder.length()) + mPostfixPaint.measureText(mPostfixText.toString());
            }
            ellipsizedStringBuilder.append(generatePostfixSpannableString(mPostfixText, false));
            ellipsizedStringBuilder.setSpan(new CustomTypefaceSpan(TypefaceManager.getRobotoRegularFont(getContext())), 0, ellipsizedStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(ellipsizedStringBuilder);
        } else if (!TextUtils.isEmpty(mPostfixText)) {
            SpannableStringBuilder ellipsizedStringBuilder = new SpannableStringBuilder(generatePostfixSpannableString(mPostfixText, true));
            for (int i = mPostfixText.length() - 1; i > 1 && viewWidth < mFullTextWidth; i--) {
                ellipsizedStringBuilder.delete(i, i + 1);
                mFullTextWidth = mPostfixPaint.measureText(ellipsizedStringBuilder, 0, ellipsizedStringBuilder.length());
            }
            ellipsizedStringBuilder.setSpan(new CustomTypefaceSpan(TypefaceManager.getRobotoRegularFont(getContext())), 0, ellipsizedStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(ellipsizedStringBuilder);
        }
    }

    @NonNull
    private SpannableStringBuilder generatePostfixSpannableString(@NonNull CharSequence postfix, boolean ellipsized) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (TextUtils.isEmpty(postfix)) {
            return spannableStringBuilder;
        }
        if (ellipsized) {
            append("...");
        }
        spannableStringBuilder.append(postfix);
        spannableStringBuilder.setSpan(mPostfixTextColorSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(mPostfixTextSizeSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
}