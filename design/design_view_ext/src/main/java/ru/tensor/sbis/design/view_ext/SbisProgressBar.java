package ru.tensor.sbis.design.view_ext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * ProgressBar, по-умолчанию окрашеный в оранжевый цвет, но можно задать цвет аттрибутом progressColor
 *
 * @author sa.nikitin
 *
 * @deprecated переименован: {@link LoadingIndicator}
 */
public class SbisProgressBar extends ProgressBar {

    private boolean mIsRunningDelayedShowing;
    @NonNull
    private final Runnable mDelayedShowing = new Runnable() {
        @Override
        public void run() {
            if (mIsRunningDelayedShowing) {
                SbisProgressBar.super.setVisibility(VISIBLE);
                mIsRunningDelayedShowing = false;
            }
        }
    };
    private boolean mIsDelayedShowing;
    private int mShowingDelay;
    private boolean mStrictlyUseProgressDrawable;

    public SbisProgressBar(Context context) {
        super(context);
        initialize(context, null);
    }

    public SbisProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public SbisProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        if (isInEditMode())
            return;
        int progressColor = ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.color_accent);
        int showingDelay = getResources().getInteger(ru.tensor.sbis.design.R.integer.progressShowingDefaultDelay);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SbisProgressBar, 0, R.style.SbisProgressBarGlobal);
            try {
                progressColor = a.getColor(R.styleable.SbisProgressBar_progressColor, progressColor);
                mIsDelayedShowing = a.getBoolean(R.styleable.SbisProgressBar_delayedShowing, false);
                mStrictlyUseProgressDrawable = a.getBoolean(R.styleable.SbisProgressBar_strictlyUseProgressDrawable, false);
                showingDelay = a.getInteger(R.styleable.SbisProgressBar_showingDelay, showingDelay);
            } finally {
                a.recycle();
            }
        }
        if (!mStrictlyUseProgressDrawable) {
            setIndeterminateColor(progressColor);
        }
        setShowingDelay(showingDelay);
    }

    /** @SelfDocumented */
    public void setDelayedShowing(boolean isDelayedShowing) {
        if (mIsDelayedShowing && !isDelayedShowing) {
            stopDelayedShowing(true);
        }
        mIsDelayedShowing = isDelayedShowing;
    }

    /** @SelfDocumented */
    public void setShowingDelay(int milliseconds) {
        mShowingDelay = milliseconds;
    }

    /** @SelfDocumented */
    public void setIndeterminateColor(int progressColor) {
        Drawable indeterminateDrawable = getIndeterminateDrawable();
        if (indeterminateDrawable != null) {
            indeterminateDrawable.mutate();
            indeterminateDrawable.setColorFilter(new LightingColorFilter(Color.BLACK, progressColor));
            setIndeterminateDrawable(indeterminateDrawable);
        }
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.mutate();
            progressDrawable.setColorFilter(new LightingColorFilter(Color.BLACK, progressColor));
            setProgressDrawable(progressDrawable);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == VISIBLE && mIsDelayedShowing) {
            if (!mIsRunningDelayedShowing) {
                mIsRunningDelayedShowing = true;
                postDelayed(mDelayedShowing, mShowingDelay);
            }
        } else {
            setVisibilityForce(visibility);
        }
    }

    /** @SelfDocumented */
    public void setVisibilityForce(int visibility) {
        stopDelayedShowing(false);
        super.setVisibility(visibility);
    }

    /**
     * Синхронно делает вью невидимой и устанавливает видимость с задержкой по умолчанию
     */
    public void postDefaultDelayedVisible() {
        if (getVisibility() == View.VISIBLE) {
            setVisibilityForce(INVISIBLE);
        }
        setDelayedShowing(true);
        setVisibility(VISIBLE);
    }

    private void stopDelayedShowing(boolean completeImmediately) {
        if (mIsRunningDelayedShowing) {
            mIsRunningDelayedShowing = false;
            removeCallbacks(mDelayedShowing);
            if (completeImmediately) {
                super.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopDelayedShowing(true);
    }
}
