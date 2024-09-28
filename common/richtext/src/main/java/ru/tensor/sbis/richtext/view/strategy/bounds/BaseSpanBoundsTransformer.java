package ru.tensor.sbis.richtext.view.strategy.bounds;

import android.text.Editable;

import androidx.annotation.NonNull;

/**
 * Базовый класс для более удобной трасформации границ спанов
 *
 * @author am.boldinov
 */
abstract class BaseSpanBoundsTransformer implements SpanBoundsTransformer {

    @NonNull
    private final Class<?> mTransformClass;
    int mInsertPosition;
    private int mLength;

    BaseSpanBoundsTransformer(@NonNull Class<?> transformClass) {
        mTransformClass = transformClass;
    }

    public final void onBeforeTextInserted(@NonNull Editable text, int position) {
        mInsertPosition = position;
        mLength = text.length();
    }

    @Override
    public final void onAfterTextInserted(@NonNull Editable text) {
        final int newPosition = mInsertPosition + text.length() - mLength;
        onAfterTextInserted(text, mTransformClass, newPosition);
    }

    abstract void onAfterTextInserted(@NonNull Editable text, @NonNull Class<?> transformClass, int newPosition);
}
