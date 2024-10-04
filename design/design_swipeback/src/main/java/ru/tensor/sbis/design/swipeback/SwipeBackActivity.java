package ru.tensor.sbis.design.swipeback;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Расширение активити для применения свойства закрытия по свайпу к root view
 */
@SuppressWarnings({"JavaDoc", "unused", "ConstantConditions"})
public class SwipeBackActivity
        extends AppCompatActivity
        implements SwipeBackLayout.SwipeBackListener {

    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    private SwipeBackHelper mSwipeBackHelper = new SwipeBackHelper(this, this);

    @Override
    public void setContentView(int layoutResID) {
        if (swipeBackEnabled()) {
            View swipeRoot = mSwipeBackHelper.getContainer();
            View root;
            mSwipeBackHelper.getSwipeBackLayout().addView(
                    root = getLayoutInflater().inflate(layoutResID, null, false)
            );
            super.setContentView(swipeRoot);
            if (dataBindingEnabled() && DataBindingUtil.findBinding(root) != null) {
                DataBindingUtil.bind(swipeRoot);
            }
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        if (swipeBackEnabled()) {
            View swipeRoot = mSwipeBackHelper.getContainer();
            mSwipeBackHelper.getSwipeBackLayout().addView(view);
            super.setContentView(swipeRoot);
            if (dataBindingEnabled() && DataBindingUtil.findBinding(view) != null) {
                DataBindingUtil.bind(swipeRoot);
            }
        } else {
            super.setContentView(view);
        }
    }

    /** @SelfDocumented */
    public void setEnableSwipe(boolean enableSwipe) {
        mSwipeBackHelper.getSwipeBackLayout().setEnablePullToBack(enableSwipe);
    }

    /** @SelfDocumented */
    public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
        mSwipeBackHelper.getSwipeBackLayout().setDragEdge(dragEdge);
    }

    /** @SelfDocumented */
    public void setDragEdge(boolean enabled) {
        mSwipeBackHelper.getSwipeBackLayout().setDragEdge(enabled ? SwipeBackHelper.DEFAULT_DRAG_EDGE : SwipeBackLayout.DragEdge.NONE);
    }

    /** @SelfDocumented */
    public void setDragDirectMode(SwipeBackLayout.DragDirectMode dragDirectMode) {
        mSwipeBackHelper.getSwipeBackLayout().setDragDirectMode(dragDirectMode);
    }

    /**
     * @see SwipeBackLayout#setSwipeOnlyOnePointerEnabled
     */
    public void setSwipeOnlyOnePointerEnabled(boolean isSwipeOnlyOnePointerEnabled) {
        mSwipeBackHelper.getSwipeBackLayout().setSwipeOnlyOnePointerEnabled(isSwipeOnlyOnePointerEnabled);
    }

    /** @SelfDocumented */
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackHelper.getSwipeBackLayout();
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        mSwipeBackHelper.animateSwipe(fractionScreen);
    }

    @Override
    public void onViewGoneBySwipe() {
        finish();
        //R.anim.instant_fade_out - моментальное изменение прозрачности до 0
        //Требуется, чтобы окно активности не моргнуло после свайпа
        overridePendingTransition(0, R.anim.swipeback_instant_fade_out);
    }

    protected boolean swipeBackEnabled() {
        return false;
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean dataBindingEnabled() {
        return true;
    }

    /**
     * Создает изображение вью как картинку
     **/
    @NonNull
    protected BitmapDrawable makeFragmentBackground(@NonNull View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap b = Bitmap.createBitmap(drawingCache, 0, 0, drawingCache.getWidth(), drawingCache.getHeight());
        return new BitmapDrawable(getResources(), b);
    }

    /**
     * Устанавливает фон фрагмента
     **/
    public void setFragmentBackgroundOnConfigurationChanged(@IdRes int fragmentContainerId, String mainFragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(fragmentContainerId);
        Fragment oldFragment = fragmentManager.findFragmentByTag(mainFragmentTag);
        if (oldFragment instanceof SwipeBackFragmentOperations && fragment instanceof SwipeBackFragmentOperations) {
            ((SwipeBackFragmentOperations) oldFragment).setOnFragmentAddedListener((fragmentView) -> {
                BitmapDrawable background = makeFragmentBackground((ViewGroup) fragmentView.getParent());

                fragmentManager.beginTransaction()
                        .replace(fragmentContainerId, fragment, fragment.getTag())
                        .commitNow();
                ((SwipeBackFragmentOperations) fragment).updateViewBackground(background);
            });

            fragmentManager.beginTransaction()
                    .replace(fragmentContainerId, oldFragment, oldFragment.getTag())
                    .commitNow();
        }
    }

    /**
     * Устанавливает фон из фрагмента
     **/
    public void setFragmentBackgroundFromOldFragment(@IdRes int fragmentContainerId, @NonNull Fragment targetFragment) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment oldFragment = manager.findFragmentById(fragmentContainerId);
        BitmapDrawable background = null;

        if (targetFragment instanceof SwipeBackFragmentOperations) {
            if (oldFragment != null) {
                View view = oldFragment.getView();
                background = makeFragmentBackground(view);
            }
            ((SwipeBackFragmentOperations) targetFragment).updateViewBackground(background);
        }
    }
}
