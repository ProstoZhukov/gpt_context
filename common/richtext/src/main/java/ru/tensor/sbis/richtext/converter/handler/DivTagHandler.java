package ru.tensor.sbis.richtext.converter.handler;

import android.text.Editable;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.converter.TagAttributes;
import ru.tensor.sbis.richtext.converter.handler.base.MarkedTagHandler;
import ru.tensor.sbis.richtext.converter.handler.postprocessor.SpanPostprocessor;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Обработчик тега div, вставляет перенос строки в окончании тега, если это необходимо
 *
 * @author am.boldinov
 */
public class DivTagHandler extends MarkedTagHandler implements SpanPostprocessor {

    private int mDivStartDomPosition = 0;
    private int mPreviousEnumerationPosition = -1;

    @Override
    public void onStartTag(@NonNull Editable stream, @NonNull TagAttributes attributes) {
        mDivStartDomPosition++;
        final int enumerationPosition = findEnumerationParentPosition(stream);
        // если нет родителя перечисления либо это второй див внутри перечисления - маркируем
        if (enumerationPosition < 0 || mPreviousEnumerationPosition == enumerationPosition) {
            final DivMarkSpan span = new DivMarkSpan();
            span.setDomPosition(this, mDivStartDomPosition);
            mark(stream, span);
        }
        mPreviousEnumerationPosition = enumerationPosition;
    }

    @Override
    public void onEndTag(@NonNull Editable stream) {
        final DivMarkSpan span = SpannableUtil.getLast(stream, DivMarkSpan.class);
        if (span != null && span.containsDomPosition(this, mDivStartDomPosition)) {
            SpannableUtil.setSpanFromMark(stream, span, span.getRealSpan());
        }
        mDivStartDomPosition--;
    }

    @Override
    public void recycle() {
        mDivStartDomPosition = 0;
        mPreviousEnumerationPosition = -1;
    }

    @Override
    public void process(@NonNull Editable text) {
        final DivSpan[] spans = text.getSpans(0, text.length(), DivSpan.class);
        for (DivSpan span : spans) {
            final int end = text.getSpanEnd(span);
            if (end > 0 && end < text.length()) {
                if (text.charAt(end) != CharUtils.LF && text.charAt(end - 1) != CharUtils.LF) {
                    text.insert(end, StringUtils.LF);
                }
                mark(text, new MarkSpan.Paragraph(), end - 1);
            }
            text.removeSpan(span);
        }
    }

    private int findEnumerationParentPosition(@NonNull Editable stream) {
        final MarkSpan.LeadingMargin[] spans = stream.getSpans(0, stream.length(), MarkSpan.LeadingMargin.class);
        for (MarkSpan.LeadingMargin span : spans) {
            if (span instanceof MarkSpan.Bullet || span instanceof MarkSpan.Number) {
                return stream.getSpanStart(span);
            }
        }
        return -1;
    }

    private static final class DivMarkSpan extends MarkSpan {

        @NonNull
        @Override
        public Object getRealSpan() {
            return new DivSpan();
        }
    }

    private static final class DivSpan {

    }
}
