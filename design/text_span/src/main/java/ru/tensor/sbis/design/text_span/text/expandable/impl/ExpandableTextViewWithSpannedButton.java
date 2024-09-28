package ru.tensor.sbis.design.text_span.text.expandable.impl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import ru.tensor.sbis.design.text_span.R;
import ru.tensor.sbis.design.text_span.text.expandable.AbstractExpandableTextView;

/**
 * Реализация TextView с возможность сворачивания/разворачивания с CharSequence в качестве кнопки.
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class ExpandableTextViewWithSpannedButton extends AbstractExpandableTextView {

    /**
     * Размер текста кнопки соответствует размеру текста в TextView.
     */
    public static final int BUTTON_TEXT_SIZE_SAME_AS_TEXT_SIZE = -1;

    private static final int DEFAULT_EXPAND_TEXT_RES_ID = R.string.text_span_expand_text;
    private static final int DEFAULT_COLLAPSE_TEXT_RES_ID = R.string.text_span_collapse_text;
    private static final int DEFAULT_BUTTON_TEXT_SIZE = BUTTON_TEXT_SIZE_SAME_AS_TEXT_SIZE;
    private static final int DEFAULT_BUTTON_COLOR_RES_ID = R.color.text_span_expandable_text_view_default_button_color;

    // Configuration
    /**
     * Текст кнопки "Развернуть".
     */
    private CharSequence mExpandText;

    /**
     * Текст кнопки "Свернуть".
     */
    private CharSequence mCollapseText;

    /**
     * Высота текста кнопки в пикселях.
     */
    private float mButtonTextSize;

    /**
     * Цвет текста кнопки.
     */
    private int mButtonColor;

    /**
     * Кисть для отрисовки кнопок.
     */
    private final TextPaint mButtonPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    // Cache
    private StaticLayout mExpandLayout;
    private StaticLayout mCollapseLayout;

    public ExpandableTextViewWithSpannedButton(Context context) {
        this(context, null);
    }

    public ExpandableTextViewWithSpannedButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextViewWithSpannedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableTextViewWithSpannedButton);
            mExpandText = typedArray.getString(R.styleable.ExpandableTextViewWithSpannedButton_expandText);
            mCollapseText = typedArray.getString(R.styleable.ExpandableTextViewWithSpannedButton_collapseText);
            mButtonTextSize = typedArray.getDimensionPixelSize(R.styleable.ExpandableTextViewWithSpannedButton_buttonTextSize, DEFAULT_BUTTON_TEXT_SIZE);
            int defaultColor = ContextCompat.getColor(getContext(), DEFAULT_BUTTON_COLOR_RES_ID);
            mButtonColor = typedArray.getColor(R.styleable.ExpandableTextViewWithSpannedButton_buttonColor, defaultColor);
            typedArray.recycle();
        }
        if (mExpandText == null) {
            mExpandText = getContext().getString(DEFAULT_EXPAND_TEXT_RES_ID);
        }
        if (mCollapseText == null) {
            mCollapseText = getContext().getString(DEFAULT_COLLAPSE_TEXT_RES_ID);
        }
        mExpandText = buildButtonFromText(mExpandText);
        mCollapseText = buildButtonFromText(mCollapseText);
    }

    // region Properties
    /**
     * Задать текст, отображаемый в качестве кнопки "Развернуть".
     * @param expandText    - текст
     * @param styled        - стилизован ли текст
     */
    public void setExpandText(@Nullable CharSequence expandText, boolean styled) {
        if (!TextUtils.equals(mExpandText, expandText)) {
            mExpandText = styled ? expandText : buildButtonFromText(expandText);
            mExpandLayout = null;
            if (isCollapsed() && getButtonType() != BUTTON_TYPE_NONE) {
                // Кнопка "Развернуть" сейчас отображается, поэтому обновляем макет
                requestLayoutIfButtonVisible();
            }
        }
    }

    /**
     * Задать текст, отображаемый в качестве кнопки "Свернуть".
     * @param collapseText  - текст
     * @param styled        - стилизован ли текст
     */
    public void setCollapseText(@Nullable CharSequence collapseText, boolean styled) {
        if (!TextUtils.equals(mCollapseText, collapseText)) {
            mCollapseText = styled ? collapseText : buildButtonFromText(collapseText);
            mCollapseLayout = null;
            if (isExpanded() && getButtonType() != BUTTON_TYPE_NONE) {
                // Кнопка "Свернуть" сейчас отображается, поэтому обновляем макет
                requestLayoutIfButtonVisible();
            }
        }
    }

    /**
     * Задать размер текста кнопки.
     * @param buttonTextSize - размер текста кнопки в пикселях.
     */
    public void setButtonTextSize(float buttonTextSize) {
        if (mButtonTextSize != buttonTextSize) {
            mButtonTextSize = buttonTextSize;
            notifyButtonChanged();
        }
    }
    // endregion


    // region AbstractExpandableTextView impl
    /**
     * Получить высоту кнопки "Раскрыть" в пикселях.
     * @return высота кнопки
     */
    @Override
    protected int getExpandButtonHeight() {
        StaticLayout expandButton = getExpandButton();
        return expandButton != null ? expandButton.getHeight() : 0;
    }

    /**
     * Получить высоту кнопки "Свернуть" в пикселях.
     * @return высота кнопки
     */
    @Override
    protected int getCollapseButtonHeight() {
        StaticLayout collapseButton = getCollapseButton();
        return collapseButton != null ? collapseButton.getHeight() : 0;
    }

    @Override
    protected void drawButton(Canvas canvas, Rect rect) {
        // Получаем макет кнопки
        StaticLayout layout = isExpanded() ? getCollapseButton() : getExpandButton();
        if (layout != null) {
            canvas.save();
            // Смещаем макет на позицию кнопки
            canvas.translate(rect.left, rect.top);
            // Отрисовываем кнопку
            layout.draw(canvas);
            canvas.restore();
        }
    }
    // endregion


    // region Internal
    /**
     * Получить ширину view за вычетом отступов.
     * @return активная ширина view
     */
    private int getActiveWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * Получить макет кнопки "Развернуть".
     * @return экземпляр макета для кнопки
     */
    @Nullable
    private StaticLayout getExpandButton() {
        if (mExpandText == null) {
            mExpandLayout = null;
        } else if (mExpandLayout == null) {
            mExpandLayout = new StaticLayout(mExpandText, getButtonPaint(), getActiveWidth(),
                    Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        }
        return mExpandLayout;
    }

    /**
     * Получить макет кнопки "Свернуть".
     * @return экземпляр макета для кнопки
     */
    @Nullable
    private StaticLayout getCollapseButton() {
        if (mCollapseText == null) {
            mCollapseLayout = null;
        } else if (mCollapseLayout == null) {
            mCollapseLayout = new StaticLayout(mCollapseText, getButtonPaint(), getActiveWidth(),
                    Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        }
        return mCollapseLayout;
    }

    /**
     * Получить кисть для отрисовки кнопки.
     * @return экземпляр TextPaint
     */
    @NonNull
    protected TextPaint getButtonPaint() {
        float textSize = mButtonTextSize == BUTTON_TEXT_SIZE_SAME_AS_TEXT_SIZE
                ? getTextSize()
                : mButtonTextSize;
        // Обновляем размер текста для кисти
        mButtonPaint.setTextSize(textSize);
        mButtonPaint.setTypeface(getPaint().getTypeface());
        return mButtonPaint;
    }

    /**
     * Применить стиль кнопки к тексту.
     * @param text - текст кнопки
     * @return текст, стилизованный под кнопку
     */
    @Nullable
    private CharSequence buildButtonFromText(@Nullable CharSequence text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        SpannableString string = new SpannableString(text);
        ForegroundColorSpan textColorSpan = new ForegroundColorSpan(mButtonColor);
        string.setSpan(textColorSpan, 0, string.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return string;
    }
    // endregion


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthBefore = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != widthBefore) {
            // Если изменилась ширина view - сбрасываем кешированные макеты кнопок
            mExpandLayout = null;
            mCollapseLayout = null;
        }
    }

}
