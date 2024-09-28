package ru.tensor.sbis.design.text_span.span;

import android.text.TextPaint;

/**
 * URLSpan {@link android.text.style.URLSpan} без подчеркивания, и без окрашеванья в текст url-ссылки TextPaint'а {@link TextPaint#linkColor}
 *
 * @author am.boldinov
 */
@SuppressWarnings("unused")
public class CustomURLSpan extends FixedURLSpan {

    public CustomURLSpan(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        //not implemented
    }
}

