package ru.tensor.sbis.mvp.fragment;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetBehavior;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialog;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialogFragment;
import ru.tensor.sbis.mvp.presenter.BasePresenter;
import ru.tensor.sbis.mvp.presenter.PresenterLoader;

/**
 * Created by pv.suvit on 09.11.2017.
 * Improved with SensorBottomSheetCallback by ko.abramov 02.07.2018
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings({"deprecation", "SpellCheckingInspection"})
public abstract class BottomSheetDialogPresenterFragment<V, P extends BasePresenter<V>>
        extends CustomBottomSheetDialogFragment
        implements LoaderManager.LoaderCallbacks<P> {

    private final CustomBottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new SensorBottomSheetCallback();

    protected P mPresenter;

    @Nullable
    protected FrameLayout mBottomSheet;
    @Nullable
    private CustomBottomSheetBehavior<FrameLayout> mBehavior;

    protected abstract int getPresenterLoaderId();

    @NonNull
    protected abstract V getPresenterView();

    @NonNull
    protected abstract P createPresenter();

    protected void initViews(@NonNull View mainView, @Nullable Bundle savedInstanceState) {
        //implement if needed
    }

    /**
     * Инжектит зависимости в фрагмент.
     */
    protected abstract void inject();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        initPresenter();
    }

    @SuppressWarnings("rawtypes")
    private void initPresenter() {
        Loader loader = getLoaderManager().getLoader(getPresenterLoaderId());
        if (loader != null) {
            //noinspection unchecked
            mPresenter = ((PresenterLoader<P>) loader).getPresenter();
        } else {
            mPresenter = createPresenter();
            getLoaderManager().initLoader(getPresenterLoaderId(), null, this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setOnShowListener(dialog -> {
            CustomBottomSheetDialog bottomSheetDialog = (CustomBottomSheetDialog) dialog;
            mBottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
            if (mBottomSheet != null) {
                mBehavior = CustomBottomSheetBehavior.from(mBottomSheet);
                mBehavior.setBottomSheetCallback(mBottomSheetCallback);
                setExpandedState();
                mBehavior.setSkipCollapsed(true);
            }
        });
        mPresenter.attachView(getPresenterView());
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        if (mBehavior != null) {
            mBehavior.setBottomSheetCallback(null);
            mBehavior = null;
        }
        mBottomSheet = null;
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Loader<P> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(getContext(), mPresenter);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<P> loader, P data) {
        //ignored
    }

    @Override
    public void onLoaderReset(@NonNull Loader<P> loader) {
        mPresenter = null;
    }

    protected void setExpandedState() {
        if (mBehavior != null) {
            mBehavior.setState(CustomBottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * Показать фрагмент
     **/
    public void showAllowingStateLoss(@NonNull FragmentManager manager, @Nullable String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * Класс для контроля поведения диалогового окна
     */
    class SensorBottomSheetCallback extends CustomBottomSheetBehavior.BottomSheetCallback {

        private static final float CONTROL_VELOCITY_THRESHOLD = 10.0F;
        private static final int DEFAULT_VELOCITY_COMPUTE = 1000;

        private int bottomSheetHeight;

        @CustomBottomSheetBehavior.State
        private int mState;

        private long mLastDownTime;
        private VelocityTracker mTracker = null;

        @Override
        public void onStateChanged(@NonNull View bottomSheet,
                                   @CustomBottomSheetBehavior.State int newState) {
            int bottomSheetNewHeight = bottomSheet.getMeasuredHeight();
            if (bottomSheetHeight != bottomSheetNewHeight) {
                bottomSheetHeight = bottomSheetNewHeight;
                if (mBehavior != null) {
                    mBehavior.setPeekHeight(bottomSheetHeight / 2);
                }
            }

            if (newState == CustomBottomSheetBehavior.STATE_HIDDEN) {
                if (mState == CustomBottomSheetBehavior.STATE_SETTLING) {
                    dismissAfterSlidingDown();
                    if (mBehavior != null) {
                        mBehavior.setState(CustomBottomSheetBehavior.STATE_COLLAPSED);
                    }
                } else if (mState == CustomBottomSheetBehavior.STATE_EXPANDED) {
                    setExpandedState();
                }
            }

            if (mState != CustomBottomSheetBehavior.STATE_DRAGGING && newState == CustomBottomSheetBehavior.STATE_DRAGGING) {
                if (mTracker == null) {
                    mTracker = VelocityTracker.obtain();
                } else {
                    mTracker.clear();
                }
                mLastDownTime = System.currentTimeMillis();
                mTracker.addMovement(
                        MotionEvent.obtain(
                                mLastDownTime,
                                mLastDownTime,
                                MotionEvent.ACTION_DOWN,
                                0,
                                0,
                                0
                        )
                );
            }

            if (mState == CustomBottomSheetBehavior.STATE_DRAGGING && newState == CustomBottomSheetBehavior.STATE_SETTLING) {
                // count slideOffset and open or dismiss bottomSheet
                float velocity = mTracker.getYVelocity();
                if (velocity >= CONTROL_VELOCITY_THRESHOLD) {
                    open();
                }
                if (mTracker != null) {
                    mTracker.recycle();
                    mTracker = null;
                }
            }
            mState = newState;
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (mState == CustomBottomSheetBehavior.STATE_DRAGGING) {
                mTracker.addMovement(
                        MotionEvent.obtain(
                                mLastDownTime,
                                System.currentTimeMillis(),
                                MotionEvent.ACTION_MOVE,
                                0,
                                slideOffset,
                                0
                        )
                );
                mTracker.computeCurrentVelocity(DEFAULT_VELOCITY_COMPUTE);
            }
        }

        private void open() {
            setExpandedState();
        }

    }

}
