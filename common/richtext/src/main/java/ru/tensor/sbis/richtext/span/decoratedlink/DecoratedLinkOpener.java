package ru.tensor.sbis.richtext.span.decoratedlink;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview;

/**
 * Интерфейс для открытия декорированных ссылок
 *
 * @author am.boldinov
 */
public interface DecoratedLinkOpener {

    /**
     * Открывает декорированную ссылку
     *
     * @param context     контекст
     * @param linkPreview модель ссылки
     * @param title       заголовок ссылки
     */
    void open(@NonNull Context context, @NonNull LinkPreview linkPreview, @Nullable String title);

}
