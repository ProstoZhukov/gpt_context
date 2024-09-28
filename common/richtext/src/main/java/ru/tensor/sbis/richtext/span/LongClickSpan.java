package ru.tensor.sbis.richtext.span;

import androidx.annotation.NonNull;

import android.view.View;

/**
 * Span для обработки долгого нажатия.
 * Необходимо использовать в связке с {@link ru.tensor.sbis.richtext.view.RichTextView} для автоматического вызова
 *
 * @author am.boldinov
 */
public interface LongClickSpan {

    /**
     * Вызывается при долгом нажатии на текущий Span
     */
    void onLongClick(@NonNull View widget);
}
