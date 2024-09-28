package ru.tensor.sbis.common_views.date;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Date;

import ru.tensor.sbis.common.util.date.DateFormatUtils;
import ru.tensor.sbis.common_views.R;
import ru.tensor.sbis.design.TypefaceManager;

/**
 * Вью заголовка даты для стандартизированного отображения
 * https://online.sbis.ru/opendoc.html?guid=6a91ca08-d256-4f0c-9e68-ed5b29c3d1bc
 *
 * @author vv.chekurda
 */
public class DateHeaderView extends View {

    @NonNull
    private final Rect mRect = new Rect();
    @ColorInt
    protected int mChangeableTextColor;
    @ColorInt
    protected int mDefaultTextColor;
    private int mViewHeight;
    private int mViewWidth;
    private boolean mWrapVertically;
    private boolean mWrapHorizontally;
    private float mVerticalCenter;
    private int mContentEdgeRight;
    private int mDayTextRightMargin;
    private float mTextBaseline;
    private float mTimeTextBorderLeft;
    private float mDayTextBorderLeft;
    private Paint mTextPaint;
    @Nullable
    private String mDayText;
    @Nullable
    private String mTimeText;
    @Nullable
    private String mDateText;
    @ColorInt
    private int mAccentTextColor;

    public DateHeaderView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public DateHeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DateHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CommonViewsDateHeaderView, 0, 0);
            try {
                mWrapVertically = attributeArray.getBoolean(R.styleable.CommonViewsDateHeaderView_wrap_vertically, false);
                mWrapHorizontally = attributeArray.getBoolean(R.styleable.CommonViewsDateHeaderView_wrap_horizontally, false);
            } finally {
                attributeArray.recycle();
            }
        }

        final Resources resources = getResources();
        mDayTextRightMargin = resources.getDimensionPixelOffset(R.dimen.common_views_date_header_day_padding_right);
        final int headerTextSize = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.size_caption1_scaleOff);
        int additionalLeftAndRightPadding = getAdditionalLeftAndRightPadding(resources);
        int additionalTopAndBottomPadding = getAdditionalTopAndBottomPadding(resources);
        final int paddingTop = getPaddingTop() + additionalTopAndBottomPadding;
        final int paddingBottom = getPaddingBottom() + additionalTopAndBottomPadding;
        mContentEdgeRight = getPaddingRight();
        mDefaultTextColor = mChangeableTextColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.text_color_black_3);
        mAccentTextColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.text_color_accent_3);

        setPadding(getPaddingLeft() + additionalLeftAndRightPadding, paddingTop, mContentEdgeRight + additionalLeftAndRightPadding, paddingBottom);

        mTextPaint = new Paint();
        mTextPaint.setTypeface(TypefaceManager.getRobotoRegularFont(context));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, headerTextSize, resources.getDisplayMetrics()));
        mTextPaint.setColor(mDefaultTextColor);

        final Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mViewHeight = (int) (fm.bottom - fm.top + fm.leading + paddingTop + paddingBottom);
        mVerticalCenter = ((mViewHeight + paddingTop - paddingBottom) / 2f);
    }

    private int getAdditionalLeftAndRightPadding(Resources resources) {
        if (!mWrapHorizontally) {
            return resources.getDimensionPixelOffset(R.dimen.common_views_date_header_additional_padding);
        } else {
            return 0;
        }
    }

    protected int getAdditionalTopAndBottomPadding(Resources resources) {
        if (!mWrapVertically) {
            return resources.getDimensionPixelOffset(R.dimen.common_views_date_header_additional_padding);
        } else {
            return 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDateText != null) {
            mTextPaint.setColor(mChangeableTextColor);
            canvas.drawText(mDateText, getPaddingStart(), mTextBaseline, mTextPaint);
        } else {
            if (mTimeText != null) {
                mTextPaint.setColor(mChangeableTextColor);
                canvas.drawText(mTimeText, mTimeTextBorderLeft, mTextBaseline, mTextPaint);
            }
            if (mDayText != null) {
                mTextPaint.setColor(mDefaultTextColor);
                canvas.drawText(mDayText, mDayTextBorderLeft, mTextBaseline, mTextPaint);
            }
        }
    }

    @Override
    public int getBaseline() {
        return (int) mTextBaseline;
    }

    /**
     * Метод для обновления в хэдера.
     *
     * @param dayText  день.
     * @param timeText время.
     */
    public void updateHeader(@Nullable String dayText, @Nullable String timeText) {
        if ((timeText != null || dayText != null) && (!TextUtils.equals(mDayText, dayText) || !TextUtils.equals(mTimeText, timeText))) {
            int dayTextWidth = 0;
            int timeTextWidth = 0;
            mTimeText = timeText;
            mDayText = dayText;

            //measure baseline
            String completeText = mDayText + mTimeText;
            mTextPaint.getTextBounds(completeText, 0, completeText.length(), mRect);
            mTextBaseline = mVerticalCenter + mRect.height() / 2f - mRect.bottom;

            if (timeText != null) {
                mTextPaint.getTextBounds(mTimeText, 0, mTimeText.length(), mRect);
                timeTextWidth = mRect.width();
            }
            float width = getPaddingLeft() + getPaddingRight() + timeTextWidth;

            if (mDayText != null) {
                mTextPaint.getTextBounds(mDayText, 0, mDayText.length(), mRect);
                dayTextWidth = mRect.width();
                width += dayTextWidth;
            }

            if (dayTextWidth > 0 && timeTextWidth > 0) {
                width += mDayTextRightMargin;
            }

            if (mViewWidth != (int) width) {
                mViewWidth = (int) width;
                updateContentRightEdge();
                updateLayout();
            }

            mTimeTextBorderLeft = mContentEdgeRight - timeTextWidth;
            mDayTextBorderLeft = mTimeTextBorderLeft - dayTextWidth
                    - (timeTextWidth > 0 ? mDayTextRightMargin : 0);

            invalidate();
        } else if (timeText == null && dayText == null && mDayText != null && mTimeText != null) {
            mDayText = null;
            mTimeText = null;
            postInvalidateOnAnimation();
        }
    }

    /**
     * Метод для обновления даты в хэдере по формату:
     * <ul>
     * <li>Если сегодня - выводим только время (14:56)</li>
     * <li>Если в этом году - выводим число и месяц (23.08)</li>
     * <li>Если не в этом году - выводим число, месяц и год (23.08.13)</li>
     * </ul>
     * Стандарт из <b><a href=https://online.sbis.ru/opendoc.html?guid=6a91ca08-d256-4f0c-9e68-ed5b29c3d1bc>задачи</a></b>
     *
     * @param date Дата для вывода в хэдере
     */
    public void updateHeader(Date date) {
        mDateText = DateFormatUtils.formatDateOrTimeUsingOnlyNumbers(date);

        //measure baseline
        mTextPaint.getTextBounds(mDateText, 0, mDateText.length(), mRect);
        mTextBaseline = mVerticalCenter + mRect.height() / 2f - mRect.bottom;

        int dateTextWidth = getPaddingLeft() + getPaddingRight() + mRect.width();
        if (mViewWidth != dateTextWidth) {
            mViewWidth = dateTextWidth;
            updateContentRightEdge();
            updateLayout();
        }

        invalidate();
    }

    protected void updateLayout() {
        requestLayout();
    }

    private void updateContentRightEdge() {
        mContentEdgeRight = mViewWidth - getPaddingRight();
    }

    /**
     * Метод для выделения времени.
     *
     * @param shouldHighlightTime должно ли быть выделено время.
     */
    public void highlightTime(boolean shouldHighlightTime) {
        boolean invalidate = false;
        if (shouldHighlightTime) {
            if (mChangeableTextColor != mAccentTextColor) {
                invalidate = true;
            }
            mChangeableTextColor = mAccentTextColor;
        } else {
            if (mChangeableTextColor != mDefaultTextColor) {
                invalidate = true;
            }
            mChangeableTextColor = mDefaultTextColor;
        }
        if (invalidate) {
            invalidate();
        }
    }
}