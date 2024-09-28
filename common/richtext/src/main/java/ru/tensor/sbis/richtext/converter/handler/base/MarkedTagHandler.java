package ru.tensor.sbis.richtext.converter.handler.base;

import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;

import ru.tensor.sbis.richtext.converter.MarkSpan;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Обработчик тегов с возможностью отметки и последующей установки отмеченного span
 *
 * @author am.boldinov
 */
public abstract class MarkedTagHandler implements TagHandler {

    /**
     * Отметить позицию начала спана в конце текста
     * @param text поток текста
     * @param mark отметка
     */
    protected final void mark(@NonNull Editable text, @NonNull MarkSpan mark) {
        mark(text, mark, text.length());
    }

    /**
     * Отметить позицию начала спана
     * @param text поток текста
     * @param mark отметка
     * @param position позиция
     */
    protected final void mark(@NonNull Editable text, @NonNull MarkSpan mark, int position) {
        text.setSpan(mark, position, position, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    /**
     * Применить последний отмеченный span
     * @param text поток текста
     * @param kind класс отметки
     * @return true если span был успешно установлен
     */
    protected final boolean span(@NonNull Editable text, @NonNull Class<? extends MarkSpan> kind) {
        final MarkSpan span = SpannableUtil.getLast(text, kind);
        return span != null && SpannableUtil.setSpanFromMark(text, span, span.getRealSpan());
    }
}
