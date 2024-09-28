package ru.tensor.sbis.richtext.view.worker;

import android.view.View;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import ru.tensor.sbis.richtext.span.view.ViewStubSpan;
import ru.tensor.sbis.richtext.view.RichViewLayout;
import ru.tensor.sbis.richtext.view.strategy.LineCursor;
import ru.tensor.sbis.richtext.view.strategy.ViewLayout;
import ru.tensor.sbis.richtext.view.strategy.WrapLineStrategy;

/**
 * Реализация воркера для построчного обтекания View-компонентов по умолчанию.
 *
 * @author am.boldinov
 */
public final class TextLineWrapWorker implements WrapWorker {

    private int mFrameHeight;
    private boolean mIsFinished;

    @NonNull
    private final ViewStubSpan mStubSpan;
    @NonNull
    private final View mView;
    private final int mPosition;

    public TextLineWrapWorker(@NonNull ViewStubSpan span, @NonNull View view, int position) {
        mStubSpan = span;
        mView = view;
        mPosition = position;
    }

    @Override
    public void doWork(@NonNull ViewLayout layout, @NonNull LineCursor cursor) {
        final WrapLineStrategy strategy = mStubSpan.getWrapLineStrategy();
        if (strategy == null) {
            mIsFinished = true;
            return;
        }
        final int frameTop;
        if (cursor.get() == layout.getLineCount()) {
            layout.getText().append(StringUtils.LF);
            if (mFrameHeight > 0) {
                finishStretch(layout, cursor, strategy);
                return;
            }
        }
        if (strategy.wrap(layout, mView, mStubSpan.getOptions(), cursor, mPosition)) {
            if (mFrameHeight == 0) {
                final RichViewLayout.LayoutParams layoutParams = (RichViewLayout.LayoutParams) mView.getLayoutParams();
                strategy.layout(layoutParams, layout, cursor.get());
                frameTop = layoutParams.topOffset;
            } else {
                frameTop = layout.getLineTop(cursor.get());
            }
            mFrameHeight += layout.getLineBottom(cursor.get()) - frameTop;
        } else if (mFrameHeight > 0) {
            finishStretch(layout, cursor, strategy);
            return;
        }
        mIsFinished = mFrameHeight >= mView.getMeasuredHeight();
        if (mIsFinished) {
            strategy.onWrapCompleted(layout, cursor.get(), 0);
        }
    }

    @Override
    public boolean isFinished() {
        return mIsFinished;
    }

    private void finishStretch(@NonNull ViewLayout layout, @NonNull LineCursor cursor,
                               @NonNull WrapLineStrategy strategy) {
        final int stretchSpace = Math.max(mView.getMeasuredHeight() - mFrameHeight, 0);
        cursor.moveToPrev();
        strategy.onWrapCompleted(layout, cursor.get(), stretchSpace);
        cursor.moveToNext();
        mIsFinished = true;
    }
}
