package ru.tensor.sbis.design_dialogs.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetBehavior;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialog;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialogFragment;
/**
 * Всплывающее снизу окно(BottomSheet), реализовано на {@link CustomBottomSheetDialog}
 * с кастомным Behavior
 */
public class BottomPane extends CustomBottomSheetDialogFragment {

    @SuppressWarnings("rawtypes")
    @Nullable
    private CustomBottomSheetBehavior mBottomSheetBehavior;
    @Nullable
    private View.OnLayoutChangeListener mBottomSheetContentLayoutChangeListener;
    @Nullable
    private View mBottomSheetView;

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBottomSheetContentLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (mBottomSheetBehavior != null && bottom - top > 0) {
                mBottomSheetBehavior.setState(CustomBottomSheetBehavior.STATE_EXPANDED);
            }
        };

        getDialog().setOnShowListener(dialog1 -> {
            final CustomBottomSheetDialog bottomSheetDialog = (CustomBottomSheetDialog) dialog1;
            mBottomSheetView = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
            if (mBottomSheetView != null) {
                mBottomSheetBehavior = CustomBottomSheetBehavior.from(mBottomSheetView);
                mBottomSheetBehavior.setState(CustomBottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetBehavior.setSkipCollapsed(true);
                mBottomSheetView.addOnLayoutChangeListener(mBottomSheetContentLayoutChangeListener);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBottomSheetView != null) {
            mBottomSheetView.removeOnLayoutChangeListener(mBottomSheetContentLayoutChangeListener);
            mBottomSheetView = null;
        }
    }
}
