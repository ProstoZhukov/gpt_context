package ru.tensor.sbis.richtext.view;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.widget.AppCompatTextView;

import android.graphics.Canvas;
import android.os.Build;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UpdateAppearance;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.facebook.common.lifecycle.AttachDetachListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.tensor.sbis.richtext.span.background.LayoutBackgroundSpan;
import ru.tensor.sbis.richtext.util.RichWidgetUtil;
import ru.tensor.sbis.richtext.util.SingleDelayHandler;
import ru.tensor.sbis.richtext.view.gesture.DefaultGestureSpanFinder;
import ru.tensor.sbis.richtext.view.gesture.SpanMovementDelegate;
import timber.log.Timber;

/**
 * Компонент для рендера богатого текста.
 * Обрабатывает нажатия на текст и перерисовку спанов (при установке кастомного TouchListener в обработчике
 * необходимо сначала вызвать метод onTouchEvent непосредственно у самого компонента,
 * в противном случае предустановленная обработка нажатий работать не будет).
 *
 * Для работы с компонентом достаточно использовать стандартные методы {@link android.widget.TextView}
 *
 * @author am.boldinov
 */
public class RichTextView extends AppCompatTextView {

    private static final int INVALIDATE_SPANS_DELAY = 50;

    private boolean mIsAttached;
    @Nullable
    private AttachDetachListener[] mPreviousAttachSpans;
    @Nullable
    private List<SpanMovementDelegate> mMovementDelegates;
    @NonNull
    private final Set<UpdateAppearance> mPendingInvalidateSpans = new HashSet<>();
    private boolean mLongClickHandled;
    @NonNull
    private final SingleDelayHandler mDelayHandler = new SingleDelayHandler();
    @NonNull
    private final GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
            if (isLongClickable() || getLinksClickable()) {
                mLongClickHandled = RichWidgetUtil.processLongClick(
                        RichTextView.this,
                        e,
                        DefaultGestureSpanFinder.INSTANCE,
                        mMovementDelegates
                );
            }
        }
    }, mDelayHandler.getMainHandler());
    @NonNull
    private final Runnable mInvalidateSpanRunnable = () -> {
        if (canInvalidateSpans() && !mPendingInvalidateSpans.isEmpty()) {
            if (getText() instanceof Spanned) {
                final Spanned text = (Spanned) getText();
                int start = text.length();
                int end = 0;
                UpdateAppearance candidate = null;
                for (UpdateAppearance span : mPendingInvalidateSpans) {
                    final int spanStart = text.getSpanStart(span);
                    if (spanStart != -1) {
                        start = Math.min(start, spanStart);
                        end = Math.max(end, text.getSpanEnd(span));
                        if (candidate == null) {
                            candidate = span;
                        }
                    }
                }
                if (start < end && candidate != null) {
                    dispatchSpanChanged(text, candidate, start, end);
                }
            } else {
                Timber.e("Invalidate span method support only Spannable");
            }
        }
        mPendingInvalidateSpans.clear();
    };

    public RichTextView(Context context) {
        super(context);
        init();
    }

    public RichTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setFallbackLineSpacing(false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean handled = mGestureDetector.onTouchEvent(event) || mLongClickHandled;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mLongClickHandled = false;
        }
        return handled || (getLinksClickable() && RichWidgetUtil.processLinkMovement(
                this, event, DefaultGestureSpanFinder.INSTANCE, mMovementDelegates))
                || super.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttached = true;
        attachViewToSpan(getText());
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mIsAttached = true;
        attachViewToSpan(getText());
    }

    @Override
    protected void onDetachedFromWindow() {
        mIsAttached = false;
        detachViewFromSpan();
        super.onDetachedFromWindow();
    }

    @Override
    public void onStartTemporaryDetach() {
        mIsAttached = false;
        detachViewFromSpan();
        super.onStartTemporaryDetach();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (type == BufferType.NORMAL && getEllipsize() == null) {
            type = BufferType.SPANNABLE;
        }
        detachPreviousSpans();
        if (mIsAttached) {
            attachViewToSpan(text);
        }
        super.setText(text, type);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            Timber.d(e);
            drawBrokenLayout(canvas);
        }
    }

    @Override
    public boolean performLongClick() {
        try {
            return super.performLongClick();
        } catch (Exception e) { // может быть внутренняя ошибка при выделении
            Timber.e(e);
            return false;
        }
    }

    /**
     * Добавляет подписку на отслеживание пользовательских действий по тексту.
     */
    public void addMovementDelegate(@NonNull SpanMovementDelegate delegate) {
        requireMovementDelegates().add(delegate);
    }

    /**
     * Удаляет подписку на отслеживание пользовательских действий по тексту.
     */
    public void removeMovementDelegate(@NonNull SpanMovementDelegate delegate) {
        requireMovementDelegates().remove(delegate);
    }

    /**
     * Перерисовывает span, повтороно вызывая его методы.
     * Выполняется с задержкой для предотвращения троттлинга, объединяет несколько операций перерисовки в одну
     */
    public void postInvalidateSpan(@NonNull UpdateAppearance span) {
        if (canInvalidateSpans()) {
            mPendingInvalidateSpans.add(span);
            mDelayHandler.post(mInvalidateSpanRunnable, INVALIDATE_SPANS_DELAY);
        }
    }

    /**
     * Перерисовывает Span, повторно вызывая все его методы.
     * Можно так же использовать для анимации ReplacementSpan
     */
    public void invalidateSpan(@NonNull UpdateAppearance span) {
        if (canInvalidateSpans()) {
            mPendingInvalidateSpans.add(span);
            mInvalidateSpanRunnable.run();
        }
    }

    /**
     * Устанавливает текст без отсоединения текущих спанов от View
     */
    @SuppressWarnings("SameParameterValue")
    void setTextWithoutDetachSpans(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    /**
     * Устанавливает размеры для View.
     * Необходимо использовать только в случае если текст ранее измерялся {@link RichTextView#measure(int, int)}
     * и {@link android.text.Layout} готов к отрисовке.
     */
    void setMeasuredDimensionProxy(int measuredWidth, int measuredHeight) {
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void drawBrokenLayout(@NonNull Canvas canvas) {
        try {
            getLayout().draw(canvas);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void drawBackground(@NonNull Canvas canvas) {
        if (getText() instanceof Spanned) {
            final Spanned text = (Spanned) getText();
            final LayoutBackgroundSpan[] spans = text.getSpans(0, text.length(), LayoutBackgroundSpan.class);
            for (LayoutBackgroundSpan span : spans) {
                final int start = text.getSpanStart(span);
                final int end = text.getSpanEnd(span);
                span.draw(canvas, getLayout(), start, end);
            }
        }
    }

    private boolean canInvalidateSpans() {
        return mIsAttached && getText().length() > 0;
    }

    private void attachViewToSpan(CharSequence input) {
        if (input instanceof Spanned) {
            final Spanned text = (Spanned) input;
            final AttachDetachListener[] spans = text.getSpans(0, text.length(), AttachDetachListener.class);
            for (AttachDetachListener span : spans) {
                span.onAttachToView(this);
            }
            mPreviousAttachSpans = spans;
        }
    }

    private void detachViewFromSpan() {
        if (getText() instanceof Spanned) {
            final Spanned text = (Spanned) getText();
            final AttachDetachListener[] spans = text.getSpans(0, text.length(), AttachDetachListener.class);
            for (AttachDetachListener span : spans) {
                span.onDetachFromView(this);
            }
        }
        detachPreviousSpans();
    }

    private void detachPreviousSpans() {
        //noinspection ConstantConditions
        if (mDelayHandler != null) {
            mDelayHandler.removeCallbacks(mInvalidateSpanRunnable);
            mPendingInvalidateSpans.clear();
        }
        if (mPreviousAttachSpans != null) {
            for (AttachDetachListener span : mPreviousAttachSpans) {
                span.onDetachFromView(this);
            }
            mPreviousAttachSpans = null;
        }
    }

    private void dispatchSpanChanged(@NonNull Spanned text, @NonNull UpdateAppearance span, int start, int end) {
        final SpanWatcher[] spans = text.getSpans(0, text.length(), SpanWatcher.class);
        boolean changed = false;
        if (spans.length > 0) {
            for (SpanWatcher watcher : spans) {
                if (watcher.getClass().getDeclaringClass() == TextView.class) {
                    final Spannable target;
                    if (text instanceof Spannable) {
                        target = (Spannable) text;
                    } else {
                        // по идее никогда не вызовется, т.к SpanWatcher навешиваются только на Spannable
                        target = new SpannableString(text);
                    }
                    watcher.onSpanChanged(target, span, start, end, start, end);
                    changed = true;
                    break;
                }
            }
        }
        if (!changed) {
            setTextWithoutDetachSpans(getText(), BufferType.SPANNABLE);
        }
    }

    @NonNull
    private List<SpanMovementDelegate> requireMovementDelegates() {
        if (mMovementDelegates == null) {
            mMovementDelegates = new ArrayList<>();
        }
        return mMovementDelegates;
    }
}
