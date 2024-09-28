package ru.tensor.sbis.richtext.view.strategy.bounds;

import android.text.Editable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.util.SpannableUtil;

/**
 * Утилита для трансформации границ спанов при добавлении текста после них
 *
 * @author am.boldinov
 */
public final class EndSpanBoundsTransformer extends BaseSpanBoundsTransformer {

    public EndSpanBoundsTransformer(@NonNull Class<?> transformClass) {
        super(transformClass);
    }

    @Override
    void onAfterTextInserted(@NonNull Editable text, @NonNull Class<?> transformClass, int newPosition) {
        if (SpannableUtil.hasNextSpanTransition(text, mInsertPosition - 1, mInsertPosition + 1, transformClass)) {
            final Object[] spans = text.getSpans(mInsertPosition - 1, mInsertPosition, transformClass);
            for (Object span : spans) {
                if (text.getSpanEnd(span) == mInsertPosition) {
                    final int start = text.getSpanStart(span);
                    final int flags = text.getSpanFlags(span);
                    text.setSpan(span, start, newPosition, flags);
                }
            }
        }
    }
}
