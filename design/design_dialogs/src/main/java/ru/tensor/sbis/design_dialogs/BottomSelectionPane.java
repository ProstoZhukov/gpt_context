package ru.tensor.sbis.design_dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.design_dialogs.dialogs.OnDialogItemClickListener;
/**
 * @deprecated use {@link ru.tensor.sbis.design_dialogs.fragment.BottomSelectionPane} instead.
 */
@Deprecated
public class BottomSelectionPane extends BottomPane {

    public static final String TAG = BottomSelectionPane.class.getSimpleName();
    public static final String TITLE_STATE = TAG + ".title_state";
    public static final String ITEMS_ARRAY_RES_STATE = TAG + ".items_array_res_state";
    public static final String DIALOG_CODE_STATE = TAG + ".dialog_code_state";

    private static final String CUSTOM_VALUES = TAG + "custom_values";

    public static BottomSelectionPane newInstance(int dialogCode, @Nullable String title, @ArrayRes int arrayRes) {
        BottomSelectionPane instance = new BottomSelectionPane();
        Bundle arguments = new Bundle();
        arguments.putInt(DIALOG_CODE_STATE, dialogCode);
        arguments.putString(TITLE_STATE, title);
        arguments.putInt(ITEMS_ARRAY_RES_STATE, arrayRes);
        arguments.putStringArrayList(CUSTOM_VALUES, null);
        instance.setArguments(arguments);
        return instance;
    }

    public static BottomSelectionPane newInstance(int dialogCode, @Nullable String title, @NonNull ArrayList<String> values) {
        BottomSelectionPane instance = new BottomSelectionPane();
        Bundle arguments = new Bundle();
        arguments.putInt(DIALOG_CODE_STATE, dialogCode);
        arguments.putString(TITLE_STATE, title);
        arguments.putStringArrayList(CUSTOM_VALUES, values);
        instance.setArguments(arguments);
        return instance;
    }

    public static void show(int dialogCode, @Nullable String title, @ArrayRes int array, @NonNull FragmentManager fragmentManager) {
        BottomSelectionPane bottomDialogFragment = (BottomSelectionPane) fragmentManager.findFragmentByTag(TAG);
        if (bottomDialogFragment != null) {
            bottomDialogFragment.dismiss();
        }
        bottomDialogFragment = BottomSelectionPane.newInstance(dialogCode, title, array);
        bottomDialogFragment.show(fragmentManager, TAG);
    }

    public static void showAllowingStateLoss(int dialogCode, @Nullable String title, @NonNull ArrayList<String> values, @NonNull FragmentManager fragmentManager) {
        BottomSelectionPane bottomSelectionPane = (BottomSelectionPane) fragmentManager.findFragmentByTag(TAG);
        if (bottomSelectionPane != null) {
            bottomSelectionPane.dismiss();
        }
        bottomSelectionPane = newInstance(dialogCode, title, values);
        bottomSelectionPane.showAllowingStateLoss(fragmentManager, TAG);
    }

    public static void showAllowingStateLoss(int dialogCode, @Nullable String title, @ArrayRes int array, FragmentManager fragmentManager) {
        BottomSelectionPane bottomDialogFragment = (BottomSelectionPane) fragmentManager.findFragmentByTag(TAG);
        if (bottomDialogFragment != null) {
            bottomDialogFragment.dismiss();
        }
        bottomDialogFragment = BottomSelectionPane.newInstance(dialogCode, title, array);
        bottomDialogFragment.showAllowingStateLoss(fragmentManager, TAG);
    }

    public void showAllowingStateLoss(@NonNull FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.design_dialogs_bottom_sheet_selection_dialog, container);

        Bundle args = getArguments();

        SbisTextView txtTitle = mainView.findViewById(R.id.design_dialogs_title);
        String title = null;
        if (args != null) {
            title = args.getString(TITLE_STATE);
        }
        if (title != null) {
            txtTitle.setText(title);
        } else {
            txtTitle.setVisibility(View.GONE);
        }

        ListView listView = mainView.findViewById(android.R.id.list);
        ListAdapter adapter = getAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            int dialogCodeState = 0;
            if (args != null) {
                dialogCodeState = args.getInt(DIALOG_CODE_STATE);
            }
            getItemClickListener().onItem(dialogCodeState, position);
            dismiss();
        });

        mainView.findViewById(R.id.design_dialogs_close_area).setOnClickListener(v -> dismiss());

        return mainView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        checkItemClickListener(context);
    }

    @NonNull
    private ListAdapter getAdapter() {
        ArrayList<String> customValues = null;
        Bundle args = getArguments();
        if (args != null) {
            customValues = args.getStringArrayList(CUSTOM_VALUES);
        }
        if (customValues == null) {
            return new ArrayAdapter<>(getContext(), R.layout.design_dialogs_bottom_sheet_selection_item, getResources().getStringArray(getArguments().getInt(ITEMS_ARRAY_RES_STATE)));
        } else {
            return new ArrayAdapter<>(getContext(), R.layout.design_dialogs_bottom_sheet_selection_item, customValues);
        }
    }

    @NonNull
    protected OnDialogItemClickListener getItemClickListener() {
        if (getActivity() == null
                && getParentFragment() == null
                && getTargetFragment() == null) {
            throw new ClassCastException("Caller must not be null!");
        }
        if (getParentFragment() instanceof OnDialogItemClickListener) {
            return (OnDialogItemClickListener) getParentFragment();
        }
        if (getTargetFragment() instanceof OnDialogItemClickListener) {
            return (OnDialogItemClickListener) getTargetFragment();
        }
        if (getActivity() instanceof OnDialogItemClickListener) {
            return (OnDialogItemClickListener) getActivity();
        }
        throw new ClassCastException("Caller must implement " + OnDialogItemClickListener.class.getSimpleName());
    }

    private void checkItemClickListener(@NonNull Context context) {
        if (!(context instanceof OnDialogItemClickListener) &&
                (getParentFragment() == null || !(getParentFragment() instanceof OnDialogItemClickListener))
                && (getTargetFragment() == null || !(getTargetFragment() instanceof OnDialogItemClickListener))) {
            if (getTargetFragment() != null) {
                throw new ClassCastException(getTargetFragment().toString() + " must implement " + OnDialogItemClickListener.class.getSimpleName());
            } else if (getParentFragment() != null) {
                throw new ClassCastException(getParentFragment().toString() + " must implement " + OnDialogItemClickListener.class.getSimpleName());
            } else {
                throw new ClassCastException(context.toString() + " must implement " + OnDialogItemClickListener.class.getSimpleName());
            }
        }
    }
}
