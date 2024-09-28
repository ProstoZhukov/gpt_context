package ru.tensor.sbis.common.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.tensor.sbis.design.sbis_text_view.SbisTextView;

/**
 * @author am.boldinov
 */
public class ViewHolderUtil {

    /**
     * Безопасный способ установки текста в {@link TextView}.<br>
     * При пустом тексте {@link TextView} скрывается
     *
     * @return {@code true} если {@link TextView} {@code != null}  и {@code value} не пустой
     * */
    public static boolean setTextWithVisibility(@Nullable TextView view, @Nullable CharSequence value) {
        if (setText(view, value)){
            view.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
            return view.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    /**
     * Безопасный способ установки текста в {@link SbisTextView}.<br>
     * При пустом тексте {@link SbisTextView} скрывается
     *
     * @return {@code true} если {@link SbisTextView} {@code != null}  и {@code value} не пустой
     * */
    public static boolean setSbisTextWithVisibility(@Nullable SbisTextView view, @Nullable CharSequence value) {
        if (setSbisText(view, value)){
            view.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
            return view.getVisibility() == View.VISIBLE;
        }
        return false;
    }
    
    /**
     * Безопасный способ установки текста в {@link TextView}.
     *
     * @return {@code true} если {@link TextView} {@code != null}
     * */
    public static boolean setText(@Nullable TextView textView, @Nullable CharSequence text){
        if (textView != null){
            textView.setText(text);
        }
        return textView != null;
    }

    /**
     * Безопасный способ установки текста в {@link SbisTextView}.
     *
     * @return {@code true} если {@link SbisTextView} {@code != null}
     * */
    public static boolean setSbisText(@Nullable SbisTextView textView, @Nullable CharSequence text){
        if (textView != null){
            textView.setText(text);
        }
        return textView != null;
    }

    public static void assertNonNull(View... views) {
        for (View view : views) {
            if (view == null) {
                throw new IllegalStateException("View can not be null");
            }
        }
    }

}
