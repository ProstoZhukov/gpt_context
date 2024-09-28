package ru.tensor.sbis.mvp.multiselection.adapter;

import static ru.tensor.sbis.base_components.adapter.checkable.impl.AbstractCheckableListAdapter.NO_POSITION;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.design.R;
import ru.tensor.sbis.mvp.multiselection.MultiSelectionItemClickListener;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;

/**
 * Legacy-код
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public abstract class MultiSelectionViewHolder<T extends MultiSelectionItem> extends AbstractViewHolder<T> {

    protected T mItem;

    protected ImageView mCheckBox;
    protected View mCheckBoxContainer;
    protected View mSeparatorView;
    protected boolean mChecked = false;
    protected boolean mIsSingleChoice = false;

    private long mLastClickTime;
    private static final long CLICK_TIME_INTERVAL = 1000;


    @Nullable
    protected MultiSelectionItemClickListener mOnItemClickListener;

    public MultiSelectionViewHolder(View itemView) {
        super(itemView);
        initViews();
        setupListeners();
        itemView.setClickable(true);
    }

    public MultiSelectionViewHolder(View itemView, boolean isSingleChoice) {
        super(itemView);
        mIsSingleChoice = isSingleChoice;
        initViews();
        setupListeners();
        itemView.setClickable(true);
    }

    protected abstract void initViews();

    @Override
    public void bind(T dataModel) {
        super.bind(dataModel);
        mItem = dataModel;
        mChecked = mItem.isChecked();
        bindCheckBoxBackground();
        itemView.setBackgroundColor(mChecked ? ContextCompat.getColor(itemView.getContext(), R.color.recipient_selection_background_color) : Color.WHITE);
    }

    private void bindCheckBoxBackground() {
        @DrawableRes int resId;
        if (mIsSingleChoice) {
            resId = mChecked ? R.drawable.minus_checkbox_icon : 0;
        } else {
            resId = mChecked ? R.drawable.minus_checkbox_icon : R.drawable.plus_checkbox_icon;
        }
        mCheckBox.setBackgroundResource(resId);
    }

    protected void setupListeners() {
        mCheckBoxContainer.setOnClickListener(view -> {
            int position = getAdapterPosition();
            if (mOnItemClickListener != null && position != NO_POSITION) {
                mOnItemClickListener.onClickCheckbox(mItem, position);
            }
        });

        itemView.setOnClickListener(view -> {
            int position = getAdapterPosition();
            if (mOnItemClickListener != null && position != NO_POSITION) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                mOnItemClickListener.onClickItem(mItem, position);
            }
        });
        itemView.setOnLongClickListener(null);
    }

    /**
     * @SelfDocumented
     */
    public void setOnItemClickListener(@Nullable MultiSelectionItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Обновить состояние чекбокса
     */
    public void updateCheckState() {
        boolean newCheckedState = mItem != null && mItem.isChecked();
        if (mChecked != newCheckedState) {
            mChecked = newCheckedState;
            mCheckBox.setBackgroundResource(mChecked ? R.drawable.minus_checkbox_icon : R.drawable.plus_checkbox_icon);
            itemView.setBackgroundColor(mChecked ? ContextCompat.getColor(itemView.getContext(), R.color.recipient_selection_background_color) : Color.WHITE);
        }
    }

    /**
     * Показать разделитель
     */
    public void showSeparator(boolean show) {
        mSeparatorView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
