package ru.tensor.sbis.richtext.view.strategy.bounds;

import android.text.Editable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Утилита для трансформации границ спанов при добавлении текста перед ними
 *
 * @author am.boldinov
 */
public final class StartSpanBoundsTransformer extends BaseSpanBoundsTransformer {

    public StartSpanBoundsTransformer(@NonNull Class<?> transformClass) {
        super(transformClass);
    }

    @Override
    void onAfterTextInserted(@NonNull Editable text, @NonNull Class<?> transformClass, int newPosition) {
        if (SpannableUtil.hasNextSpanTransition(text, newPosition - 1, newPosition + 1, transformClass)) {
            final Object[] spans = text.getSpans(newPosition, newPosition + 1, transformClass);
            for (Object span : spans) {
                if (text.getSpanStart(span) == newPosition) {
                    final int end = text.getSpanEnd(span);
                    final int flags = text.getSpanFlags(span);
                    text.setSpan(span, mInsertPosition, end, flags);
                }
            }
        }
    }
}
