package ru.tensor.sbis.design_dialogs.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design.sbis_text_view.SbisTextView;
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.CustomBottomSheetDialog;

/**
 * Расширение {@link BottomPane}, содержит набор вспомогательных(утильных) метод и оберток для работы с {@link BottomPane}
 */
public class BottomSelectionPane extends BottomPane {

    protected static final String CUSTOM_STRING_VALUES = "custom_string_values";
    private static final String CUSTOM_RES_VALUES = "custom_resource_values";
    private static final String WITH_ICONS_BOOLEAN = "with_icons_boolean";
    public static final String ITEMS_ARRAY_RES_STATE = BottomSelectionPane.class.getCanonicalName() + ".items_array_res_state";
    public static final String DIALOG_CODE_STATE = BottomSelectionPane.class.getCanonicalName() + ".dialog_code_state";

    public static BottomSelectionPane newInstance(int dialogCode, @ArrayRes int arrayRes) {
        BottomSelectionPane instance = new BottomSelectionPane();
        Bundle arguments = new Bundle();
        arguments.putInt(DIALOG_CODE_STATE, dialogCode);
        arguments.putInt(ITEMS_ARRAY_RES_STATE, arrayRes);
        arguments.putStringArrayList(CUSTOM_STRING_VALUES, null);
        arguments.putBoolean(WITH_ICONS_BOOLEAN, false);
        instance.setArguments(arguments);
        return instance;
    }

    public static BottomSelectionPane newInstance(int dialogCode, @NonNull ArrayList<String> values) {
        BottomSelectionPane instance = new BottomSelectionPane();
        Bundle arguments = new Bundle();
        arguments.putInt(DIALOG_CODE_STATE, dialogCode);
        arguments.putStringArrayList(CUSTOM_STRING_VALUES, values);
        instance.setArguments(arguments);
        return instance;
    }

    public static BottomSelectionPane newInstance(int dialogCode, @NonNull List<Integer> values, boolean withIcons) {
        BottomSelectionPane instance = new BottomSelectionPane();
        Bundle arguments = new Bundle();
        arguments.putInt(DIALOG_CODE_STATE, dialogCode);
        arguments.putIntegerArrayList(CUSTOM_RES_VALUES, (ArrayList<Integer>) values);
        arguments.putBoolean(WITH_ICONS_BOOLEAN, withIcons);
        instance.setArguments(arguments);
        return instance;
    }

    public static void show(int dialogCode, @ArrayRes int array, @NonNull FragmentManager fragmentManager) {
        BottomSelectionPane bottomDialogFragment = (BottomSelectionPane) fragmentManager
                .findFragmentByTag(BottomSelectionPane.class.getSimpleName());
        if (bottomDialogFragment != null) {
            bottomDialogFragment.dismiss();
        }
        bottomDialogFragment = BottomSelectionPane.newInstance(dialogCode, array);
        bottomDialogFragment.show(fragmentManager, BottomSelectionPane.class.getSimpleName());
    }

    public static void showAllowingStateLoss(int dialogCode, @NonNull ArrayList<String> values, @NonNull FragmentManager fragmentManager) {
        BottomSelectionPane bottomSelectionPane = (BottomSelectionPane) fragmentManager
                .findFragmentByTag(BottomSelectionPane.class.getSimpleName());
        if (bottomSelectionPane != null) {
            bottomSelectionPane.dismiss();
        }
        bottomSelectionPane = newInstance(dialogCode, values);
        bottomSelectionPane.showAllowingStateLoss(fragmentManager, BottomSelectionPane.class.getSimpleName());
    }

    public static void showMenuWithIcons(int dialogCode, @NonNull List<Integer> values, @NonNull FragmentManager fragmentManager) {
        BottomSelectionPane bottomSelectionPane = (BottomSelectionPane) fragmentManager
                .findFragmentByTag(BottomSelectionPane.class.getSimpleName());
        if (bottomSelectionPane != null) {
            bottomSelectionPane.dismiss();
        }
        bottomSelectionPane = newInstance(dialogCode, values, true);
        bottomSelectionPane.showAllowingStateLoss(fragmentManager, BottomSelectionPane.class.getSimpleName());
    }

    public static void showAllowingStateLoss(int dialogCode, @ArrayRes int array, FragmentManager fragmentManager) {
        BottomSelectionPane bottomDialogFragment = (BottomSelectionPane) fragmentManager
                .findFragmentByTag(BottomSelectionPane.class.getSimpleName());
        if (bottomDialogFragment != null) {
            bottomDialogFragment.dismiss();
        }
        bottomDialogFragment = BottomSelectionPane.newInstance(dialogCode, array);
        bottomDialogFragment.showAllowingStateLoss(fragmentManager, BottomSelectionPane.class.getSimpleName());
    }

    public void showAllowingStateLoss(@NonNull FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ListAdapter adapter = getAdapter();
        View mainView = inflater.inflate(R.layout.design_dialogs_bottom_dialog_options_panel, container);
        ListView listView = mainView.findViewById(R.id.design_dialogs_options_list_for_add_contact);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            getItemClickListener().onOptionClick(getArguments().getInt(DIALOG_CODE_STATE), position, adapter.getItem(position));
            dismiss();
        });
        return mainView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        checkItemClickListener(context);
    }

    @NonNull
    private ListAdapter getAdapter() {
        ArrayList<String> customValues = getArguments().getStringArrayList(CUSTOM_STRING_VALUES);
        boolean withIcons = getArguments().getBoolean(WITH_ICONS_BOOLEAN);
        if (withIcons) {
            ArrayList<Integer> resValues = getArguments().getIntegerArrayList(CUSTOM_RES_VALUES);
            return new OptionWithIconAdapter(getContext(), R.layout.design_dialogs_bottom_dialog_option_with_icon_item, resValues);
        } else if (customValues == null) {
            return new ArrayAdapter<>(getContext(), R.layout.design_dialogs_bottom_dialog_option_item, getResources().getStringArray(getArguments().getInt(ITEMS_ARRAY_RES_STATE)));
        } else {
            return new ArrayAdapter<>(getContext(), R.layout.design_dialogs_bottom_dialog_option_item, customValues);
        }
    }

    @Nullable
    protected OptionClickListener getItemClickListener() {
        if (getActivity() == null
                && getParentFragment() == null
                && getTargetFragment() == null) {
            return null;
        }
        if (getParentFragment() instanceof OptionClickListener) {
            return (OptionClickListener) getParentFragment();
        }
        if (getTargetFragment() instanceof OptionClickListener) {
            return (OptionClickListener) getTargetFragment();
        }
        if (getActivity() instanceof OptionClickListener) {
            return (OptionClickListener) getActivity();
        }
        throw new ClassCastException(getActivity().toString() + " must implement " + OptionClickListener.class.getSimpleName());
    }

    private void checkItemClickListener(@NonNull Context context) {
        if (!(context instanceof OptionClickListener) &&
                (getParentFragment() == null || !(getParentFragment() instanceof OptionClickListener))
                && (getTargetFragment() == null || !(getTargetFragment() instanceof OptionClickListener))) {
            if (getTargetFragment() != null) {
                throw new ClassCastException(getTargetFragment().toString() + " must implement " + OptionClickListener.class.getSimpleName());
            } else if (getParentFragment() != null) {
                throw new ClassCastException(getParentFragment().toString() + " must implement " + OptionClickListener.class.getSimpleName());
            } else {
                throw new ClassCastException(context.toString() + " must implement " + OptionClickListener.class.getSimpleName());
            }
        }
    }

    private static class OptionWithIconAdapter extends ArrayAdapter<Integer> {

        private final Context mContext;

        private final List<Integer> mOptionList;

        OptionWithIconAdapter(Context context, int resource, List<Integer> objects) {
            super(context, resource, objects);
            mContext = context;
            mOptionList = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.design_dialogs_bottom_dialog_option_with_icon_item, parent, false);

            Integer currentOption = mOptionList.get(position);

            SbisTextView icon = listItem.findViewById(R.id.design_dialogs_menu_icon);
            Integer iconRes = BottomSelectionIconsUtil.getIconForOption(currentOption);
            if (iconRes == null) {
                icon.setText("");
            } else {
                icon.setText(iconRes);
            }
            icon.setTextColor(mContext.getResources().getColor(BottomSelectionIconsUtil.getIconColorForOption(currentOption)));

            SbisTextView text = listItem.findViewById(R.id.design_dialogs_menu_item_text);
            text.setText(currentOption);
            text.setTextColor(mContext.getResources().getColor(BottomSelectionIconsUtil.getTextColorForOption(currentOption)));

            return listItem;
        }
    }

    public interface OptionClickListener {

        void onOptionClick(int dialogCode, int position, Object item);

    }
}
