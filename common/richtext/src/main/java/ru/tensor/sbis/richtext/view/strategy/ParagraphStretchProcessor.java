package ru.tensor.sbis.richtext.view.strategy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ReplacementSpan;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.span.decoratedlink.DecoratedLinkSpan;
import ru.tensor.sbis.richtext.util.HtmlHelper;
import ru.tensor.sbis.richtext.util.SpannableUtil;
import ru.tensor.sbis.richtext.view.ViewTemplate;

/**
 * Обработчик параграфа для растягивания его нижней границы по высоте.
 *
 * @author am.boldinov
 */
class ParagraphStretchProcessor {

    private static final String DECORATED_LINK_END_STRETCH = HtmlHelper.VIEW_SYMBOL + StringUtils.LF;
    private static final String DECORATED_LINK_FULL_STRETCH = StringUtils.LF + DECORATED_LINK_END_STRETCH;

    /**
     * Растягивает нижнюю границу параграфа по высоте.
     * При необходимости может модифицировать текст, добавляя переносы строк и увеличивая количество строк.
     *
     * @param layout      Layout для работы с текстом и обтекаемыми View
     * @param stretchLine номер строки, которую необходимо растянуть
     * @param stretchSize высота, на которую необходимо растянуть параграф. В случае передачи
     *                    нулевого значения будет добавлен перенос строки, если он отсутствует на
     *                    переданной строке
     */
    void process(@NonNull ViewLayout layout, int stretchLine, int stretchSize) {
        final Editable text = layout.getText();
        final int prevStretchLine = stretchLine;
        if (stretchSize > 0) {
            stretchLine = validateStretchLine(layout, stretchLine);
        }
        int end = layout.getLineEnd(stretchLine);
        if (end < text.length() && text.charAt(end) != CharUtils.LF && text.charAt(end - 1) != CharUtils.LF) {
            if (text.charAt(end - 1) == ' ') { // если перед переносом строки окажется пробел, то он может увеличить количество строк после добавления переноса
                text.replace(end - 1, end, StringUtils.LF);
                end--;
            } else {
                text.insert(end, StringUtils.LF);
            }
        } else if (end == text.length() && !(end > 0 && text.charAt(end - 1) == CharUtils.LF)) {
            text.insert(end, StringUtils.CR); // LF при нахождении в конце добавляет лишний отступ
        } else {
            end--;
        }
        if (stretchSize > 0) {
            final int lineDiff = stretchLine - prevStretchLine;
            if (lineDiff > 0) {
                for (int i = 0; i < lineDiff; i++) {
                    final int line = stretchLine - i;
                    stretchSize -= layout.getLineBottom(line) - layout.getLineTop(line);
                }
            }
            if (stretchSize > 0) {
                final int lineHeight = layout.getLineBottom(stretchLine) - layout.getLineTop(stretchLine);
                text.setSpan(new StretchLineSpan(lineHeight + stretchSize), end, end + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static int validateStretchLine(@NonNull ViewLayout layout, int stretchLine) {
        final int end = layout.getLineEnd(stretchLine);
        if (SpannableUtil.hasNextSpanTransition(layout.getText(), end - 2, DecoratedLinkSpan.class)) {
            // декорированные ссылки не умеют растягиваться
            if (end > 0 && layout.getText().charAt(end - 1) != CharUtils.LF) {
                layout.getText().insert(end, DECORATED_LINK_FULL_STRETCH);
                stretchLine = layout.getLineForOffset(end + DECORATED_LINK_FULL_STRETCH.length() - 1);
            } else {
                layout.getText().insert(end, DECORATED_LINK_END_STRETCH);
                stretchLine = layout.getLineForOffset(end + DECORATED_LINK_END_STRETCH.length() - 1);
            }
        }
        return stretchLine;
    }

    private static final class StretchLineSpan extends ReplacementSpan implements WrapLineSpan {

        private final int mSize;

        StretchLineSpan(int size) {
            mSize = size;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            if (fm != null) {
                final int offset = mSize - (fm.descent - fm.ascent);
                if (offset > 0) {
                    fm.bottom += offset;
                    fm.descent = fm.bottom;
                }
            }
            return 0;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        }

        @Override
        public int getSize() {
            return getViewSize();
        }

        @Override
        public int getViewSize() {
            return 0;
        }

        @NonNull
        @Override
        public ViewTemplate getTemplate() {
            return ViewTemplate.INLINE;
        }
    }
}
