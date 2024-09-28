package ru.tensor.sbis.mvp_extensions.sbisview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;

import ru.tensor.sbis.mvp.multiselection.MultiSelectedItemsPanel;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.mvp_extensions.BuildConfig;
import timber.log.Timber;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
public abstract class SelectedItemsView<DisplayInfo extends SelectedItemsView.SelectedItemsViewDisplayInfo>
        extends FrameLayout implements MultiSelectedItemsPanel {

    public static final int MAX_DISPLAYED_COUNT_VALUE = 999;
    public final static int STATE_EMPTY = 0;
    public final static int STATE_NOT_EMPTY = 1;

    protected int mItemsFullSize = 0;

    private View mSelectedItemsPanel;
    protected LinearLayout mItemsContainer;
    protected TextView mCounter;
    private TextView mRollUpArrow;


    @SelectedItemsViewState
    private int mDisplayingState = STATE_EMPTY;

    protected int mItemMarginRight;
    protected int mItemsContainerMaxWidth;

    @Nullable
    private OnArrowsClickListener mOnArrowsClickListener;
    private boolean mIsSelectedExpanded;
    private boolean mIsArrowButtonEnabled = true;

    public SelectedItemsView(Context context) {
        this(context, null);
    }

    public SelectedItemsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SelectedItemsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        initViews(context);
        initUsingResources(context, attrs);

        setVisibility(GONE);

        initListeners();
    }

    @CallSuper
    protected void initViews(@NonNull Context context) {
        final View mainView = LayoutInflater.from(context).inflate(ru.tensor.sbis.common.R.layout.selected_items_panel, this, true);
        mSelectedItemsPanel = mainView.findViewById(ru.tensor.sbis.common.R.id.checked_items_panel);
        mItemsContainer = mainView.findViewById(ru.tensor.sbis.common.R.id.selected_items_layout);
        mCounter = mainView.findViewById(ru.tensor.sbis.common.R.id.selector_counter);
        mRollUpArrow = mainView.findViewById(ru.tensor.sbis.common.R.id.roll_up_arrow);
    }

    @CallSuper
    protected void initListeners() {
        mSelectedItemsPanel.setOnClickListener(v -> {
            if (mOnArrowsClickListener != null) {
                mOnArrowsClickListener.onExpandArrowClick();
            }
        });
        mRollUpArrow.setOnClickListener(v -> {
            if (mOnArrowsClickListener != null) {
                mOnArrowsClickListener.onRollUpArrowClick();
            }
        });
    }

    @CallSuper
    protected void initUsingResources(@NonNull Context context, @Nullable AttributeSet attrs) {
        mItemMarginRight = context.getResources().getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.design_selected_panel_item_margin_right);
    }

    @Override
    public final void addItem(@NonNull MultiSelectionItem item) {
        addItemToList(item);
        setupItems();
    }

    @Override
    public final void removeItem(@NonNull MultiSelectionItem item) {
        if (isItemTypeCorrect(item)) {
            if (removeVerifiedItem(item)) {
                mItemsFullSize -= item.getItemCount();
            }
        } else {
            if (BuildConfig.DEBUG) {
                Timber.e("SelectedItemsView::removeItem unacceptable item type");
                throw new IllegalStateException("SelectedItemsView::removeItem unacceptable item type");
            }
        }
        setupItems();
    }

    protected abstract boolean isItemTypeCorrect(@NonNull MultiSelectionItem item);

    protected abstract boolean removeVerifiedItem(@NonNull MultiSelectionItem item);

    protected abstract boolean addVerifiedItem(@NonNull MultiSelectionItem item);

    private void setRollUpArrowVisibility() {
        if (mIsSelectedExpanded) {
            setVisibility(VISIBLE);
            setBackgroundColor(ContextCompat.getColor(getContext(), ru.tensor.sbis.design.R.color.recipient_selection_background_color));
            mSelectedItemsPanel.setVisibility(GONE);
            mRollUpArrow.setVisibility(mIsArrowButtonEnabled ? VISIBLE : GONE);
        } else if (mDisplayingState == STATE_NOT_EMPTY) {
            //animate only first appearing of selected items view
            setVisibility(VISIBLE);
            setBackgroundColor(Color.WHITE);
            mRollUpArrow.setVisibility(GONE);
            mSelectedItemsPanel.setVisibility(VISIBLE);
            if (mItemsContainerMaxWidth == 0) calcItemsContainerMaxWidth();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void setItems(@NonNull Collection<MultiSelectionItem> items) {
        clearData();
        mItemsFullSize = 0;
        for (MultiSelectionItem item : items) {
            addItemToList(item);
        }
        setupItems();
    }

    protected final void addItemToList(@NonNull MultiSelectionItem item) {
        if (isItemTypeCorrect(item)) {
            if (addVerifiedItem(item)) {
                mItemsFullSize += item.getItemCount();
            }
        } else {
            if (BuildConfig.DEBUG) {
                Timber.e("SelectedItemsView::addItem unacceptable item type");
                throw new IllegalStateException("SelectedItemsView::addItem unacceptable item type");
            }
        }
        setupItems();
    }

    @Override
    public void setupToggle(boolean isExpanded) {
        if (mIsSelectedExpanded != isExpanded) {
            mIsSelectedExpanded = isExpanded;
            setRollUpArrowVisibility();
        }
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public boolean isExpanded() {
        return mIsSelectedExpanded;
    }

    @Override
    public void enableRollUpArrow(boolean isEnabled) {
        mIsArrowButtonEnabled = isEnabled;
        if (!isEnabled) {
            mRollUpArrow.setVisibility(GONE);
        }
    }

    @Override
    public void setOnArrowsClickListener(@Nullable OnArrowsClickListener onArrowsClickListener) {
        mOnArrowsClickListener = onArrowsClickListener;
    }

    protected abstract void clearData();

    protected abstract void clearIndexes();

    protected void setupItems() {
        calculateDisplayState();
        clearIndexes();
        DisplayInfo displayInfo;

        switch (mDisplayingState) {
            case STATE_NOT_EMPTY: {
                displayInfo = setupItemsIndexes();
                displayItems(displayInfo);
                setRollUpArrowVisibility();
                break;
            }
            case STATE_EMPTY:
                setRollUpArrowVisibility();
                break;
            default: {
                if (BuildConfig.DEBUG) {
                    Timber.e("SelectedItemsView::setItems items displaying state not found");
                    throw new IllegalStateException("SelectedItemsView::setItems items displaying state not found");
                }
            }
        }
        if (mCounter.getText() == null || mCounter.getText().length() == 0) {
            mCounter.setVisibility(View.GONE);
        } else {
            mCounter.setVisibility(View.VISIBLE);
        }
    }

    private void calculateDisplayState() {
        if (isEmptyContent()) {
            mDisplayingState = STATE_EMPTY;
        } else {
            mDisplayingState = STATE_NOT_EMPTY;
        }
    }

    protected abstract boolean isEmptyContent();

    private void clearLayout() {
        mItemsContainer.removeAllViewsInLayout();
    }

    private void calcItemsContainerMaxWidth() {
        // Для расчёта mItemsContainerMaxWidth необходимо получить ширину вью
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (mItemsContainerMaxWidth == 0) {
                    Resources resources = getContext().getResources();
                    mItemsContainerMaxWidth = getWidth() - resources.getDimensionPixelSize(ru.tensor.sbis.common.R.dimen.selected_panel_left_margin)
                            - resources.getDimensionPixelSize(ru.tensor.sbis.common.R.dimen.selected_panel_right_margin)
                            - resources.getDimensionPixelSize(ru.tensor.sbis.common.R.dimen.selected_panel_counter_right_margin)
                            - resources.getDimensionPixelSize(ru.tensor.sbis.common.R.dimen.selected_panel_toggle_width);
                    // Выполнить переотрисовку вью согласно новой mItemsContainerMaxWidth
                    setupItems();
                }
                return false;
            }
        });
    }

    @CallSuper
    protected void displayItems(@NonNull DisplayInfo displayInfo) {
        clearLayout();
    }

    @NonNull
    protected abstract DisplayInfo setupItemsIndexes();

    protected int measureCounterWidth(boolean hasInvisibleItems, int count) {
        if (count > 0) {
            final int targetWidth = (int) mCounter.getPaint().measureText(formatCount(hasInvisibleItems, Math.min(count, MAX_DISPLAYED_COUNT_VALUE))) +
                    mCounter.getPaddingLeft() + mCounter.getPaddingRight();
            return Math.max(targetWidth, mCounter.getMinimumWidth());
        } else {
            return 0;
        }
    }

    @NonNull
    protected String formatCount(boolean hasInvisibleItems, int count) {
        return count > 0 && hasInvisibleItems ? (count >= MAX_DISPLAYED_COUNT_VALUE ? "+" + MAX_DISPLAYED_COUNT_VALUE : String.valueOf(count)) : "";
    }

    /**
     * Legacy-код
     */
    @SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
    public static class SelectedItemsViewDisplayInfo {

        private boolean mHasInvisibleItems;
        private int mTotalItemsCount;

        public SelectedItemsViewDisplayInfo(int totalItemsCount, boolean hasInvisibleItems) {
            mHasInvisibleItems = hasInvisibleItems;
            mTotalItemsCount = totalItemsCount;
        }

        public boolean hasInvisibleItems() {
            return mHasInvisibleItems;
        }

        public int getTotalItemsCount() {
            return mTotalItemsCount;
        }
    }

    @IntDef({STATE_EMPTY, STATE_NOT_EMPTY})
    @Retention(RetentionPolicy.SOURCE)
    @interface SelectedItemsViewState {
    }
}
