package ru.tensor.sbis.design.text_span.span;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.text.style.TextAppearanceSpan;

import ru.tensor.sbis.design.text_span.span.CustomTypefaceSpan;

/**
 * Span для установки кастомного стиля из ресурсов
 *
 * @author am.boldinov
 */
public class TypefaceTextAppearanceSpan extends TextAppearanceSpan {

    @Nullable
    private CustomTypefaceSpan mTypefaceSpan;

    /**
     * Использует стиль для определения внешнего вида текста
     * @param context контекст приложения
     * @param appearance ссылка на ресурс со стилем
     * @param typeface шрифт, который необходимо применить к тексту
     */
    public TypefaceTextAppearanceSpan(@NonNull Context context, int appearance, @Nullable Typeface typeface) {
        super(context, appearance);
        if (typeface != null) {
            mTypefaceSpan = new CustomTypefaceSpan(typeface);
        }
    }

    /**
     * Обновляет форматирование текста путем изменения цвета символов
     * Делегирует вызов updateDrawState в {@link CustomTypefaceSpan}
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        if (mTypefaceSpan != null) {
            mTypefaceSpan.updateDrawState(ds);
        }
    }

    /**
     * Обновляет форматирование текста путем изменения размера символов
     * Делегирует вызов updateMeasureState в {@link CustomTypefaceSpan}
     */
    @Override
    public void updateMeasureState(TextPaint ds) {
        super.updateMeasureState(ds);
        if (mTypefaceSpan != null) {
            mTypefaceSpan.updateMeasureState(ds);
        }
    }
}

