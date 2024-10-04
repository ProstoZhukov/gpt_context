package ru.tensor.sbis.design.swipeback;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * Расширение фрагмента для применения свойства закрытия по свайпу к root view
 */
@SuppressWarnings({"JavaDoc", "unused"})
public class SwipeBackFragment extends Fragment implements SwipeBackLayout.SwipeBackListener, SwipeBackFragmentOperations {

    private OnFragmentAddedListener mOnFragmentAddedListener;

    private SwipeBackHelper mSwipeBackHelper;
    private BitmapDrawable mBackgroundDrawable;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    /**
     ** Создание вью
     */
    public View inflate(@NonNull LayoutInflater inflater, int layoutResID, @Nullable ViewGroup container, boolean attachToRoot) {
        mSwipeBackHelper = new SwipeBackHelper(getContext(), this);
        if (swipeBackEnabled()) {
            View swipeRoot = mSwipeBackHelper.getContainer();
            View root;
            mSwipeBackHelper.getSwipeBackLayout().addView(
                    root = inflater.inflate(layoutResID, container, attachToRoot)
            );
            if (dataBindingEnabled() && DataBindingUtil.findBinding(root) != null) {
                DataBindingUtil.bind(swipeRoot);
            }
            initNestedSwipeBackSupport();
            return swipeRoot;
        } else {
            return inflater.inflate(layoutResID, container, attachToRoot);
        }
    }

    /**
     * Добавить swipeBackLayout в качестве родителя
     */
    @NonNull
    public View addToSwipeBackLayout(@NonNull View rootView) {
        if (swipeBackEnabled()) {
            mSwipeBackHelper = new SwipeBackHelper(getContext(), this);
            View swipeRoot = mSwipeBackHelper.getContainer();
            mSwipeBackHelper.getSwipeBackLayout().addView(rootView);
            initNestedSwipeBackSupport();
            return swipeRoot;
        } else {
            return rootView;
        }
    }

    /** @SelfDocumented */
    public void setEnableSwipe(boolean enableSwipe) {
        mSwipeBackHelper.getSwipeBackLayout().setEnablePullToBack(enableSwipe);
    }

    /** @SelfDocumented */
    public void setDragEdge(@NonNull SwipeBackLayout.DragEdge dragEdge) {
        mSwipeBackHelper.getSwipeBackLayout().setDragEdge(dragEdge);
    }

    /** @SelfDocumented */
    public void setDragDirectMode(@NonNull SwipeBackLayout.DragDirectMode dragDirectMode) {
        mSwipeBackHelper.getSwipeBackLayout().setDragDirectMode(dragDirectMode);
    }

    /**
     * @see SwipeBackLayout#setSwipeOnlyOnePointerEnabled
     */
    public void setSwipeOnlyOnePointerEnabled(boolean isSwipeOnlyOnePointerEnabled) {
        mSwipeBackHelper.getSwipeBackLayout().setSwipeOnlyOnePointerEnabled(isSwipeOnlyOnePointerEnabled);
    }

    /** @SelfDocumented */
    @Nullable
    public SwipeBackLayout getSwipeBackLayout() {
        if (mSwipeBackHelper != null) {
            return mSwipeBackHelper.getSwipeBackLayout();
        }
        return null;
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        mSwipeBackHelper.animateSwipe(fractionScreen);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewGoneBySwipe() {
        Activity act = (Activity) getContext();
        act.onBackPressed();
    }

    /** @SelfDocumented */
    protected boolean swipeBackEnabled() {
        return false;
    }

    /**
     * @see SwipeBackLayout#setSupportNestedSwipeBack(boolean)
     */
    protected boolean nestedSwipeBackSupported() {
        return false;
    }

    /** @SelfDocumented */
    @SuppressWarnings("SameReturnValue")
    protected boolean dataBindingEnabled() {
        return true;
    }

    @SuppressWarnings({"deprecation"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        globalLayoutListener = new GlobalLayoutListener(mOnFragmentAddedListener, getView());

        View view = getView();
        if (view != null) {
            initFragmentBackground(view);
            view.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        }
    }

    @Override
    public void onPause() {
        View view = getView();
        if (view != null) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        }
        super.onPause();
    }

    private void initFragmentBackground(@NonNull View view) {
        if (view instanceof SwipeBackLayout) {
            View childView = ((SwipeBackLayout) view).getChildAt(0);
            setBackground(childView);
        } else {
            setBackground(view);
        }
    }

    @Override
    public void setBackground(@Nullable View view) {
        if (view != null && view.getBackground() == null) {
            int defaultBg = 0;

            if (mBackgroundDrawable != null) {
                view.setBackground(mBackgroundDrawable);
            } else {
                view.setBackgroundResource(defaultBg);
            }
        }
    }

    @Override
    public void updateViewBackground(@Nullable BitmapDrawable background) {
        mBackgroundDrawable = background;
        View view = getView();
        if (view != null) {
            initFragmentBackground(view);
        }
    }

    @Override
    public void setOnFragmentAddedListener(@Nullable OnFragmentAddedListener listener) {
        mOnFragmentAddedListener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.setOnSwipeBackListener(null);
            mSwipeBackHelper = null;
        }
    }

    private void initNestedSwipeBackSupport() {
        mSwipeBackHelper.getSwipeBackLayout().setSupportNestedSwipeBack(nestedSwipeBackSupported());
    }

    /** @SelfDocumented */
    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    static class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private WeakReference<OnFragmentAddedListener> onFragmentAddedListener;
        private WeakReference<View> view;

        GlobalLayoutListener(OnFragmentAddedListener onFragmentAddedListener, View view) {
            this.onFragmentAddedListener = new WeakReference<>(onFragmentAddedListener);
            this.view = new WeakReference<>(view);
        }

        @Override
        public void onGlobalLayout() {
            if (view.get() != null) {
                view.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (onFragmentAddedListener.get() != null) {
                    onFragmentAddedListener.get().onFragmentAdded(view.get());
                }
            }
        }
    }

}
