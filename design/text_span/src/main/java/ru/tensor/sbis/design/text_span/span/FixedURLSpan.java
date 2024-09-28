package ru.tensor.sbis.design.text_span.span;

import android.content.ActivityNotFoundException;
import android.os.Parcel;
import android.text.style.URLSpan;
import android.view.View;

import androidx.annotation.Nullable;
import ru.tensor.sbis.design.utils.DebounceActionHandler;
import timber.log.Timber;

/**
 * Обрабатывает исключение {@link ActivityNotFoundException) для pre-lollipop версий
 *
 * По ошибке, добавлена обработка всех исключений с последующим их логированием.
 * https://online.sbis.ru/opendoc.html?guid=4ac399ca-b13b-4ccd-a0ec-b12c8675e06c
 *
 * @author am.boldinov
 */
public class FixedURLSpan extends URLSpan {

    @Nullable
    private DebounceActionHandler mActionHandler;

    public FixedURLSpan(String url) {
        super(url);
    }

    @SuppressWarnings("unused")
    public FixedURLSpan(Parcel src) {
        super(src);
    }

    @Override
    public void onClick(View widget) {
        if (getURL().isEmpty()) {
            return;
        }
        if (mActionHandler == null) {
            mActionHandler = new DebounceActionHandler();
        }
        if (mActionHandler.enqueue()) {
            try {
                super.onClick(widget);
            } catch (ActivityNotFoundException ignored) {
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
