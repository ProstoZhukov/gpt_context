package ru.tensor.sbis.modalwindows.bottomsheet.binding;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.modalwindows.bottomsheet.OnOptionClickListener;

/**
 * Универсальный холдер для опции с использованием биндинга.
 * @param <T> - тип вью-модели
 *
 * @author sr.golovkin
 */
public class UniversalOptionHolder<T extends UniversalBottomSheetOption> extends RecyclerView.ViewHolder {

    @NonNull
    protected final ViewDataBinding mBinding;

    public UniversalOptionHolder(@NonNull ViewDataBinding binding, @Nullable OnOptionClickListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;
        if (listener != null) {
            itemView.setOnClickListener(v -> listener.onOptionClick(getAdapterPosition()));
        }
    }

    public void bind(@NonNull T option) {
        setBindingVariables(option.getBindingVariables());
        mBinding.executePendingBindings();
    }

    /**
     * Устанавливаем переменные для биндинга.
     * @param variables - переменные
     */
    protected void setBindingVariables(@NonNull SparseArray<Object> variables) {
        for (int i = 0; i < variables.size(); i++) {
            int variableId = variables.keyAt(i);
            mBinding.setVariable(variableId, variables.get(variableId));
        }
    }

    /**
     * Возвращает биндинг, приведенный к нужному типу.
     */
    protected <B> B getBinding() {
        //noinspection unchecked
        return (B) mBinding;
    }

}
