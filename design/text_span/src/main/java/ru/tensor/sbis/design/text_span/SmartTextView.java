package ru.tensor.sbis.design.text_span;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Легаси код
 *
 * @author vi.demidov
 */
public class SmartTextView extends AppCompatTextView {

    public SmartTextView(Context context) {
        super(context);
    }

    public SmartTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Установка текста.
     */
    public void setText(@Nullable String text) {
        super.setText(text);
        setVisibility(!TextUtils.isEmpty(text) ? VISIBLE : GONE);
    }

    /**
     * Установка текста.
     */
    public void setText(@Nullable Spannable text) {
        super.setText(text);
        setVisibility(!TextUtils.isEmpty(text) ? VISIBLE : GONE);
    }

    /**
     * Очистка текста.
     */
    @SuppressWarnings("unused")
    public void clearText() {
        super.setText(null);
        setVisibility(GONE);
    }
}
