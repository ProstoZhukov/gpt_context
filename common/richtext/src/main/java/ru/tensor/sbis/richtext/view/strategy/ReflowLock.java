package ru.tensor.sbis.richtext.view.strategy;

import android.text.DynamicLayout;
import android.text.Editable;
import android.text.TextWatcher;

import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

/**
 * Реализует поведение блокировки обновления макета и повторной генерации текста при его изменении.
 * Блокирует вызов reflow, см. {@link DynamicLayout}.
 * Необходимо использовать при массовом изменении текста для избежания лишних обновлений.
 * <p>
 * Пример рекомендуемого использования:
 *
 * <pre> {@code
 *  class X {
 *      private final Editable text = new SpannableStringBuilder();
 *      private final ReflowLock lock = new ReflowLock(text);
 *
 *      public void test() {
 *          lock.lock();
 *          try {
 *              // ... modify text body
 *          } finally {
 *              lock.unlock();
 *          }
 *      }
 *  }
 * }</pre>
 *
 * @author am.boldinov
 */
@UiThread
class ReflowLock {

    @NonNull
    private final Editable mText;
    @Nullable
    private LockWatcher mWatcher;

    ReflowLock(@NonNull Editable text) {
        mText = text;
    }

    /**
     * Блокирует обновление макета {@link DynamicLayout}.
     */
    void lock() {
        if (mWatcher != null) {
            throw new IllegalStateException("LockWatcher already locked");
        }
        final TextWatcher[] watchers = mText.getSpans(0, mText.length(), TextWatcher.class);
        for (TextWatcher w : watchers) {
            if (w.getClass().getDeclaringClass() == DynamicLayout.class) {
                mWatcher = new LockWatcher(w, mText.getSpanStart(w), mText.getSpanEnd(w), mText.getSpanFlags(w), mText.length());
                mText.removeSpan(w);
                break;
            }
        }
    }

    /**
     * Отключает блокировку обновления макета {@link DynamicLayout}.
     * В случае если за время блокировки текст изменился запускает reflow.
     */
    void unlock() {
        if (mWatcher != null) {
            mText.setSpan(mWatcher.origin, Math.min(mWatcher.start, mText.length()), Math.min(mWatcher.end, mText.length()), mWatcher.flags);
            if (mWatcher.length != mText.length()) {
                mWatcher.origin.onTextChanged(mText, 0, mWatcher.length, mText.length());
            }
            mWatcher = null;
        }
    }

    private static final class LockWatcher {

        @NonNull
        private final TextWatcher origin;
        private final int start;
        private final int end;
        private final int flags;
        private final int length;

        LockWatcher(@NonNull TextWatcher origin, int start, int end, int flags, int length) {
            this.origin = origin;
            this.start = start;
            this.end = end;
            this.flags = flags;
            this.length = length;
        }

    }
}
