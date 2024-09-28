package ru.tensor.sbis.richtext.view.strategy;

import android.graphics.Paint;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import android.view.View.MeasureSpec;

import org.apache.commons.lang3.CharUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkSpan;
import ru.tensor.sbis.richtext.util.LayoutUtil;
import ru.tensor.sbis.richtext.util.SpannableUtil;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Расширение {@link android.text.DynamicLayout} для возможности работать с обтекаемыми View
 *
 * @author am.boldinov
 */
public final class ViewLayout {

    @NonNull
    private final Layout mLayout;
    @NonNull
    private final Editable mText;
    private final int mWidthMeasureSpec;
    private final int mHeightMeasureSpec;
    @NonNull
    private final TextPaint mWorkPaint = LayoutUtil.getWorkPaint();
    @NonNull
    private final Paint.FontMetricsInt mWorkMetrics = LayoutUtil.getWorkMetrics();
    @NonNull
    private final ReflowLock mReflowLock;

    public ViewLayout(@NonNull Layout layout, @NonNull CharSequence text, int widthMeasureSpec, int heightMeasureSpec) {
        mLayout = layout;
        mText = (Editable) text;
        mReflowLock = new ReflowLock(mText);
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
    }

    /**
     * Возвращает требование по режиму отображения и доступной ширине, которое накладывает
     * родительский элемент при измерении
     */
    public int getWidthMeasureSpec() {
        return mWidthMeasureSpec;
    }

    /**
     * Возвращает требование по режиму отображения и доступной высоте, которое накладывает
     * родительский элемент при измерении
     */
    public int getHeightMeasureSpec() {
        return mHeightMeasureSpec;
    }

    /**
     * Метод для вычисления параграфа, в отличие от дефолтного {@link Layout#getParagraphLeft(int)}
     * учитывает приоритет спанов
     */
    public int getPriorityParagraphLeft(int line, @Nullable LeadingMarginSpan stopSpan) {
        final Spanned text = (Spanned) mLayout.getText();
        final int lineStart = mLayout.getLineStart(line);
        final int lineEnd = mLayout.getLineEnd(line);
        final int spanEnd = text.nextSpanTransition(lineStart, lineEnd, LeadingMarginSpan.class);
        if (lineStart == spanEnd && lineStart > 0) {
            return 0;
        }
        final LeadingMarginSpan[] spans = text.getSpans(lineStart, spanEnd, LeadingMarginSpan.class);
        if (spans.length == 0) {
            return 0;
        }
        int margin = 0;
        final boolean useFirstLineMargin = lineStart == 0 || text.charAt(lineStart - 1) == CharUtils.LF;
        for (LeadingMarginSpan span : spans) {
            if (span == stopSpan) {
                break;
            }
            margin += span.getLeadingMargin(useFirstLineMargin);
        }
        return margin;
    }

    /**
     * Возвращает расстояние между top и ascent для строки.
     * Рекомендуется передавать позиции начала и конца только для первого/последнего символа строки
     */
    public int getTextPaddingTop(int start, int end) {
        mLayout.getPaint().getFontMetricsInt(mWorkMetrics);
        final Spannable text = (Spannable) mLayout.getText();
        if (SpannableUtil.hasNextSpanTransition(text, start - 1, end + 1, LineHeightSpan.class)) {
            final LineHeightSpan[] spans = text.getSpans(start, end, LineHeightSpan.class);
            if (spans.length > 0) {
                final LineHeightSpan span = spans[0];
                final int oldTop = mWorkMetrics.top;
                if (!(span instanceof DecoratedLinkSpan || span instanceof WrapLineSpan)) {
                    if (span instanceof LineHeightSpan.WithDensity) {
                        mWorkPaint.set(mLayout.getPaint());
                        ((LineHeightSpan.WithDensity) span).chooseHeight(text, start, end, 0, 0, mWorkMetrics, mWorkPaint);
                    } else {
                        span.chooseHeight(text, start, end, 0, 0, mWorkMetrics);
                    }
                }
                return Math.abs(mWorkMetrics.top - oldTop + mWorkMetrics.top - mWorkMetrics.ascent);
            }
        } else if (SpannableUtil.hasNextSpanTransition(text, start - 1, end + 1, MetricAffectingSpan.class)) {
            final MetricAffectingSpan[] spans = text.getSpans(start, end, MetricAffectingSpan.class);
            if (spans.length > 0) {
                mWorkPaint.set(mLayout.getPaint());
                spans[0].updateMeasureState(mWorkPaint);
                mWorkPaint.getFontMetricsInt(mWorkMetrics);
            }
        }
        return Math.abs(mWorkMetrics.top - mWorkMetrics.ascent);
    }

    /**
     * @see Layout#getText()
     */
    @NonNull
    public Editable getText() {
        return mText;
    }

    /**
     * Возвращает используемую ширину строки другими видами обтекания для конкретного
     * шаблона обтекания.
     * Необходимо использовать для решения конфликтов при обтекании на одной строке одновременно
     * нескольких View.
     *
     * @param line     номер строки
     * @param template шаблон обтекания
     */
    public int getUsedWidth(int line, @NonNull ViewTemplate template) {
        final int start = getLineStart(line);
        final int end = getLineEnd(line);
        int usedWidth = 0;
        final WrapLineSpan[] spans = getText().getSpans(start, end, WrapLineSpan.class);
        for (WrapLineSpan span : spans) {
            if ((template == ViewTemplate.LEFT || template == ViewTemplate.RIGHT)
                    && (span.getTemplate() == ViewTemplate.LEFT || span.getTemplate() == ViewTemplate.RIGHT)) {
                // на одной строке не может быть нескольких обтеканий слева и справа
                usedWidth = MeasureSpec.getSize(mWidthMeasureSpec);
                break;
            }
            usedWidth += span.getSize();
        }
        return usedWidth;
    }

    /**
     * Возвращает свободную (доступную) ширину для обтекания шаблона.
     * Если доступной ширины недостаточно для обтекания шаблона, рекомендуется изменить шаблон
     * или переместиться на следующую строку для исключения наложения шаблонов друг на друга.
     *
     * @param line     номер строки
     * @param template шаблон обтекания
     * @see ViewLayout#getUsedWidth(int, ViewTemplate)
     */
    public int getFreeWidth(int line, @NonNull ViewTemplate template) {
        return MeasureSpec.getSize(mWidthMeasureSpec) - getUsedWidth(line, template);
    }

    /**
     * Вычисляет возможность обтекания View по шаблону на конкретной строке.
     * Если обтекание недоступно (свободное пространство занято другими View или для шаблона
     * обтекание на этой строке недоступно), рекомендуется изменить шаблон или переместиться
     * на следующую строку для исключения наложения шаблонов друг на друга.
     *
     * @param viewWidth ширина обтекаемой View
     * @param line      номер строки
     * @param template  шаблон обтекания
     * @see ViewLayout#getUsedWidth(int, ViewTemplate)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean computeFitToLine(int viewWidth, int line, @NonNull ViewTemplate template) {
        final int used = getUsedWidth(line, template);
        if (used == 0) {
            // В случае отсутствия used рендерим так как посчитали при измерении View относительно
            // текущего MeasureSpec
            return true;
        }
        final int free = MeasureSpec.getSize(mWidthMeasureSpec) - used;
        return viewWidth <= free;
    }

    /**
     * Возвращает ширину для установки в качестве measuredDimension во View.
     * Рекомендуется использовать в случае если View рендерится как {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     */
    public int getCompactWidth() {
        final int mode = MeasureSpec.getMode(mWidthMeasureSpec);
        final int size = MeasureSpec.getSize(mWidthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return size;
        }
        final WrapLineSpan[] wrapLineSpans = getText().getSpans(0, getText().length(), WrapLineSpan.class);
        final int count = getLineCount();
        float max = 0;
        int spanStartIndex = 0;
        for (int i = 0; i < count; i++) {
            final int start = getLineStart(i);
            final int end = getLineEnd(i);
            int spanSingleLineWidth = 0;
            int spanVirtualWidth = 0;
            for (int j = spanStartIndex; j < wrapLineSpans.length; j++) {
                final WrapLineSpan span = wrapLineSpans[j];
                final boolean isSingleLineSpan;
                if ((isSingleLineSpan = span.getTemplate() == ViewTemplate.CENTER)
                        || !(span instanceof ReplacementSpan || span instanceof LeadingMarginSpan)) {
                    if (getText().getSpanStart(span) >= start && getText().getSpanEnd(span) <= end) {
                        if (isSingleLineSpan) {
                            spanSingleLineWidth = span.getViewSize() + getParagraphLeft(i);
                        } else {
                            spanVirtualWidth = span.getViewSize();
                        }
                        spanStartIndex = j + 1;
                    } else {
                        spanStartIndex = j; // на следующей строке начинаем проверку с этого элемента
                    }
                    break;
                } else if (j == wrapLineSpans.length - 1) {
                    spanStartIndex = Integer.MAX_VALUE; // не нашли необходимых спанов, исключаем последующие запуски поиска
                }
            }
            if (spanSingleLineWidth > 0) {
                max = Math.max(max, spanSingleLineWidth);
            } else {
                max = Math.max(max, mLayout.getLineWidth(i) + spanVirtualWidth);
            }
        }
        if (mode == MeasureSpec.AT_MOST) {
            max = Math.min(size, max);
        }
        return (int) Math.ceil(max);
    }

    /**
     * Возвращает высоту для установки в качестве measuredDimension во View.
     * Рекомендуется использовать в случае если View рендерится как {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     */
    public int getCompactHeight() {
        final int mode = MeasureSpec.getMode(mHeightMeasureSpec);
        final int size = MeasureSpec.getSize(mHeightMeasureSpec);
        int height = getHeight();
        if (mode == MeasureSpec.AT_MOST) {
            height = Math.min(height, size);
        }
        return height;
    }

    /**
     * Блокирует обновление макета и повторную генерацию текста при его изменении.
     * Необходимо использовать при массовом изменении текста для избежания лишних обновлений.
     * После завершения изменения обязательно вызвать {@link #unlockReflow()}.
     *
     * @see android.text.DynamicLayout
     */
    public void lockReflow() {
        mReflowLock.lock();
    }

    /**
     * Отключает блокировку обновления макета.
     * В случае если за время блокировки текст изменился запускает reflow у {@link android.text.DynamicLayout}.
     */
    public void unlockReflow() {
        mReflowLock.unlock();
    }

    /**
     * @see Layout#getLineCount()
     */
    public int getLineCount() {
        return mLayout.getLineCount();
    }

    /**
     * @see Layout#getLineTop(int)
     */
    public int getLineTop(int line) {
        return mLayout.getLineTop(line);
    }

    /**
     * @see Layout#getLineBottom(int)
     */
    public int getLineBottom(int line) {
        return mLayout.getLineBottom(line);
    }

    /**
     * @see Layout#getLineStart(int)
     */
    public int getLineStart(int line) {
        return mLayout.getLineStart(line);
    }

    /**
     * @see Layout#getLineEnd(int)
     */
    public int getLineEnd(int line) {
        return mLayout.getLineEnd(line);
    }

    /**
     * @see Layout#getLineForOffset(int)
     */
    public int getLineForOffset(int line) {
        return mLayout.getLineForOffset(line);
    }

    /**
     * @see Layout#getWidth()
     */
    public int getWidth() {
        return mLayout.getWidth();
    }

    /**
     * @see Layout#getHeight()
     */
    public int getHeight() {
        return mLayout.getHeight();
    }

    /**
     * @see Layout#getLineWidth(int)
     */
    public float getLineWidth(int line) {
        return mLayout.getLineWidth(line);
    }

    /**
     * @see Layout#getParagraphLeft(int)
     */
    public int getParagraphLeft(int line) {
        return mLayout.getParagraphLeft(line);
    }

    /**
     * @see Layout#getPrimaryHorizontal(int)
     */
    public float getPrimaryHorizontal(int offset) {
        return mLayout.getPrimaryHorizontal(offset);
    }

    /**
     * @see Layout#increaseWidthTo(int)
     */
    public void increaseWidthTo(int width) {
        mLayout.increaseWidthTo(width);
    }

    /**
     * @return экземпляр {@link android.text.DynamicLayout}, приаттаченный к {@link android.widget.TextView}
     */
    @NonNull
    public Layout getTextLayout() {
        return mLayout;
    }
}
