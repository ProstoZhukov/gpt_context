package ru.tensor.sbis.design.scroll_to_top;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import kotlin.Deprecated;
import kotlin.jvm.functions.Function1;
import ru.tensor.sbis.design.SbisMobileIcon;

/**
 * ScrollToTop (Скролл в самый верх).
 *
 * @author du.bykov
 */
@Deprecated(message = "Отказываемся от компонента, теперь скролл будет проходить по ННП, событие отлавливается в ScrollToTopSubscriptionHolder")
public class ScrollToTop extends ViewGroup {

    private static final int BAR_HEIGHT = 30;
    private static final int BAR_ELEVATION = 32;
    private static final int MARGIN = 12;
    private static final int TITLE_TEXT_COLOR = ru.tensor.sbis.design.R.color.palette_color_white0;
    private static final int TITLE_TEXT_SIZE = 14;
    private static final int ICON_COLOR = ru.tensor.sbis.design.R.color.palette_color_gray9;
    private static final int ICON_SIZE = 20;
    private static final int CIRCLE_SIZE = 14;
    private static final int STROKE_COLOR = ru.tensor.sbis.design.R.color.color_primary;
    private static final boolean IS_RIGHT_DRAWABLE_VISIBLE_BY_DEFAULT = false;
    private static final boolean HAS_NO_CUSTOM_VIEW = false;
    //индекс, по которому в иерархии представлений будет находиться кастомная вьюгруппа (если она добавлена в layout)
    private static final int CUSTOM_VIEW_LAYOUT_HIERARCHY_INDEX = 0;
    private static final int CUSTOM_VIEW_NEIGHBOUR_LIMIT = 1; //лимит количества View, находящихся на одном уровне с кастомной вью.

    @NonNull
    private Analytics mAnalytics;

    @Nullable
    private TextView mTitleTextView;
    @Nullable
    private TextView mIconTextView;
    @Nullable
    private TextView mRightTextView;
    @Nullable
    private View mCircleView;
    @Nullable
    private View mCustomView;

    private boolean hasCustomView;

    private final Paint paint;

    private boolean mResetVisibilityFromState = true;

    public ScrollToTop(Context context) {
        this(context, null);
    }

    public ScrollToTop(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.ScrollToTopStyle);
    }

    public ScrollToTop(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ScrollToTop_style, R.style.ScrollToTopStyle);
    }

    public ScrollToTop(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mAnalytics = new Analytics(context.getApplicationContext());

        setClickable(true);

        paint = new Paint();
        paint.setStrokeWidth(1);

        boolean hasElevation = true;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.ScrollToTop,
                    defStyleAttr,
                    defStyleRes);
            try {
                //common attrs
                hasElevation = a.getBoolean(R.styleable.ScrollToTop_hasElevation, true);

                hasCustomView = a.getBoolean(R.styleable.ScrollToTop_hasCustomView, HAS_NO_CUSTOM_VIEW);

                if (!hasCustomView) {
                    //не найден флаг в аттрибутах, указывающий на кастомное содержимое, добавляем чайлдов по-умолчанию
                    createDefaultViews();
                    //specified attrs
                    if (a.hasValue(R.styleable.ScrollToTop_title)) {
                        setTitle(a.getText(R.styleable.ScrollToTop_title));
                    }
                    if (a.hasValue(R.styleable.ScrollToTop_rightText)) {
                        setRightText(a.getText(R.styleable.ScrollToTop_rightText));
                    }
                    if (a.hasValue(R.styleable.ScrollToTop_rightDrawable)) {
                        setRightDrawable(a.getDrawable(R.styleable.ScrollToTop_rightDrawable));
                    }
                    if (a.hasValue(R.styleable.ScrollToTop_rightTextVisible)) {
                        setRightTextVisible(a.getBoolean(R.styleable.ScrollToTop_rightTextVisible, true));
                    }
                    if (a.hasValue(R.styleable.ScrollToTop_rightDrawableVisible)) {
                        setRightDrawableVisible(a.getBoolean(R.styleable.ScrollToTop_rightDrawableVisible, IS_RIGHT_DRAWABLE_VISIBLE_BY_DEFAULT));
                    }
                    if (a.hasValue(R.styleable.ScrollToTop_titleTextColor)) {
                        setTitleTextColor(a.getColor(R.styleable.ScrollToTop_titleTextColor, ContextCompat.getColor(getContext(), TITLE_TEXT_COLOR)));
                    }
                    if (a.hasValue(R.styleable.ScrollToTop_arrowColor)) {
                        setArrowIconColor(a.getColor(R.styleable.ScrollToTop_arrowColor, ContextCompat.getColor(getContext(), ICON_COLOR)));
                    }
                }

                int defaultStrokeColor = ContextCompat.getColor(getContext(), STROKE_COLOR);
                paint.setColor(a.getColor(R.styleable.ScrollToTop_strokeColor, defaultStrokeColor));
            } finally {
                a.recycle();
            }
        } else {
            createDefaultViews();
        }

        if (hasElevation) {
            ViewCompat.setElevation(this, BAR_ELEVATION);
        }

        if (getBackground() == null) {
            setBackgroundColor(ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.color_primary));
        }
    }

    /** SelfDocumented */
    public void allowResetVisibilityFromSavedState(boolean resetVisibilityFromState) {
        mResetVisibilityFromState = resetVisibilityFromState;
    }

    private void prepareTextView(@NonNull TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TITLE_TEXT_SIZE);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        TextViewCompat.setTextAppearance(textView, ru.tensor.sbis.design.R.style.RegularStyle);
    }

    /**
     * Возвращает {@link TextView} заголовка.
     * @return {@link TextView}
     */
    @Nullable
    public TextView getTitleTextView() {
        if (mTitleTextView == null) {
            mTitleTextView = getActualView(mCustomView, ScrollableToTop::getTitleTextView);
        }
        return mTitleTextView;
    }

    /**
     * Возвращает {@link TextView} иконки.
     * @return {@link TextView}
     */
    @Nullable
    public TextView getIconTextView() {
        if (mIconTextView == null) {
            mIconTextView = getActualView(mCustomView, ScrollableToTop::getIconTextView);
        }
        return mIconTextView;

    }

    /**
     * Возвращает {@link TextView} текста справа.
     * @return {@link TextView}
     */
    @Nullable
    public TextView getRightTextView() {
        if (mRightTextView == null) {
            mRightTextView = getActualView(mCustomView, ScrollableToTop::getRightTextView);
        }
        return mRightTextView;
    }

    /**
     * Установка заголовка.
     * @param title заголовок.
     */
    public void setTitle(@Nullable CharSequence title) {
        TextView titleView = getTitleTextView();
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    /**
     * @return текущий заголовок.
     */
    public CharSequence getTitle() {
        TextView titleView = getTitleTextView();
        if (titleView != null) {
            return titleView.getText();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Установка текста справа.
     * @param text текст справа.
     */
    public void setRightText(@Nullable CharSequence text) {
        TextView rightText = getRightTextView();
        if (rightText != null) {
            rightText.setText(text);
        }
    }

    /**
     * @return текст справа.
     */
    public CharSequence getRightText() {
        TextView rightTextView = getRightTextView();
        if (rightTextView != null) {
            return rightTextView.getText();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Возвращает кастомную {@link View}
     * @return {@link View}
     */
    @Nullable
    public View getCustomView() {
        return mCustomView;
    }

    /**
     * Установка изображения справа.
     * @param drawable изображение, которое должно быть отображено справа.
     */
    public void setRightDrawable(Drawable drawable) {
        if (mCircleView != null) {
            mCircleView.setBackground(drawable);
        }
    }

    /**
     * Установка видимости текста справа.
     * @param visible boolean
     */
    public void setRightTextVisible(boolean visible) {
        TextView rightTextView = getRightTextView();
        if (rightTextView != null) {
            rightTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Установка видимости правой иконки.
     */
    public void setRightDrawableVisible(boolean visible) {
        if (mCircleView != null) {
            mCircleView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Установка цвета заголовка.
     */
    public void setTitleTextColor(int color) {
        if (getTitleTextView() != null) {
            getTitleTextView().setTextColor(color);
        }

        if (getRightTextView() != null) {
            getRightTextView().setTextColor(color);
        }
    }

    /**
     * Установка цвета стрелочки.
     */
    public void setArrowIconColor(int color) {
        if (mIconTextView != null) {
            mIconTextView.setTextColor(color);
        }
    }

    @Override
    public boolean performClick() {
        boolean isClicked = super.performClick();
        if (isClicked) {
            mAnalytics.reportScrollToTopClicked();
        }
        return isClicked;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCustomView = tryGetCustomView();
        if (mCustomView != null) {
            if (!isCustomViewAloneInLayout()) {
                throw new IllegalStateException("ScrollToTop must contain only one children " +
                        "- custom view! Current child count: "
                        + getChildCount());
            }
            if (!(mCustomView instanceof ViewGroup)) {
                throw new IllegalStateException("Expected a ViewGroup but was " + mCustomView.getClass().getSimpleName() + " in ScrollToTop");
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // Если в ScrollToTop помещена иная ViewGroup - ограничиваемся только ей
        if (mCustomView != null) {
            int customViewTop = (height - mCustomView.getMeasuredHeight()) / 2;
            int customViewBottom = customViewTop + mCustomView.getMeasuredHeight();
            mCustomView.layout(0, customViewTop, width, customViewBottom);
            return;
        }

        //заголовок
        int titleTextRight = 0;
        // Пробуем закешировать тайтл из кастомной вью. mTitleTextView всё еще может остаться пустым,
        // тк тайтла в кастомной вью может не быть
        final TextView titleTextView = getTitleTextView();
        if (titleTextView != null) {
            FrameLayout.LayoutParams titleLp = (FrameLayout.LayoutParams) titleTextView.getLayoutParams();
            int titleTextLeft = titleLp.leftMargin;
            titleTextRight = titleTextLeft + titleTextView.getMeasuredWidth();
            int titleTextTop = (height - titleTextView.getMeasuredHeight()) / 2;
            int titleTextBottom = titleTextTop + titleTextView.getMeasuredHeight();
            titleTextView.layout(titleTextLeft, titleTextTop, titleTextRight, titleTextBottom);

        }
        // текст справа
        final TextView rightTextView = getRightTextView();
        if (rightTextView != null && hasRightText()) {
            FrameLayout.LayoutParams rightTextLp = (FrameLayout.LayoutParams) rightTextView.getLayoutParams();
            int rightTextRight = width - rightTextLp.rightMargin;
            int rightTextLeft = rightTextRight - rightTextView.getMeasuredWidth();
            int rightTextTop = (height - rightTextView.getMeasuredHeight()) / 2;
            int rightTextBottom = rightTextTop + rightTextView.getMeasuredHeight();
            rightTextView.layout(rightTextLeft, rightTextTop, rightTextRight, rightTextBottom);
        }

        // offline_indicator слева от текста справа
        if (mCircleView != null && hasRightDrawable()) {
            FrameLayout.LayoutParams circleLp = (FrameLayout.LayoutParams) mCircleView.getLayoutParams();
            int circleRight = width;
            if (hasRightText() && mRightTextView != null) {
                circleRight = mRightTextView.getLeft() - circleLp.rightMargin;
            }
            int circleLeft = circleRight - mCircleView.getMeasuredWidth();
            int circleTop = (height - circleLp.height) / 2;
            int circleBottom = circleTop + mCircleView.getMeasuredHeight();
            mCircleView.layout(circleLeft, circleTop, circleRight, circleBottom);
        }

        //иконка
        final TextView iconTextView = getIconTextView();
        if (iconTextView != null) {
            FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) iconTextView.getLayoutParams();
            int minIconLeft = titleTextRight + iconLp.leftMargin;
            int maxIconRight = width - iconLp.rightMargin;
            if (hasRightDrawable()) {
                maxIconRight = mCircleView.getLeft() - iconLp.rightMargin;
            } else if (hasRightText() && mRightTextView != null) {
                maxIconRight = mRightTextView.getLeft() - iconLp.rightMargin;
            }
            int iconWidth = iconTextView.getMeasuredWidth();

            int desiredIconLeft = (width - iconWidth) / 2;
            int desiredIconRight = desiredIconLeft + iconWidth;
            int iconLeft = desiredIconLeft;

            if (desiredIconLeft < minIconLeft && !hasRightText()) {
                iconLeft = minIconLeft;
            } else if (desiredIconLeft < minIconLeft || desiredIconRight > maxIconRight) {
                iconLeft = (minIconLeft + maxIconRight - iconWidth) / 2;
            }
            int iconRight = iconLeft + iconWidth;

            int iconTop = (height - iconTextView.getMeasuredHeight()) / 2;
            int iconBottom = iconTop + iconTextView.getMeasuredHeight();
            iconTextView.layout(iconLeft, iconTop, iconRight, iconBottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        final int height = Math.round(metrics.density * BAR_HEIGHT);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, height);

        //Если в ScrollToTop помещена иная ViewGroup - ограничиваемся только ей
        if (mCustomView != null) {
            //Измеряем сразу с родительскими спецификациями: чайлд полностью занимает площадь родителя
            mCustomView.measure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        FrameLayout.LayoutParams iconLp = null;
        if (mIconTextView != null) {
            iconLp = (FrameLayout.LayoutParams) mIconTextView.getLayoutParams();
            measureChild(mIconTextView, MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        FrameLayout.LayoutParams circleLp = null;
        if (mCircleView != null) {
            circleLp = (FrameLayout.LayoutParams) mCircleView.getLayoutParams();
        }
        FrameLayout.LayoutParams titleLp = null;
        if (mTitleTextView != null) {
            titleLp = (FrameLayout.LayoutParams) mTitleTextView.getLayoutParams();
        }
        FrameLayout.LayoutParams rightTextLp = null;
        if (mRightTextView != null) {
            rightTextLp = (FrameLayout.LayoutParams) mRightTextView.getLayoutParams();
        }


        int circleWidthWithMargins = 0;
        if (hasRightDrawable()) {
            measureChild(mCircleView, MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            circleWidthWithMargins = mCircleView.getMeasuredWidth() + (circleLp != null ? circleLp.rightMargin : 0);
        }

        int textMargins = titleLp != null ? titleLp.leftMargin : 0;
        if (hasRightText() && rightTextLp != null) {
            textMargins += rightTextLp.rightMargin;
        }

        int occupiedWidth = (mIconTextView != null ? mIconTextView.getMeasuredWidth() : 0)
                + (iconLp != null ? iconLp.rightMargin : 0)
                + (iconLp != null ? iconLp.leftMargin : 0)
                + circleWidthWithMargins
                + textMargins;
        int availableForTextWidth = width - occupiedWidth;

        measureChild(mTitleTextView, MeasureSpec.makeMeasureSpec(availableForTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec);

        if (!hasRightText()) {
            //есть только заголовок
            return;
        }

        measureChild(mRightTextView, MeasureSpec.makeMeasureSpec(availableForTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        int desiredTitleWidth = mTitleTextView.getMeasuredWidth();
        int desiredRightTextWidth = mRightTextView.getMeasuredWidth();

        if (desiredTitleWidth + desiredRightTextWidth <= availableForTextWidth) {
            //заголовок и текст справа помещаются полностью
            return;
        }

        int defaultAvailableForTitleWidth = (availableForTextWidth + circleWidthWithMargins) / 2;
        int defaultAvailableForRightTextWidth = defaultAvailableForTitleWidth - circleWidthWithMargins;
        if (desiredTitleWidth <= defaultAvailableForTitleWidth) {
            //заголовок помещается полностью
            int availableForRightTextWidth = availableForTextWidth - desiredTitleWidth;
            measureChild(mRightTextView, MeasureSpec.makeMeasureSpec(availableForRightTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        } else if (desiredRightTextWidth <= defaultAvailableForRightTextWidth) {
            //текст справа помещается полностью
            int availableForTitleWidth = availableForTextWidth - desiredRightTextWidth;
            measureChild(mTitleTextView, MeasureSpec.makeMeasureSpec(availableForTitleWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        } else {
            //оба текста не помещаются полностью
            measureChild(mTitleTextView, MeasureSpec.makeMeasureSpec(defaultAvailableForTitleWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            measureChild(mRightTextView, MeasureSpec.makeMeasureSpec(defaultAvailableForRightTextWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, paint);
    }

    private void createDefaultViews() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int margin = (int) (metrics.density * MARGIN);

        mIconTextView = new TextView(getContext());
        mIconTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mIconTextView.setTypeface(ResourcesCompat.getFont(getContext(), ru.tensor.sbis.design.R.font.sbis_mobile_icons));
        mIconTextView.setText(String.valueOf(SbisMobileIcon.Icon.smi_STTnew.getCharacter()));
        mIconTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE);
        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        iconLp.leftMargin = margin;
        iconLp.rightMargin = margin;
        addView(mIconTextView, iconLp);

        mTitleTextView = new TextView(getContext());
        prepareTextView(mTitleTextView);
        FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        titleLp.leftMargin = margin;
        mTitleTextView.setLayoutParams(titleLp);
        addView(mTitleTextView, titleLp);

        mRightTextView = new TextView(getContext());
        prepareTextView(mRightTextView);
        FrameLayout.LayoutParams rightTextLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rightTextLp.rightMargin = margin;
        mRightTextView.setLayoutParams(rightTextLp);
        addView(mRightTextView, rightTextLp);

        mCircleView = new View(getContext());
        int circleSize = (int) (metrics.density * CIRCLE_SIZE);
        FrameLayout.LayoutParams circleLp = new FrameLayout.LayoutParams(circleSize, circleSize);
        circleLp.rightMargin = margin / 2;
        addView(mCircleView, circleLp);
        setRightDrawableVisible(IS_RIGHT_DRAWABLE_VISIBLE_BY_DEFAULT);
    }

    private boolean hasRightText() {
        if (mRightTextView != null) {
            return mRightTextView.getVisibility() != View.GONE && mRightTextView.getText().length() > 0;
        }
        return false;
    }

    private boolean hasRightDrawable() {
        if (mCircleView != null) {
            return mCircleView.getVisibility() != View.GONE;
        }
        return false;
    }

    @Nullable
    private View tryGetCustomView() {
        if (hasCustomView) {
            return getChildAt(CUSTOM_VIEW_LAYOUT_HIERARCHY_INDEX);
        }
        return null;
    }

    /**
     * Упрощенный метод получения желаемой дочерней вью из кастомного представления.
     * @param customView ссылка на кастомную вью
     * @param getIfCustom лямбда с методом получения необходимой дочерней вью из кастомной
     * @param <VIEW_TYPE> тип получаемой вью
     * @return дочерняя вью, или null - если родительская не реализует интерфейс {@link ScrollableToTop} или не имеет такого чайлда
     */
    @SuppressWarnings("unchecked")
    private <VIEW_TYPE extends View> VIEW_TYPE getActualView(@Nullable View customView, @NonNull Function1<ScrollableToTop, View> getIfCustom) {
        if (customView instanceof ScrollableToTop) {
            return (VIEW_TYPE) getIfCustom.invoke((ScrollableToTop) customView);
        }
        return null;
    }

    private boolean isCustomViewAloneInLayout() {
        return getChildCount() <= CUSTOM_VIEW_NEIGHBOUR_LIMIT;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (mResetVisibilityFromState) {
            setVisibility(savedState.visibility);
        }
    }

    /** SelfDocumented */
    protected static class SavedState extends BaseSavedState {
        final int visibility;

        private SavedState(Parcelable superState, int visibility) {
            super(superState);
            this.visibility = visibility;
        }

        private SavedState(Parcel in) {
            super(in);
            visibility = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(visibility);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Интерфейс, предоставляющий обратную совместимость из custom view в текущую view.
     * <br/>
     * <b>ВСЕ custom view, которые устанавливаются в эту view, должны наследовать этот интерфейс.</b>
     */
    public interface ScrollableToTop {

        /**
         * Получить view заголовка.
         * @return {@link TextView}
         */
        @Nullable
        TextView getTitleTextView();

        /**
         * Получить view иконки.
         * @return {@link TextView}
         */
        @Nullable
        TextView getIconTextView();

        /**
         * Получить view текста справа.
         * @return {@link TextView}
         */
        @Nullable
        TextView getRightTextView();

        /**
         * Установка заголовка.
         * @param title зоголовок
         */
        void setTitle(@Nullable CharSequence title);

        /**
         * Получить текст заголовка.
         * @return текст заголовока
         */
        @Nullable
        CharSequence getTitle();

        /**
         * Установить текст справа.
         * @param rightText текст справа
         */
        void setRightText(@Nullable CharSequence rightText);

        /**
         * Получить текст справа.
         * @return текст справа
         */
        @Nullable
        CharSequence getRightText();

        /**
         * Установить видимость текста справа.
         */
        void setRightTextVisible(boolean visible);

        /**
         * Установить цвет заголовка.
         */
        void setTitleTextColor(@ColorInt int color);

    }
}