package ru.tensor.sbis.folderspanel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.tensor.sbis.base_components.BaseDialogFragment;
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer;
import ru.tensor.sbis.design_notification.SbisPopupNotification;

/**
 * Диалоговое окно для ввода имени папки при создании/переименовании
 */
public class PickNameDialogFragment extends BaseDialogFragment {

    private static final String FOLDER_BUNDLE_NAME = PickNameDialogFragment.class.getCanonicalName() + ".folderUUID";
    private static final String TITLE_BUNDLE_NAME = PickNameDialogFragment.class.getCanonicalName() + ".title";
    private static final String HINT_BUNDLE_NAME = PickNameDialogFragment.class.getCanonicalName() + ".hint";
    private static final String IS_KEYBOARD_SHOWN = PickNameDialogFragment.class.getCanonicalName() + ".is_keyboard_shown";
    private static final int FOLDER_NAME_LENGTH_LIMIT = 255;

    @Nullable
    private AppCompatEditText mEditText;
    @Nullable
    private Button mSaveButton;
    @Nullable
    private Button mCancelButton;
    @Nullable
    private String previousName;
    @Nullable
    private String mTitle;
    @Nullable
    private String mHint;

    private Disposable mFolderNameDisposable;

    private boolean isKeyboardShown;

    /**
     * Создание экземпляра диалогового окна ввода имени папки
     *
     * @param title        заголовок диалогового окна
     * @param previousName текущее имя папки (или пустая строка)
     * @param hint         текстовая подсказка
     * @return экземпляр диалогового окна
     */
    @NonNull
    public static PickNameDialogFragment newInstance(@NonNull String title, @NonNull String previousName, @NonNull String hint) {
        PickNameDialogFragment fragment = new PickNameDialogFragment();
        Bundle args = new Bundle();
        args.putString(FOLDER_BUNDLE_NAME, previousName);
        args.putString(TITLE_BUNDLE_NAME, title);
        args.putString(HINT_BUNDLE_NAME, hint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_KEYBOARD_SHOWN, isKeyboardShown);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        checkFolderNameListener(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            previousName = args.getString(FOLDER_BUNDLE_NAME);
            mTitle = args.getString(TITLE_BUNDLE_NAME);
            mHint = args.getString(HINT_BUNDLE_NAME);
        }
        if (savedInstanceState != null) {
            isKeyboardShown = savedInstanceState.getBoolean(IS_KEYBOARD_SHOWN);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folderspanel_dialog_edit_folder_edittext, container, false);
        TextInputLayout textInputLayout = view.findViewById(R.id.folderspanel_edit_folder_edittext_layout);
        mEditText = view.findViewById(R.id.folderspanel_edit_folder_edittext);
        mSaveButton = view.findViewById(R.id.folderspanel_edit_folder_button_done);
        mCancelButton = view.findViewById(R.id.folderspanel_edit_folder_button_cancel);
        //noinspection deprecation
        mFolderNameDisposable = RxTextView.afterTextChangeEvents(Objects.requireNonNull(mEditText))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> Objects.requireNonNull(mSaveButton)
                        .setEnabled(Objects.requireNonNull(text.editable()).toString().trim().length() > 0), FallbackErrorConsumer.DEFAULT);
        Objects.requireNonNull(mSaveButton).setEnabled(false);
        textInputLayout.setHint(mHint);
        mEditText.setText(previousName);
        mEditText.setSelection(previousName == null ? 0 : previousName.length());
        mSaveButton.setOnClickListener(v -> {
            String newName = Objects.requireNonNull(mEditText.getText()).toString().trim();
            if (newName.length() <= FOLDER_NAME_LENGTH_LIMIT) {
                closeKeyboard();
                getFolderNameListener().onNameAccepted(newName);
                dismiss();

            } else {
                SbisPopupNotification.pushToast(requireContext(), getString(
                        R.string.folders_panel_create_folder_error_limit_length,
                        FOLDER_NAME_LENGTH_LIMIT));
            }
        });

        if (mCancelButton != null) {
            mCancelButton.setOnClickListener(v -> {
                closeKeyboard();
                dismiss();
                getFolderNameListener().onDialogClose();
            });
        }

        TextView title = view.findViewById(R.id.folderspanel_edit_folder_title);
        title.setText(mTitle);

        if (mEditText.requestFocus()) {
            showKeyboard();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        mFolderNameDisposable.dispose();
        mEditText = null;
        mSaveButton = null;
        mCancelButton = null;
        super.onDestroyView();
    }

    @NonNull
    private FolderPickNameDialogListener getFolderNameListener() {
        if (getActivity() instanceof FolderPickNameDialogListener) {
            return (FolderPickNameDialogListener) getActivity();
        }
        if (getParentFragment() instanceof FolderPickNameDialogListener) {
            return (FolderPickNameDialogListener) getParentFragment();
        }
        throw new ClassCastException(requireActivity().toString() + " or " + requireParentFragment().toString()
                + " must implement " + FolderPickNameDialogListener.class.getSimpleName());
    }

    private void checkFolderNameListener(Context context) {
        if (!(context instanceof FolderPickNameDialogListener) && (getParentFragment() == null || !(getParentFragment() instanceof FolderPickNameDialogListener))) {
            if (getParentFragment() != null) {
                throw new ClassCastException(getParentFragment().toString() + " must implement " + FolderPickNameDialogListener.class.getSimpleName());
            } else {
                throw new ClassCastException(context.toString() + " must implement " + FolderPickNameDialogListener.class.getSimpleName());
            }
        }
    }

    private void showKeyboard() {
        if (!isKeyboardShown) {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            isKeyboardShown = true;
        }
    }

    private void closeKeyboard() {
        if (isKeyboardShown) {
            InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            isKeyboardShown = false;
        }
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        closeKeyboard();
        super.onCancel(dialog);
    }

    /**
     * Интерфейс для оповещения хост-фрагмента о завершении ввода имени папки или отмене
     */
    public interface FolderPickNameDialogListener {

        /** SelfDocumented */
        void onNameAccepted(String name);

        /** SelfDocumented */
        void onDialogClose();

    }
}
