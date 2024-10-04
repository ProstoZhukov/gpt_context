package ru.tensor.sbis.design_dialogs.dialogs.helper;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Вспомогательной класс для управления видимостью вью.
 */
public class ViewVisibilityHelper {

    /**
     * Тег для идентификации хелпера.
     */
    @NonNull
    private final String mViewTag;

    /**
     * Вью, для которой будет изменяться видимость.
     */
    @NonNull
    private final View mTargetView;

    /**
     * Хендлер для выполнения отложенных операций.
     */
    @NonNull
    private final Handler mHandler = new Handler();

    /**
     * Текущая видимость вью.
     */
    private boolean mVisible;

    /**
     * Время выполнения отложенной операции.
     */
    private long mExecuteTime;

    /**
     * Значение видимости для отложенной операции.
     */
    private boolean mDeferredVisibility;

    /**
     * Runnable с отложенной операцией.
     */
    private Runnable mDeferredRunnable;

    @UiThread
    public ViewVisibilityHelper(@NonNull String viewTag, @NonNull View targetView, @Nullable Bundle savedInstanceState, boolean defaultState) {
        mViewTag = viewTag;
        mTargetView = targetView;
        if (savedInstanceState != null) {
            mExecuteTime = savedInstanceState.getLong(getExecuteTimeStateKey(), -1);
            mDeferredVisibility = savedInstanceState.getBoolean(getDeferredVisibility());
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis >= mExecuteTime) {
                // Пропустили отложенную операцию, обновляем visibility
                mVisible = mDeferredVisibility;
            } else {
                mVisible = savedInstanceState.getBoolean(getVisibilityStateKey(), defaultState);
                // Возобновляем отложенную операцию
                setVisibilityDelayed(mDeferredVisibility, mExecuteTime - currentTimeMillis);
            }
        } else {
            mVisible = defaultState;
        }
        applyVisibility(false);
    }

    /**
     * Сохранить состояние видимости вью.
     */
    public void saveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(getVisibilityStateKey(), mVisible);
        outState.putLong(getExecuteTimeStateKey(), mExecuteTime);
        outState.putBoolean(getDeferredVisibility(), mDeferredVisibility);
    }

    private String getBaseStateKey() {
        return ViewVisibilityHelper.class.getSimpleName().concat(mViewTag);
    }

    private String getVisibilityStateKey() {
        return getBaseStateKey().concat(".visibility");
    }

    private String getExecuteTimeStateKey() {
        return getBaseStateKey().concat(".execute_time");
    }

    private String getDeferredVisibility() {
        return getBaseStateKey().concat(".deferred_visibility");
    }

    /**
     * Применить состояние к вью.
     * @param animate - нужно ли анимировать изменение состояния
     */
    private void applyVisibility(boolean animate) {
        if (animate) {
            final ViewGroup parent = mTargetView.getParent() instanceof ViewGroup
                    ? (ViewGroup) mTargetView.getParent() : null;
            if (parent != null) TransitionManager.beginDelayedTransition(parent);
            mTargetView.setVisibility(mVisible ? View.VISIBLE : View.INVISIBLE);
            if (parent != null) TransitionManager.endTransitions(parent);
        } else {
            mTargetView.setVisibility(mVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * Изменить видимость вью.
     */
    @UiThread
    public void setVisibility(boolean visible) {
        clearDeferredRunnable();
        if (mVisible != visible) {
            mVisible = visible;
            applyVisibility(true);
        }
    }

    /**
     * Изменить видимость вью через указанный промежуток времени.
     * @param visible   - видимость
     * @param delay     - задержка перед применением
     */
    @SuppressWarnings("Convert2Lambda")
    public void setVisibilityDelayed(boolean visible, long delay) {
        clearDeferredRunnable();
        mExecuteTime = System.currentTimeMillis() + delay;
        mDeferredVisibility = visible;
        mDeferredRunnable = new Runnable() {
            @Override
            public void run() {
                setVisibility(visible);
            }
        };
        mHandler.postDelayed(mDeferredRunnable, delay);
    }

    private void clearDeferredRunnable() {
        if (mDeferredRunnable != null) {
            mHandler.removeCallbacks(mDeferredRunnable);
            mDeferredRunnable = null;
        }
    }

}
