package ru.tensor.sbis.design.utils;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Functions for animating views.
 */
@SuppressWarnings("unused")
public class AnimationUtil {

    public static final long ANIMATION_DURATION = 150;

    private AnimationUtil() {
        // no instance
    }

    /**
     * Changes tint color for drawable
     *
     * @param drawable  Drawable resource
     * @param color     A color to be applied
     * @param forceTint true if apply tint for given drawable, false if generate the new one
     * @return Drawable with applied tint color
     */
    public static Drawable getTintDrawable(Drawable drawable, @ColorInt int color, boolean forceTint) {
        if (forceTint) {
            drawable.clearColorFilter();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawable.invalidateSelf();
            return drawable;
        }
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
    }

    /**
     * Update text size with animation
     */
    public static void updateTextSize(final TextView textView, float fromSize, float toSize) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromSize, toSize);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            float animatedValue = (float) valueAnimator.getAnimatedValue();
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, animatedValue);
        });
        animator.start();
    }

    /**
     * Update alpha with animation
     */
    public static void updateAlpha(final View view, float fromValue, float toValue, @Nullable ValueAnimator.AnimatorListener animatorListener) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromValue, toValue);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(valueAnimator -> {
            float animatedValue = (float) valueAnimator.getAnimatedValue();
            view.setAlpha(animatedValue);
        });
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    /**
     * Update text color with animation
     */
    public static void updateTextColor(final TextView textView, @ColorInt int fromColor, @ColorInt int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.addUpdateListener(animator -> textView.setTextColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    /**
     * Update background color with animation
     */
    public static void updateViewBackgroundColor(final View view, @ColorInt int fromColor, @ColorInt int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.addUpdateListener(animator -> view.setBackgroundColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    /**
     * Update image view color with animation
     */
    public static void updateDrawableColor(final Context context, final Drawable drawable, final ImageView imageView,
            @ColorInt int fromColor, @ColorInt int toColor, final boolean forceTint) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.addUpdateListener(animator -> {
            imageView.setImageDrawable(getTintDrawable(drawable, (Integer) animator.getAnimatedValue(), forceTint));
            imageView.requestLayout();
        });
        colorAnimation.start();
    }

    /**
     * Update view width with animation
     */
    public static void updateWidth(final View view, float fromWidth, float toWidth, @Nullable ValueAnimator.AnimatorListener animatorListener) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromWidth, toWidth);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(animator1 -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = Math.round((float) animator1.getAnimatedValue());
            view.setLayoutParams(params);
        });
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    /**
     * Update view height with animation
     */
    public static void updateHeight(final View view, float fromHeight, float toHeight, @Nullable ValueAnimator.AnimatorListener animatorListener) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromHeight, toHeight);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(animator1 -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = Math.round((float) animator1.getAnimatedValue());
            view.setLayoutParams(params);
        });
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    /**
     * Update view translationX with animation
     */
    public static void updateTranslationX(final View view, float from, float to, @Nullable ValueAnimator.AnimatorListener animatorListener) {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(animator1 -> view.setTranslationX((float) animator1.getAnimatedValue()));
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    /**
     * Update view translationY with animation
     */
    public static void updateTranslationY(final View view, float from, float to, @Nullable ValueAnimator.AnimatorListener animatorListener) {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(animator1 -> view.setTranslationY((float) animator1.getAnimatedValue()));
        if (animatorListener != null) {
            animator.addListener(animatorListener);
        }
        animator.start();
    }

    /**
     * Animate view reveal
     */
    public static void revealView(final View view) {
        view.setScaleX(0);
        view.setScaleY(0);
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    /**
     * Animate view conceal
     */
    public static void concealView(final View view) {
        view.animate()
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(ANIMATION_DURATION)
                .start();
    }

    /**
     * Простой слушатель событий анимации, позволяющий переопределять только нужные колбэки.
     */
    public static class SimpleAnimatorListener implements ValueAnimator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

}
