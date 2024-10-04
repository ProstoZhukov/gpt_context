package ru.tensor.sbis.design.swipeback;

import static java.lang.Math.min;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Вспомогательный класс для работы свайпа
 */
@SuppressWarnings("JavaDoc")
public class SwipeBackHelper {

    /**
     * Край, определяющий направление свайпа по умолчанию
     */
    public static final SwipeBackLayout.DragEdge DEFAULT_DRAG_EDGE = SwipeBackLayout.DragEdge.LEFT;

    private static final float SWIPE_ANIMATION_WIDTH_RANGE = 0.75f;

    /** @SelfDocumented */
    protected final Context mContext;
    /** @SelfDocumented */
    protected SwipeBackLayout mSwipeBackLayout;

    /** @SelfDocumented */
    protected ImageView mIvShadow;
    /** @SelfDocumented */
    protected SwipeBackLayout.SwipeBackListener mSwipeBackListener;

    private final Interpolator swipeAnimationInterpolator = new AccelerateInterpolator();

    public SwipeBackHelper(Context context, SwipeBackLayout.SwipeBackListener swipeBackListener) {
        mContext = context;
        mSwipeBackListener = swipeBackListener;
    }

    /** @SelfDocumented */
    public void setOnSwipeBackListener(@Nullable SwipeBackLayout.SwipeBackListener swipeBackListener) {
        mSwipeBackListener = swipeBackListener;
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setOnSwipeBackListener(swipeBackListener);
        }
    }

    /**
     * Возвращает вью со свойством свайпа
     */
    public View getContainer() {
        RelativeLayout container = new RelativeLayout(mContext);
        mSwipeBackLayout = new SwipeBackLayout(mContext);
        mSwipeBackLayout.setDragEdge(DEFAULT_DRAG_EDGE);
        mSwipeBackLayout.setOnSwipeBackListener(mSwipeBackListener);
        mIvShadow = new ImageView(mContext);
        mIvShadow.setBackgroundColor(mContext.getResources().getColor(R.color.swipe_back_dimm_background_color));
        mIvShadow.setAlpha(0f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        container.addView(mIvShadow, params);
        container.addView(mSwipeBackLayout);
        return container;
    }

    /** @SelfDocumented */
    @NonNull
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    /** @SelfDocumented */
    public ImageView getIvShadow() {
        return mIvShadow;
    }

    /**
     * Анимировать свайп.
     *
     * @param screenFraction доля смещения экрана относительно ширины.
     */
    public void animateSwipe(float screenFraction) {
        float fraction = min(screenFraction / SWIPE_ANIMATION_WIDTH_RANGE, 1f);
        float interpolation = swipeAnimationInterpolator.getInterpolation(fraction);
        float alpha = 1 - interpolation;
        mSwipeBackLayout.setAlpha(alpha);
        mIvShadow.setAlpha(alpha);
    }
}
