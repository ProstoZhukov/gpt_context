package ru.tensor.sbis.modalwindows.bottomsheet;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.tensor.sbis.common.util.BottomSheetUtilKt;
import ru.tensor.sbis.common.util.DeviceConfigurationUtils;
import ru.tensor.sbis.modalwindows.R;
import ru.tensor.sbis.mvp.fragment.BottomSheetDialogPresenterFragment;

/**
 * Абстрактный диалог-фрагмент с дизайном Bottom Sheet.
 *
 * @author sr.golovkin
 *
 * @deprecated TODO: Будет удалено по <a href="https://online.sbis.ru/opendoc.html?guid=4f5ff4ec-2c38-4e09-92e9-89c7809bb3c8&client=3"></a>
 */
@Deprecated
public abstract class AbstractBottomSheet<
        V extends AbstractBottomSheetContract.View,
        P extends AbstractBottomSheetContract.Presenter<V>>
        extends BottomSheetDialogPresenterFragment<V, P> implements AbstractBottomSheetContract.View {

    // region Args
    public static final String ARG_SHOW_CANCEL_BUTTON = "ARG_SHOW_CANCEL_BUTTON";
    public static final boolean SHOW_CANCEL_BUTTON_BY_DEFAULT = true;

    /**
     * Сформировать аргументы с указанием параметра showCancelButton.
     * @param showCancelButton - нужно ли показывать кнопку отмены
     * @return Bundle с аргументом showCancelButton
     */
    public static Bundle buildArgs(boolean showCancelButton) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_SHOW_CANCEL_BUTTON, showCancelButton);
        return bundle;
    }

    /**
     * Нужно ли отображать кнопку отмены.
     * @return true - если нужно, false - иначе
     */
    public boolean isShowCancelButton() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return SHOW_CANCEL_BUTTON_BY_DEFAULT;
        }
        return arguments.getBoolean(ARG_SHOW_CANCEL_BUTTON, SHOW_CANCEL_BUTTON_BY_DEFAULT);
    }
    // endregion

    /**
     * Создать view для диалогового окна.
     */
    @Nullable
    protected abstract View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @SuppressWarnings("unused") @Nullable Bundle savedInstanceState);

    /**
     * Нужно ли отображать диалог в режиме full screen в горизонтальной ориентации.
     * По умолчанию для телефонов - да, для планшетов - нет.
     * @return true - если нужно, false - иначе
     */
    protected boolean isFullscreenInLandscape() {
        return !DeviceConfigurationUtils.isTablet(requireContext());
    }

    @Override
    protected void inject() {
        // There is no need to inject anything apart presenter. So leave this method empty
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (isFullscreenInLandscape()) {
            final int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        return BottomSheetUtilKt.skipCollapseState(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isShowCancelButton()) {
            View root = inflater.inflate(R.layout.modalwindows_bottom_option_sheet_container, container, false);
            root.findViewById(R.id.modalwindows_cancel_button)
                    .setOnClickListener(v -> mPresenter.onCancelClick());
            ViewGroup sheetContainer = root.findViewById(R.id.modalwindows_sheet_container);
            View view = createView(inflater, sheetContainer, savedInstanceState);
            if (view != null && view.getParent() == null) {
                sheetContainer.addView(view);
            }
            return root;
        } else {
            return createView(inflater, container, savedInstanceState);
        }
    }

    /** SelfDocumented */
    public void showAllowingStateLoss(@NonNull FragmentManager manager, @Nullable String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @SuppressWarnings("unused")
    @Override
    public void closeDialog() {
        dismissAllowingStateLoss();
    }

}
