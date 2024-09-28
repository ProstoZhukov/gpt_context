package ru.tensor.sbis.modalwindows.bottomsheet.binding;

import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOptionsAdapter;

/**
 * Базовый адаптер для отображения списка опций с использованием биндинга.
 * @param <T> - тип вью-модели для отображения
 *
 * @author sr.golovkin
 */
public class UniversalBottomSheetOptionsAdapter<T extends UniversalBottomSheetOption> extends BottomSheetOptionsAdapter<T> {

    public UniversalBottomSheetOptionsAdapter(@NonNull List<T> options,
                                              @Nullable Listener<T> listener) {
        super(options, listener);
    }

    // region RecyclerView.Adapter impl

    @Override
    public int getItemViewType(int position) {
        final UniversalBottomSheetOption option = getOption(position);
        if (option != null) {
           return option.getViewType();
        }
        return super.getItemViewType(position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.@NotNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final UniversalBottomSheetOption option = getOption(position);
        if (option != null && holder instanceof UniversalOptionHolder) {
            //noinspection rawtypes
            ((UniversalOptionHolder) holder).bind(option);
        }
    }

    // endregion

    // region Utility methods

    @NonNull
    public <V extends ViewDataBinding> V createBinding(@LayoutRes int layoutId, ViewGroup parent) {
        return DataBindingUtil.inflate(getInflater(parent), layoutId, parent, false);
    }

    // endregion
}
