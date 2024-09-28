package ru.tensor.sbis.common.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.lang.ref.WeakReference;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import timber.log.Timber;

import timber.log.Timber;

/**
 * Created by ss.buvaylink on 02.11.2016.
 */

public class AdjustResizeHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final double MIN_OPEN_KEYBOARD_HEIGHT_RATIO = 0.25;
    private static final double DEFAULT_KEYBOARD_HEIGHT_RATIO = 0.4;

    private static final long MIN_INTERVAL_BETWEEN_OPEN_AND_CLOSE_EVENTS = 700;
    private static final long NOTIFY_CLOSED_DELAY = 500;
    private static final long REAL_HEIGHT_RECEIVE_TIMEOUT = 2000;

    private static boolean isKeyboardShown = false;
    private static boolean isLastShownInLandscape = false;

    @NonNull
    private final AdjustResizeHelperHost mAdjustResizeHelperHost;
    private final Rect mVisibleDisplayFrame = new Rect();
    private int mLastKeyboardHeight;
    private final boolean mIsLandscape;
    private final boolean mIsTablet;
    @Px
    private int mDefaultRootContentPaddingTop;
    protected int mMainHeight;

    private long mLastOpenTime = 0;
    private long mExpectedPendingCloseEventTime = 0;
    private final Handler mHandler = new Handler();
    private final Runnable onKeyboardClosed = () -> onKeyboardCloseMeasure(getKeyboardHeight());
    private final Runnable notifyClosedAfterTimeout = () -> onKeyboardCloseMeasure(getKeyboardHeight());

    @Nullable
    private WeakReference<View> mLastFocus;


    public AdjustResizeHelper(@NonNull AdjustResizeHelperHost adjustResizeHelperHost) {
        mAdjustResizeHelperHost = adjustResizeHelperHost;
        Context context = adjustResizeHelperHost.getActivity();
        mIsLandscape = DeviceConfigurationUtils.isLandscape(context);
        mIsTablet = DeviceConfigurationUtils.isTablet(context);
    }

    @CallSuper
    @Override
    public void onGlobalLayout() {
        int assumedKeyboardHeight = calculateKeyboardHeight();
        boolean substituteHeight = false;
        if (shouldSubstituteHeight(assumedKeyboardHeight)) {
            /*
            Сменили ориентацию экрана, когда клавиатура была показана. Ещё не получено реальное
            значение высоты — используем предполагаемое
             */
            assumedKeyboardHeight = mIsLandscape
                    ? AppConfig.getLandscapeKeyboardHeight()
                    : AppConfig.getPortraitKeyboardHeight();
            if (assumedKeyboardHeight > 0) {
                substituteHeight = true;
                if (mLastKeyboardHeight == 0) {
                    /*
                    Если будет получено реальное значение клавиатуры по истечении таймаута, то она,
                    вероятно, и не откроется, так что нужно убрать отступ у контента
                     */
                    mHandler.postDelayed(notifyClosedAfterTimeout, REAL_HEIGHT_RECEIVE_TIMEOUT);
                }
            }
        }
        assumedKeyboardHeight = ensureAssumedHeightIsNotTooBig(assumedKeyboardHeight);
        if (assumedKeyboardHeight == mLastKeyboardHeight) {
            return;
        }

        if (mLastOpenTime == 0 && mDefaultRootContentPaddingTop == 0) {
            View content = getRootContentView();
            if (content != null) {
                mDefaultRootContentPaddingTop = content.getPaddingTop();
            }
        }

        mHandler.removeCallbacks(onKeyboardClosed);

        View currentFocus = getRootView().findFocus();
        if (currentFocus != null) {
            mLastFocus = new WeakReference<>(currentFocus);
        }

        if (assumedKeyboardHeight / (float) mMainHeight > MIN_OPEN_KEYBOARD_HEIGHT_RATIO) {
            mLastOpenTime = substituteHeight ? 0 : System.currentTimeMillis();
            onKeyboardOpenMeasure(assumedKeyboardHeight);
            if (!substituteHeight) {
                mHandler.removeCallbacks(notifyClosedAfterTimeout);
                isLastShownInLandscape = mIsLandscape;
            }
        } else if (mLastKeyboardHeight != 0) {
            if (System.currentTimeMillis() - mLastOpenTime < MIN_INTERVAL_BETWEEN_OPEN_AND_CLOSE_EVENTS &&
                    mayPostponeCloseEvent(currentFocus)) {
                /*
                Если прошло слишком мало времени с события открытия, публикуем событие закрытия с задержкой, если к тому
                времени не произойдёт очередного события
                 */
                postponeCloseEvent();
            } else {
                onKeyboardCloseMeasure(getKeyboardHeight());
            }
        }
    }

    public boolean isKeyboardOpen() {
        return mLastKeyboardHeight > 0;
    }

    protected int calculateKeyboardHeight() {
        mMainHeight = getMainHeight();
        return getContentHeight() - mMainHeight;
    }

    private void onKeyboardOpenMeasure(int keyboardHeight) {
        isKeyboardShown = true;
        saveKeyboardHeight(keyboardHeight);
        mAdjustResizeHelperHost.onKeyboardOpenMeasure(keyboardHeight);
        // исправляет возможный подъём содержимого после смены ориентации на альбомную
        offsetContentDownIfNeeded();
        mLastKeyboardHeight = keyboardHeight;
    }

    private void onKeyboardCloseMeasure(int keyboardHeight) {
        isKeyboardShown = false;
        mAdjustResizeHelperHost.onKeyboardCloseMeasure(keyboardHeight);
        resetContentOffset();
        mLastKeyboardHeight = 0;
    }

    private void postponeCloseEvent() {
        long notifyDelay = NOTIFY_CLOSED_DELAY;
        long cancelledPendingEventRemainingDelay = mExpectedPendingCloseEventTime - System.currentTimeMillis();
        if (cancelledPendingEventRemainingDelay > 0) {
            notifyDelay = cancelledPendingEventRemainingDelay;
        }
        mExpectedPendingCloseEventTime = System.currentTimeMillis() + notifyDelay;
        mHandler.postDelayed(onKeyboardClosed, notifyDelay);
    }

    private boolean mayPostponeCloseEvent(@Nullable View currentFocus) {
        InputMethodManager imm = (InputMethodManager) mAdjustResizeHelperHost.getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return currentFocus != null || (imm.isAcceptingText() && (mLastFocus == null || mLastFocus.get() == null));
    }

    private int getContentHeight() {
        View originalContentView = mAdjustResizeHelperHost.getContentView();
        View contentView = originalContentView.getRootView().findViewById(android.R.id.content);
        if (contentView == null) {
            String hostFragmentInfo;
            try {
                Fragment hostFragment = FragmentManager.findFragment(originalContentView);
                hostFragmentInfo = String.format("Fragment: %s", hostFragment.getClass().getName());
            } catch (Exception e) {
                hostFragmentInfo = "Not fragment host";
            }
            Timber.w(
                    "Не удалось найти 'content' для хоста %s на activity %s (%s)",
                    mAdjustResizeHelperHost.getClass().getName(),
                    mAdjustResizeHelperHost.getActivity().getClass().getName(),
                    hostFragmentInfo
            );
            return 0;
        }

        Rect rect = new Rect();
        contentView.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        int[] location = new int[2];
        contentView.getLocationInWindow(location);
        boolean isContentHeightContainsStatusBarHeight = statusBarHeight > 0 && location[1] == 0;

        int contentHeight = contentView.getHeight();
        return isContentHeightContainsStatusBarHeight ? contentHeight - statusBarHeight : contentHeight;
    }

    protected int getMainHeight() {
        mAdjustResizeHelperHost.getContentView().getWindowVisibleDisplayFrame(mVisibleDisplayFrame);
        return mVisibleDisplayFrame.bottom - mVisibleDisplayFrame.top;
    }

    @NonNull
    private View getRootView() {
        return mAdjustResizeHelperHost.getContentView().getRootView();
    }

    private int getRootViewLocationY() {
        int[] loc = new int[2];
        getRootView().getLocationInWindow(loc);
        return loc[1];
    }

    private void saveKeyboardHeight(int keyboardHeight) {
        if (mIsLandscape) {
            AppConfig.setLandscapeKeyboardHeight(keyboardHeight);
        } else {
            AppConfig.setPortraitKeyboardHeight(keyboardHeight);
        }
    }

    private int getKeyboardHeight() {
        int keyboardHeight = mIsLandscape
                ? AppConfig.getLandscapeKeyboardHeight()
                : AppConfig.getPortraitKeyboardHeight();
        if (keyboardHeight == 0) {
            keyboardHeight = (int) (mAdjustResizeHelperHost.getContentView().getHeight() * DEFAULT_KEYBOARD_HEIGHT_RATIO);
        }
        return keyboardHeight;
    }

    private void offsetContentDownIfNeeded() {
        if (shouldUpdateContentOffset() && getRootViewLocationY() < 0) {
            View content = getRootContentView();
            if (content != null) updatePaddingTop(content, -getRootViewLocationY());
        }
    }

    private void resetContentOffset() {
        if (shouldUpdateContentOffset()) {
            View content = getRootContentView();
            if (content != null) updatePaddingTop(content, mDefaultRootContentPaddingTop);
        }
    }

    @Nullable
    private View getRootContentView() {
        return ((ViewGroup) getRootView()).getChildAt(0);
    }

    private boolean shouldUpdateContentOffset() {
        try {
            return mIsLandscape && getRootView() instanceof ViewGroup;
        } catch (IllegalStateException e) {
            Timber.w(e);
            return false;
        }
    }

    private void updatePaddingTop(@NonNull View view, @Px int paddingTop) {
        view.setPadding(view.getPaddingLeft(), paddingTop, view.getPaddingRight(), view.getPaddingBottom());
    }

    private boolean shouldSubstituteHeight(int assumedKeyboardHeight) {
        return !mIsTablet && assumedKeyboardHeight == 0 && mLastOpenTime == 0 &&
                isKeyboardShown && isLastShownInLandscape != mIsLandscape;
    }

    private int ensureAssumedHeightIsNotTooBig(int assumedKeyboardHeight) {
        if (mIsLandscape) {
            return assumedKeyboardHeight;
        }
        int savedHeight = AppConfig.getPortraitKeyboardHeight();
        if (savedHeight > 0 && assumedKeyboardHeight > savedHeight * 2) {
            return savedHeight;
        }
        return assumedKeyboardHeight;
    }

    public interface KeyboardEventListener {
        boolean onKeyboardOpenMeasure(int keyboardHeight);

        boolean onKeyboardCloseMeasure(int keyboardHeight);
    }

    public interface AdjustResizeHelperHost {
        void onKeyboardOpenMeasure(int keyboardHeight);

        void onKeyboardCloseMeasure(int keyboardHeight);

        @NonNull
        Activity getActivity();

        @NonNull
        View getContentView();
    }

    public interface KeyboardStateInterface {
        boolean isKeyboardOpen();
    }

}
