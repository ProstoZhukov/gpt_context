package ru.tensor.sbis.richtext.converter.handler.view;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.util.SpannableStreamBuilder;

/**
 * Базовый обработчик тегов для встраивания кастомных View в текст, которые внутри себя
 * имеют вложенный богатый текст.
 *
 * @author am.boldinov
 */
public abstract class ContentViewTagHandler extends BaseViewTagHandler {

    public ContentViewTagHandler(@NonNull Context context) {
        super(context);
    }

    /**
     * Необходимо вызвать в случае когда начинается вложенный контент с богатым текстом.
     */
    protected final void startContent(@NonNull Editable stream) {
        final SpannableStreamBuilder builder = (SpannableStreamBuilder) stream;
        builder.enableDelegate();
    }

    /**
     * Необходимо вызвать в случае когда заканчивается вложенный контент с богатым текстом.
     * Возвращает стилизованный текст, который можно установить в поле вью модели и забиндить
     * в {@link ru.tensor.sbis.richtext.view.RichViewLayout}
     */
    @NonNull
    protected final Spannable stopContent(@NonNull Editable stream) {
        final SpannableStreamBuilder builder = (SpannableStreamBuilder) stream;
        final Spannable result = builder.build();
        builder.disableDelegate();
        return result;
    }
}
