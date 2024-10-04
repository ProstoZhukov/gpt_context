package ru.tensor.sbis.design.utils.animator.listener;

import android.animation.Animator;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Реализация слушателя аниматора, выполняющего обновление значения layout_height на каждый такт анимации.
 *
 * @author am.boldinov
 */
public class HeightAnimatorListener implements Animator.AnimatorListener {

    private final View mView;
    private final int mValueAfter;

    public HeightAnimatorListener(@NonNull View view, int valueAfter) {
        mView = view;
        mValueAfter = valueAfter;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @CallSuper
    @Override
    public void onAnimationEnd(@org.jetbrains.annotations.Nullable Animator animation) {
        // Возвращаем значение высоты по окончании анимации, если высота имеет специфичное значение
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.height = mValueAfter;
        // Вызываем обновление макета
        mView.setLayoutParams(layoutParams);
    }

    @CallSuper
    @Override
    public void onAnimationCancel(@org.jetbrains.annotations.Nullable Animator animation) {
        // Возвращаем значение высоты по окончании анимации, если высота имеет специфичное значение
        ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();
        layoutParams.height = mValueAfter;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

}
