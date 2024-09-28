package ru.tensor.sbis.richtext.util;

import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.richtext.span.PrioritySpan;

import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import java.util.Arrays;

/**
 * Кастомная реализация билдера, расширяющая его функции
 *
 * @author am.boldinov
 */
public class SpannableStreamBuilder extends SpannableStringBuilder {

    public static class Factory {
        private static final Spannable.Factory sInstance = new Spannable.Factory() {
            @Override
            public Spannable newSpannable(CharSequence source) {
                return new SpannableStreamBuilder(source);
            }
        };

        @NonNull
        public static Spannable.Factory getInstance() {
            return sInstance;
        }
    }

    @Nullable
    private SpannableStreamBuilder mDelegate;
    @Nullable
    private BuildListener mBuildListener;

    /**
     * Слушатель событий вызова {@link #build()}
     */
    public interface BuildListener {

        /**
         * Функция обратного вызова для обработки текста перед тем как он будет собран и очищен
         */
        void onBuild(@NonNull SpannableStreamBuilder source);
    }

    public SpannableStreamBuilder() {
        super();
    }

    public SpannableStreamBuilder(@Nullable BuildListener listener) {
        this();
        mBuildListener = listener;
    }

    public SpannableStreamBuilder(@NonNull CharSequence text) {
        super(text);
    }

    /**
     * Возвращает изменяемый Spannable и очищает билдер для повторного использования.
     */
    @NonNull
    public Spannable build() {
        if (mDelegate != null) {
            return mDelegate.build();
        }
        if (mBuildListener != null) {
            mBuildListener.onBuild(this);
        }
        final Spannable result = Factory.getInstance().newSpannable(this);
        clear();
        clearSpans();
        return result;
    }

    /**
     * Поднимает вложенный потоковый обработчик, которому будут перенаправлены все события
     */
    public void enableDelegate() {
        if (mDelegate != null) {
            mDelegate.enableDelegate();
        } else {
            mDelegate = new SpannableStreamBuilder(mBuildListener);
        }
    }

    /**
     * Отключает вложенный потоковый обработчик, все события будут перенаправлены текущему обработчику
     */
    public void disableDelegate() {
        if (mDelegate != null) {
            if (mDelegate.mDelegate != null) {
                mDelegate.disableDelegate();
            } else {
                mDelegate = null;
            }
        }
    }

    @Override
    public char charAt(int where) {
        if (mDelegate != null) {
            return mDelegate.charAt(where);
        }
        return super.charAt(where);
    }

    @Override
    public int length() {
        if (mDelegate != null) {
            return mDelegate.length();
        }
        return super.length();
    }

    @Override
    public SpannableStringBuilder insert(int where, CharSequence tb, int start, int end) {
        if (mDelegate != null) {
            return mDelegate.insert(where, tb, start, end);
        }
        return super.insert(where, tb, start, end);
    }

    @Override
    public SpannableStringBuilder insert(int where, CharSequence tb) {
        if (mDelegate != null) {
            return mDelegate.insert(where, tb);
        }
        return super.insert(where, tb);
    }

    @Override
    public SpannableStringBuilder delete(int start, int end) {
        if (mDelegate != null) {
            return mDelegate.delete(start, end);
        }
        return super.delete(start, end);
    }

    @Override
    public void clear() {
        if (mDelegate != null) {
            mDelegate.clear();
        } else {
            super.clear();
        }
    }

    @Override
    public void clearSpans() {
        if (mDelegate != null) {
            mDelegate.clearSpans();
        } else {
            super.clearSpans();
        }
    }

    @NonNull
    @Override
    public SpannableStringBuilder append(CharSequence text) {
        if (mDelegate != null) {
            return mDelegate.append(text);
        }
        return super.append(text);
    }

    @Override
    public SpannableStringBuilder append(CharSequence text, Object what, int flags) {
        if (mDelegate != null) {
            return mDelegate.append(text, what, flags);
        }
        return super.append(text, what, flags);
    }

    @NonNull
    @Override
    public SpannableStringBuilder append(CharSequence text, int start, int end) {
        if (mDelegate != null) {
            return mDelegate.append(text, start, end);
        }
        return super.append(text, start, end);
    }

    @NonNull
    @Override
    public SpannableStringBuilder append(char text) {
        if (mDelegate != null) {
            return mDelegate.append(text);
        }
        return super.append(text);
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb) {
        if (mDelegate != null) {
            return mDelegate.replace(start, end, tb);
        }
        return super.replace(start, end, tb);
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        if (mDelegate != null) {
            return mDelegate.replace(start, end, tb, tbstart, tbend);
        }
        return super.replace(start, end, tb, tbstart, tbend);
    }

    @Override
    public void setSpan(Object what, int start, int end, int flags) {
        if (mDelegate != null) {
            mDelegate.setSpan(what, start, end, flags);
        } else {
            if (what instanceof PrioritySpan) {
                flags |= ((PrioritySpan) what).getPriority() << SPAN_PRIORITY_SHIFT;
            } else {
                final int priority = flags & SPAN_PRIORITY;
                // для всех стандартно-системных спанов устанавливаем пользовательский приоритет
                if (priority == PrioritySpan.MIN_PRIORITY) {
                    flags |= PrioritySpan.USER_PRIORITY << SPAN_PRIORITY_SHIFT;
                }
            }
            super.setSpan(what, start, end, flags);
        }
    }

    @Override
    public void removeSpan(Object what) {
        if (mDelegate != null) {
            mDelegate.removeSpan(what);
        } else {
            super.removeSpan(what);
        }
    }

    @Override
    public int getSpanStart(Object what) {
        if (mDelegate != null) {
            return mDelegate.getSpanStart(what);
        }
        return super.getSpanStart(what);
    }

    @Override
    public int getSpanEnd(Object what) {
        if (mDelegate != null) {
            return mDelegate.getSpanEnd(what);
        }
        return super.getSpanEnd(what);
    }

    @Override
    public int getSpanFlags(Object what) {
        if (mDelegate != null) {
            return mDelegate.getSpanFlags(what);
        }
        return super.getSpanFlags(what);
    }

    @Override
    public int nextSpanTransition(int start, int limit, Class kind) {
        if (mDelegate != null) {
            return mDelegate.nextSpanTransition(start, limit, kind);
        }
        return super.nextSpanTransition(start, limit, kind);
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        if (mDelegate != null) {
            return mDelegate.subSequence(start, end);
        }
        return super.subSequence(start, end);
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        if (mDelegate != null) {
            mDelegate.getChars(start, end, dest, destoff);
        } else {
            super.getChars(start, end, dest, destoff);
        }
    }

    @NonNull
    @Override
    public String toString() {
        if (mDelegate != null) {
            return mDelegate.toString();
        }
        return super.toString();
    }

    @Override
    public int getTextWatcherDepth() {
        if (mDelegate != null) {
            return mDelegate.getTextWatcherDepth();
        }
        return super.getTextWatcherDepth();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getTextRunCursor(int contextStart, int contextEnd, int dir, int offset, int cursorOpt, Paint p) {
        if (mDelegate != null) {
            return mDelegate.getTextRunCursor(contextStart, contextEnd, dir, offset, cursorOpt, p);
        }
        return super.getTextRunCursor(contextStart, contextEnd, dir, offset, cursorOpt, p);
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        if (mDelegate != null) {
            mDelegate.setFilters(filters);
        } else {
            super.setFilters(filters);
        }
    }

    @Override
    public InputFilter[] getFilters() {
        if (mDelegate != null) {
            return mDelegate.getFilters();
        }
        return super.getFilters();
    }

    @Override
    public boolean equals(Object o) {
        if (mDelegate != null) {
            return mDelegate.equals(o);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        if (mDelegate != null) {
            return mDelegate.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        if (mDelegate != null) {
            return mDelegate.getSpans(queryStart, queryEnd, kind);
        }
        final T[] spans = super.getSpans(queryStart, queryEnd, kind);
        // сортировка спанов должна быть в порядке добавления https://issuetracker.google.com/issues/37129364
        if (spans.length > 0 && (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1)) {
            //noinspection ComparatorCombinators
            Arrays.sort(spans, (o1, o2) -> getSpanStart(o1) - getSpanStart(o2));
        }
        return spans;
    }
}
