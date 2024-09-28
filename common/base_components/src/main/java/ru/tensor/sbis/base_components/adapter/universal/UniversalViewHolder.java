package ru.tensor.sbis.base_components.adapter.universal;

import android.util.SparseArray;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.base_components.adapter.checkable.CheckableViewHolder;

/**
 * SelfDocumented
 * @author sa.nikitin, am.boldinov
 */
public class UniversalViewHolder<DM extends UniversalBindingItem> extends AbstractViewHolder<DM> implements CheckableViewHolder {

    @NonNull
    protected final ViewDataBinding mBinding;
    @Nullable
    protected DM mDataModel;

    public UniversalViewHolder(@NonNull ViewDataBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public UniversalViewHolder(@NonNull ViewDataBinding binding, int clickHandlerVariableId,
                               @Nullable Object clickHandler) {
        this(binding);
        if (clickHandler != null) {
            mBinding.setVariable(clickHandlerVariableId, clickHandler);
        }
    }

    public UniversalViewHolder(@NonNull ViewDataBinding binding,
                               @Nullable SparseArray<Object> variables) {
        this(binding);
        if (variables != null) {
            setBindingVars(variables);
        }
    }

    /** SelfDocumented */
    @Nullable
    public DM getDataModel() {
        return mDataModel;
    }

    @Override
    public final void bind(DM dataModel) {
        super.bind(dataModel);
        mDataModel = dataModel;
        setBindingVars(dataModel.getBindingVariables());
        executeBind(dataModel);
        mBinding.executePendingBindings();
    }

    /** SelfDocumented */
    @CallSuper
    public void executeBind(DM dataModel) {
        //override when it needed
    }

    private void setBindingVars(@NonNull SparseArray<Object> variables) {
        for (int i = 0; i < variables.size(); i++) {
            int variableId = variables.keyAt(i);
            mBinding.setVariable(variableId, variables.get(variableId));
        }
    }

    @Override
    public void updateCheckState(boolean checked, boolean animate) {
        //do nothing
    }
}
