package ru.tensor.sbis.design.view.input.searchinput.util;

import static androidx.core.content.res.ResourcesCompat.ID_NULL;
import static androidx.core.view.ViewCompat.getElevation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.lang.ref.WeakReference;

import kotlin.Pair;
import ru.tensor.sbis.design.toolbar.R;
import ru.tensor.sbis.design.toolbar.ToolbarTabLayout;
import ru.tensor.sbis.design.toolbar.behavior.BaseAppBarLayoutBehavior;
import ru.tensor.sbis.design.utils.HalfHeightViewOutlineProvider;
import ru.tensor.sbis.design.view.input.searchinput.SearchInput;

/**
 * Добавляет динамическую высоту(тень) для AppBar.
 * В случае если список находится на стартовой позиции высота равна 0.
 * При прокрутке добавляет высоту равную 4dp.
 * При возвращении в начало списка высота изменится на 0.
 * ВНИМАНИЕ!!!
 * При использовании этого класса может понадобиться костыль
 * ru.tensor.sbis.design.view.input.searchinput.util.expandSearchInput для
 * того, чтобы развернуть панель ввода программно. Не помогут ни вызовы AppBarLayout.setExpanded,
 * ни симуляция скролла с помощью onNestedPreScroll и др.
 *
 * @author ma.kolpakov
 */
@SuppressWarnings("unused")
public class AppBarLayoutWithDynamicElevationBehavior extends BaseAppBarLayoutBehavior {

    private float mLastElevationValue, mElevation, mElevationNone;
    private boolean mIgnoreSearchOffset, mShouldHideElevation;
    private boolean mIsFirstLaunch = true, mIsCloseState = true;
    private SearchInput mSearchInput;
    private WeakReference<ToolbarTabLayout> mToolbarTabLayout;
    @IdRes
    private final int mPinnedViewId;
    @Nullable
    private View mPinnedView;

    public AppBarLayoutWithDynamicElevationBehavior() {
        mPinnedViewId = ID_NULL;
    }

    public AppBarLayoutWithDynamicElevationBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppBarLayoutWithDynamicElevationBehavior);
            mPinnedViewId = a.getResourceId(R.styleable.AppBarLayoutWithDynamicElevationBehavior_pinned_view_id, ID_NULL);
            a.recycle();
        } else {
            mPinnedViewId = ID_NULL;
        }
        mElevationNone = 0;
        mElevation = context.getResources().getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.elevation_high);
        mLastElevationValue = mElevationNone;
    }

    @Override
    public Parcelable onSaveInstanceState(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child) {
        final SavedState state = new SavedState(super.onSaveInstanceState(parent, child));
        state.elevation = mLastElevationValue;
        state.isCloseState = getTopAndBottomOffset() < 0;
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull CoordinatorLayout parent,
                                       @NonNull AppBarLayout child,
                                       @NonNull Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState appBarState = (SavedState) state;
            super.onRestoreInstanceState(parent, child, appBarState.getSuperState());
            mLastElevationValue = appBarState.elevation;
            mIsCloseState = appBarState.isCloseState;
            setElevation(child);
        } else {
            super.onRestoreInstanceState(parent, child, state);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        mIgnoreSearchOffset = true;
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScrolled(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View target, int dyConsumed, boolean isContentUnderAppBarLayout) {
        updateElevation(child, isContentUnderAppBarLayout);
    }

    @Override
    public void onNestedScrolled(@NonNull AppBarLayout appBarLayout, @NonNull RecyclerView recyclerView, boolean isContentUnderAppBarLayout) {
        updateElevation(appBarLayout, isContentUnderAppBarLayout);
    }

    @Override
    public boolean setTopAndBottomOffset(int offset) {
        if (mSearchInput != null && !mIgnoreSearchOffset) {
            int hiddenSearchOffset = -mSearchInput.getMeasuredHeight();
            if (mSearchInput.getVisibility() != View.GONE && hiddenSearchOffset < 0) {
                SearchInputContext searchInputContext = mSearchInput.getSearchInputContext();
                SearchInputState state = SearchInputState.HIDDEN;
                if (searchInputContext != null) {
                    state = SearchStateRepository
                            .INSTANCE
                            .getSearchInputStateForScreen(searchInputContext.getViewLocationInApplication());
                }
                if (state == SearchInputState.HIDDEN
                        && (mSearchInput.getFilterString().isEmpty() || mSearchInput.isDefault())
                        && mIsCloseState
                ) {
                    offset = hiddenSearchOffset;
                } else {
                    offset = 0;
                    mIgnoreSearchOffset = true;
                }
            } else {
                //При mIgnoreSearchOffset == true мы могли тут оказаться при смене вкладки.
                // Аппбар мог быть не раскрыт полностью из за скрытой поисковой строки.
                // В этом случае аппбар нужно восстановить в исходное состояние
                offset = 0;
            }
        }
        updateSearchInputState(offset < 0 ? SearchInputState.HIDDEN : SearchInputState.SHOWN);
        return super.setTopAndBottomOffset(offset);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout abl, View target, int type) {
        AppBarLayout.LayoutParams lp = getChildCollapsingToolbarLayoutParams(abl);
        if (mSearchInput != null && mSearchInput.getVisibility() != View.GONE && !mIgnoreSearchOffset && lp != null) {
            int snapFlag = lp.getScrollFlags() & AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
            /*
            Сбрасываем флаг, чтобы предотвратить выполнение snap во время прокрутки из состояния со
            скрытой строкой поиска, иначе строка поиска принудительно отобразится
             */
            lp.setScrollFlags(lp.getScrollFlags() & ~AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            super.onStopNestedScroll(coordinatorLayout, abl, target, type);
            lp.setScrollFlags(lp.getScrollFlags() | snapFlag);
        } else {
            super.onStopNestedScroll(coordinatorLayout, abl, target, type);
        }
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, int layoutDirection) {
        if (mIsFirstLaunch) {
            mIsFirstLaunch = false;
            //Пытаемся найти необходимые нам вью - строку поиска и TabLayout.
            Pair<SearchInput, ToolbarTabLayout> pairOfViews = AppBarSearchHelperKt.findSearchInputAndTabs(parent);
            mSearchInput = pairOfViews.getFirst();
            if (mSearchInput != null) {
                ToolbarTabLayout tabLayout = pairOfViews.getSecond();
                if (tabLayout != null) {
                    mToolbarTabLayout = new WeakReference<>(tabLayout);
                }
            }
            addScrollListener(parent, child);
            mLastElevationValue = mElevationNone;
            return onLayoutChildInternal(parent, child, layoutDirection);
        } else {
            boolean canScrollContentUnderAppBarLayout = canScrollContentUnderAppBarLayout(parent);
            updateElevationValue(canScrollContentUnderAppBarLayout);
            View pinnedView = getPinnedView(child);
            if (isElevationChanged(child)) {
                return onLayoutChildInternal(parent, child, layoutDirection);
            }
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    public void setShouldHideElevation(boolean shouldHideElevation) {
        mShouldHideElevation = shouldHideElevation;
    }

    /**
     * Нужно ли игнорировать оффсет поисковой строки при изменении видимой области AppBarLayout.
     *
     * @return true - размер видимой области аппбара задается строго такой, каким он был передан
     * false - видимая область будет ограничена таким образом, чтобы спрятать поисковую строку под тулбар
     */
    public boolean isIgnoreSearchOffset() {
        return mIgnoreSearchOffset;
    }

    /**
     * Получить величину офсета поисковой строки
     *
     * @return величина офсета поисковой строки, если она отображена на экране
     */
    public int getSearchOffset() {
        if (mSearchInput != null && mSearchInput.getVisibility() != View.GONE) {
            return mSearchInput.getMeasuredHeight();
        }
        return 0;
    }

    /**
     * Сбросить флаг игнорирования оффсета поисковой строки и её текущего положения. После сброса, следующий вызов setExpanded(true)
     * оставит строку поиска под шапкой, если отсутствует выбранный фильтр или установлен дефолтный.
     */
    void resetIgnoreSearchOffset() {
        mIgnoreSearchOffset = false;
        mIsCloseState = false;
    }

    /**
     * Показать поисковую строку.
     */
    void expandSearchInput() {
        mIgnoreSearchOffset = true;
        setTopAndBottomOffset(0);
    }

    /**
     * Скрыть поисковую строку.
     */
    void collapseSearchInput() {
        mIgnoreSearchOffset = true;
        setTopAndBottomOffset(-mSearchInput.getMeasuredHeight());
    }

    private void updateSearchInputState(SearchInputState newState) {
        if (mSearchInput != null) {
            SearchInputContext searchInputContext = mSearchInput.getSearchInputContext();
            if (searchInputContext != null) {
                SearchStateRepository.INSTANCE.toggleInputState(searchInputContext.getViewLocationInApplication(), newState);
                mIsCloseState = newState == SearchInputState.HIDDEN;
            }
        }
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onLayoutChildInternal(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, int layoutDirection) {
        super.onLayoutChild(parent, child, layoutDirection);
        setElevation(child);
        return true;
    }

    private void updateElevation(@NonNull AppBarLayout appBarLayout, boolean canScrollContentUnderAppBarLayout) {
        updateElevationValue(canScrollContentUnderAppBarLayout);
        setElevation(appBarLayout);
    }

    private void updateElevationValue(boolean canScrollContentUnderAppBarLayout) {
        mLastElevationValue = canScrollContentUnderAppBarLayout && !mShouldHideElevation ? mElevation : mElevationNone;
    }

    private void setElevation(@NonNull AppBarLayout appBarLayout) {
        View pinnedView = getPinnedView(appBarLayout);
        boolean isPinnedViewVisible = pinnedView != null && pinnedView.getVisibility() == View.VISIBLE;
        float appBarElevation = isPinnedViewVisible ? mElevationNone : mLastElevationValue;
        if (pinnedView != null) {
            float pinnedViewElevation = isPinnedViewVisible ? mLastElevationValue : mElevationNone;
            ViewCompat.setElevation(pinnedView, pinnedViewElevation);
        }
        ViewCompat.setElevation(appBarLayout, appBarElevation);
    }

    private boolean isElevationChanged(@NonNull AppBarLayout appBarLayout) {
        View pinnedView = getPinnedView(appBarLayout);
        if (pinnedView != null && pinnedView.getVisibility() == View.VISIBLE) {
            return getElevation(pinnedView) != mLastElevationValue ||
                    getElevation(appBarLayout) != mElevationNone;
        }
        return getElevation(appBarLayout) != mLastElevationValue;
    }

    @Nullable
    private View getPinnedView(@NonNull AppBarLayout appBarLayout) {
        if (mPinnedView != null) {
            return mPinnedView;
        }
        ViewParent parent = appBarLayout.getParent();
        if (mPinnedViewId != ID_NULL && parent instanceof CoordinatorLayout) {
            mPinnedView = ((CoordinatorLayout) parent).findViewById(mPinnedViewId);
            mPinnedView.setOutlineProvider(new HalfHeightViewOutlineProvider());
        }
        return mPinnedView;
    }

    @Nullable
    private AppBarLayout.LayoutParams getChildCollapsingToolbarLayoutParams(@NonNull AppBarLayout appBarLayout) {
        if (appBarLayout.getChildAt(0) instanceof CollapsingToolbarLayout) {
            return (AppBarLayout.LayoutParams) appBarLayout.getChildAt(0).getLayoutParams();
        }
        return null;
    }

    private static class SavedState extends AbsSavedState {

        float elevation;
        boolean isCloseState;

        SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            elevation = source.readFloat();
            isCloseState = source.readByte() != 0;
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(elevation);
            dest.writeByte((byte) (isCloseState ? 1 : 0));
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }
}
