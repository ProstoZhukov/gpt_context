package ru.tensor.sbis.richtext.span;

import androidx.annotation.IntRange;

/**
 * Span для задания приоритета отрисовки, если происходит наложение спанов друг на друга.
 * Чаще всего необходимо использовать в {@link android.text.style.LeadingMarginSpan} для соблюдения последовательности отступов.
 *
 * @author am.boldinov
 */
public interface PrioritySpan {

    /**
     * Минимально-возможный приоритет
     */
    int MIN_PRIORITY = 0;
    /**
     * Пользовательский приоритет, который устанавливается в процессе конвертации
     * для всех спанов, не реализующих данный интерфейс
     */
    int USER_PRIORITY = 12;
    /**
     * Максимально-возможный приоритет
     */
    int MAX_PRIORITY = 255;

    @IntRange(from = MIN_PRIORITY, to = MAX_PRIORITY)
    int getPriority();

    /**
     * Обеспечивает необходимый порядок всех {@link android.text.style.LeadingMarginSpan}.
     * Чем выше приоритет, тем левее будет находиться спан.
     */
    final class LeadingMargin {
        public static final int ENUMERATION = USER_PRIORITY;
        public static final int ENUMERATION_NONE = USER_PRIORITY + 1;
        public static final int WRAP_VIEW = USER_PRIORITY + 2;
        public static final int BLOCK_QUOTE = MAX_PRIORITY;
    }
}
