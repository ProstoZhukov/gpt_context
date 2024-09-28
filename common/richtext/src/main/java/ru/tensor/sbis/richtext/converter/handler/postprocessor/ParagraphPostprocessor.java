package ru.tensor.sbis.richtext.converter.handler.postprocessor;

import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.LineHeightSpan;
import android.text.style.ParagraphStyle;

import org.apache.commons.lang3.CharUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.span.ParagraphLineSpacingSpan;
import ru.tensor.sbis.richtext.span.PrioritySpan;
import ru.tensor.sbis.richtext.util.HtmlHelper;

/**
 * Постобработчик параграфов
 *
 * @author am.boldinov
 */
public class ParagraphPostprocessor implements SpanPostprocessor {

    @Px
    private int mParagraphSpacing;

    /**
     * Устанавливает расстояние между параграфами
     */
    public void setParagraphSpacing(@Px int spacing) {
        mParagraphSpacing = spacing;
    }

    @Override
    public void process(@NonNull Editable text) {
        if (text.length() > 0) {
            final ParagraphStyle[] paragraphSpans = text.getSpans(0, text.length(), ParagraphStyle.class);
            int previousEnd = 0;
            for (ParagraphStyle paragraph : paragraphSpans) {
                if (mParagraphSpacing > 0) {
                    final int paragraphEnd = text.getSpanEnd(paragraph);
                    if (previousEnd != paragraphEnd) {
                        final int paragraphStart = text.getSpanStart(paragraph);
                        if (processParagraphSpacing(text, paragraph, mParagraphSpacing, paragraphStart, paragraphEnd)) {
                            previousEnd = paragraphEnd;
                        }
                    }
                }
                if (paragraph instanceof MarkSpan.Paragraph) {
                    text.removeSpan(paragraph);
                }
            }
        }
    }

    private static boolean processParagraphSpacing(@NonNull Editable text, @NonNull ParagraphStyle paragraph, int spacing,
                                                   int paragraphStart, int paragraphEnd) {
        if (paragraphEnd >= text.length() || (paragraph instanceof LineHeightSpan && !(paragraph instanceof PrioritySpan))
                || isEmptyParagraph(text, paragraph, paragraphStart, paragraphEnd)) {
            return false;
        }
        int spanEnd = 0;
        if (text.charAt(paragraphEnd) == CharUtils.LF) {
            spanEnd = paragraphEnd;
        } else if (paragraphEnd < text.length() - 1 && text.charAt(paragraphEnd + 1) == CharUtils.LF) {
            spanEnd = paragraphEnd + 1;
        }
        final int spanStart = spanEnd - 1;
        if (spanEnd > 0 && spanEnd < text.length()) {
            text.setSpan(new ParagraphLineSpacingSpan(spacing), spanStart, spanEnd,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE | PrioritySpan.MAX_PRIORITY << Spannable.SPAN_PRIORITY_SHIFT);
            return true;
        }
        return false;
    }

    private static boolean isEmptyParagraph(@NonNull Editable text, @NonNull ParagraphStyle paragraph,
                                            int paragraphStart, int paragraphEnd) {
        final boolean isEmptyCurrent = HtmlHelper.isBreakLine(text, paragraphStart, paragraphEnd);
        if (!isEmptyCurrent && paragraph instanceof MarkSpan.Paragraph) {
            // проверяем следующую строку является ли она пустой
            return (paragraphEnd < text.length() - 1 && HtmlHelper.isBreakLine(text, paragraphStart, paragraphEnd + 1))
                    || (paragraphEnd < text.length() - 2 && HtmlHelper.isBreakLine(text, paragraphStart, paragraphEnd + 2));
        }
        return isEmptyCurrent;
    }
}
