package ru.tensor.sbis.richtext.view.strategy;

import android.text.Editable;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.view.View;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.SpannableUtil;
import ru.tensor.sbis.richtext.util.StaticLayoutProxy;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import timber.log.Timber;

/**
 * Утилита для работы с переносами строк Spannable текста
 *
 * @author am.boldinov
 */
public final class SpannableLineBreakHandler {

    @Nullable
    private final List<RichViewLayout.LayoutParams> mLayoutParamsBuffer;
    @Nullable
    private final RichViewLayout mRichViewLayout;

    public SpannableLineBreakHandler() {
        mLayoutParamsBuffer = null;
        mRichViewLayout = null;
    }

    public SpannableLineBreakHandler(@NonNull RichViewLayout richViewLayout) {
        mLayoutParamsBuffer = new ArrayList<>();
        mRichViewLayout = richViewLayout;
    }

    /**
     * Удаляет пустые переносы строк. Необходимо использовать в случае,
     * когда нет доступа к {@link android.widget.TextView}, к примеру в фоновом потоке подготовки данных
     *
     * @param text              текст для модификации
     * @param maxLineBreakCount максимальное количество возможных переносов строк
     */
    public void removeEmptyLineBreaks(@NonNull Editable text, int maxLineBreakCount) {
        final Layout layout = StaticLayoutProxy.create(text, new TextPaint(), Integer.MAX_VALUE);
        removeEmptyLineBreaks(text, layout, maxLineBreakCount);
    }

    /**
     * Удаляет пустые переносы строк
     *
     * @param text              текст для модификации
     * @param layout            строковое представление
     * @param maxLineBreakCount максимальное количество возможных переносов строк
     */
    public void removeEmptyLineBreaks(@NonNull Editable text, @NonNull ViewLayout layout, int maxLineBreakCount) {
        layout.lockReflow();
        try {
            removeEmptyLineBreaks(text, layout.getTextLayout(), maxLineBreakCount);
        } catch (Exception e) {
            Timber.e(e);
        }
        layout.unlockReflow();
    }

    private void removeEmptyLineBreaks(@NonNull Editable text, @NonNull Layout layout, int maxLineBreakCount) {
        if (text.length() == 0) {
            return;
        }
        for (int line = layout.getLineCount() - 1; line >= 0; line--) {
            int start = layout.getLineStart(line);
            final int end = layout.getLineEnd(line);
            boolean ignoreLineSpans = false;
            if (start == end) { // странным образом работает layout, из-за спанов добавляя последнюю строку, по факту у которой нет символа
                start--;
                ignoreLineSpans = true;
            }
            if (start >= text.length() || end > text.length() || !ignoreLineSpans && line > 0
                    && checkIgnoreRemoveLineBreak(text, layout, line, maxLineBreakCount)) {
                continue;
            }
            if (checkIsLineBreakRemoveCandidate(text, start, end, ignoreLineSpans)) {
                final int bottom = layout.getLineBottom(line);
                final int top = layout.getLineTop(line);
                final int lineHeight = bottom - top;
                if (lineHeight <= 0) {
                    continue;
                }
                boolean containsView = false;
                if (mRichViewLayout != null && mLayoutParamsBuffer != null) {
                    for (int i = mRichViewLayout.getChildCount() - 1; i >= 0; i--) {
                        final View view = mRichViewLayout.getChildAt(i);
                        if (view != mRichViewLayout.getTextView()) {
                            final RichViewLayout.LayoutParams layoutParams = (RichViewLayout.LayoutParams) view.getLayoutParams();
                            final int viewBottom = layoutParams.topOffset + view.getMeasuredHeight();
                            if (bottom < layoutParams.topOffset) {
                                mLayoutParamsBuffer.add(layoutParams);
                            } else if (bottom > layoutParams.topOffset && bottom <= viewBottom || viewBottom >= top) {
                                containsView = true;
                                break;
                            }
                        }
                    }
                    if (!containsView) {
                        containsView = SpannableUtil.hasNextSpanTransition(text, start, end + 1, WrapLineSpan.class);
                    }
                }
                if (!containsView) {
                    if (mLayoutParamsBuffer != null) {
                        for (RichViewLayout.LayoutParams layoutParams : mLayoutParamsBuffer) {
                            layoutParams.topOffset -= lineHeight;
                        }
                    }
                    final int oldCount = layout.getLineCount();
                    text.replace(start, end, StringUtils.EMPTY);
                    final int diffCount = oldCount - layout.getLineCount();
                    if (diffCount > 1) {
                        line -= diffCount - 1; // на итерации цикла уменьшится дополнительно на единицу
                    }
                }
                if (mLayoutParamsBuffer != null) {
                    mLayoutParamsBuffer.clear();
                }
            }
        }
    }

    /**
     * Проверяет необходимость удаления текущего переноса в зависимости от предыдущих строк
     */
    private boolean checkIgnoreRemoveLineBreak(@NonNull Editable text, @NonNull Layout layout, int line, int maxLineBreakCount) {
        if (maxLineBreakCount > 1) {
            final int minLine = Math.max(line - (maxLineBreakCount - 1), 0);
            for (int i = line - 1; i >= minLine; i--) {
                final int start = layout.getLineStart(i);
                final int end = layout.getLineEnd(i);
                // если предыдущая строка не является переносом, то игнорируем текущую строку для того, чтоб оставить перенос
                if (!HtmlHelper.isBreakLine(text, start, end)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Проверяет необходимость удаления текущего переноса на основе содержимого текущей строки
     */
    private boolean checkIsLineBreakRemoveCandidate(@NonNull Editable text, int start, int end, boolean ignoreLineSpans) {
        if (HtmlHelper.isBreakLine(text, start, end)) {
            if (ignoreLineSpans) {
                return true;
            } else {
                final boolean hasLineHeight = SpannableUtil.hasNextSpanTransition(text, start, end + 1, LineHeightSpan.class);
                if (!hasLineHeight) {
                    final boolean hasLeadingMargin = SpannableUtil.hasNextSpanTransition(text, start, end + 1, LeadingMarginSpan.class);
                    // если это leading margin перенос, то проверим предущий символ, если он так же является переносом, то этот можно удалить
                    return !hasLeadingMargin || start == 0 || text.charAt(start - 1) == CharUtils.LF;
                }
            }
        }
        return false;
    }
}
