package ru.tensor.sbis.modalwindows.bottomsheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.modalwindows.R;

/**
 * Адаптер для списка опций в нижней панели.
 * @param <T> - тип опций
 *
 * @author sr.golovkin
 */
public class BottomSheetOptionsAdapter<T extends BottomSheetOption>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnOptionClickListener {

    protected static final int OPTION_ITEM_VIEW_TYPE = 0;

    /**
     * Список опций.
     */
    @NonNull
    private List<T> mOptions;

    /**
     * Слушатель действий с элементами в списке.
     */
    @Nullable
    private final Listener<T> mListener;

    public BottomSheetOptionsAdapter(@NonNull List<T> options, @Nullable Listener<T> listener) {
        mOptions = options;
        mListener = listener;
    }

    /**
     * Получить список опций в списке.
     * @return список опций
     */
    @SuppressWarnings("unused")
    @NonNull
    public List<T> getOptions() {
        return mOptions;
    }

    /**
     * Задать список опций
     * @param options новые опции
     */
    public void setOptions(@NonNull List<T> options) {
        mOptions = options;
        notifyDataSetChanged();
    }

    /**
     * Получить опцию на указанной позиции.
     * @param position - позиция опции
     * @return опция на указанной позиции, или null - если позиция некорректна
     */
    @Nullable
    public T getOption(int position) {
        if (-1 < position && position < mOptions.size()) {
            return mOptions.get(position);
        }
        return null;
    }

    // region RecyclerView.Adapter impl
    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return OPTION_ITEM_VIEW_TYPE;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (viewType) {
            case OPTION_ITEM_VIEW_TYPE:
                return new BaseBottomSheetOptionHolder(parent, this);
        }
        throw new IllegalArgumentException("Unsupported view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, int position) {
        if (holder instanceof BaseBottomSheetOptionHolder) {
            final BottomSheetOption option = mOptions.get(position);
            if (option == null) {
                throw new IllegalStateException("Bottom sheet mOptionValue on position " + position + " is null!");
            }
            ((BaseBottomSheetOptionHolder) holder).bindOption(option);
        }
    }
    // endregion

    // region OnOptionClickListener impl
    @Override
    public void onOptionClick(int position) {
        if (mListener != null) {
            T option = mOptions.get(position);
            mListener.onOptionClick(option, option.getOptionValue(), position);
        }
    }
    // endregion

    // region Utility methods
    @NonNull
    protected View inflate(@LayoutRes int itemLayoutResId, ViewGroup parent) {
        final LayoutInflater inflater = getInflater(parent);
        return inflater.inflate(itemLayoutResId, parent, false);
    }

    @NonNull
    protected LayoutInflater getInflater(@NonNull View view) {
        return LayoutInflater.from(view.getContext());
    }
    // endregion

    /**
     * Обработчик действий с опциями в списке.
     * @param <T> - тип опций
     */
    @SuppressWarnings("unused")
    public interface Listener<T extends BottomSheetOption> {

        /**
         * Обработать нажатие на опцию в списке.
         * @param option    - опция
         * @param value     - значение
         * @param position  - позиция опции в списке
         */
        void onOptionClick(@NonNull T option, int value, int position);

    }

}
