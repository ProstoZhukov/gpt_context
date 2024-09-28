package ru.tensor.sbis.modalwindows.bottomsheet.check;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import ru.tensor.sbis.modalwindows.R;
import ru.tensor.sbis.modalwindows.bottomsheet.OnOptionClickListener;
import ru.tensor.sbis.modalwindows.bottomsheet.binding.UniversalOptionHolder;
import ru.tensor.sbis.modalwindows.databinding.ModalwindowsBottomSheetOptionItemCheckableBinding;

/**
 * SelfDocumented
 * @author sr.golovkin
 */
public class CheckableOptionHolder extends UniversalOptionHolder<CheckableBottomSheetOption> {

    public CheckableOptionHolder(@NonNull ViewGroup parent, @Nullable OnOptionClickListener listener) {
        super(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.modalwindows_bottom_sheet_option_item_checkable, parent, false), listener);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ModalwindowsBottomSheetOptionItemCheckableBinding getBinding() {
        return super.getBinding();
    }
}
