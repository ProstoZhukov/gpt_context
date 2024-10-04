package ru.tensor.sbis.design_dialogs;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetBehavior;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialog;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialogFragment;

/**
 * @deprecated use {@link ru.tensor.sbis.design_dialogs.fragment.BottomPane} instead.
 */
@Deprecated
@SuppressWarnings("rawtypes")
public class BottomPane extends CustomBottomSheetDialogFragment {

    @Nullable
    private CustomBottomSheetBehavior mBottomSheetBehavior;
    @Nullable
    private View mBottomSheetView;

    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make transparent background for this dialog to place "close" button.
        // Ensure that content has non-transparent background.
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Disable user dragging:
        getDialog().setOnShowListener(dialog1 -> {
            final CustomBottomSheetDialog bottomSheetDialog = (CustomBottomSheetDialog) dialog1;
            mBottomSheetView = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
            if (mBottomSheetView != null) {
                mBottomSheetBehavior = CustomBottomSheetBehavior.from(mBottomSheetView);
                mBottomSheetBehavior.setState(CustomBottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetBehavior.setSkipCollapsed(true);
                //noinspection unused
                mBottomSheetBehavior.setBottomSheetCallback(new CustomBottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == CustomBottomSheetBehavior.STATE_HIDDEN) {
                            bottomSheetDialog.dismiss();
                        }
                        if (newState == CustomBottomSheetBehavior.STATE_DRAGGING) {
                            mBottomSheetBehavior.setState(CustomBottomSheetBehavior.STATE_EXPANDED);
                        }
                    }

                    @SuppressWarnings("unused")
                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    }
                });
            }
        });
    }
}
