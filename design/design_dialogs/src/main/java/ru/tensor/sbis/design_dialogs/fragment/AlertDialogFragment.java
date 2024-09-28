package ru.tensor.sbis.design_dialogs.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


import ru.tensor.sbis.design.design_dialogs.R;
import ru.tensor.sbis.design.text_span.util.TextFormatUtils;

/**
 * Базовый компонент для показа диалоговых окон, построен на базе DialogFragment
 */
@SuppressWarnings({"FieldCanBeLocal", "deprecation", "unused"})
public class AlertDialogFragment extends DialogFragment {

    public static final String DIALOG_CODE_STATE = AlertDialogFragment.class.getCanonicalName() + ".dialog_code_state";
    private static final String TITLE_STATE = AlertDialogFragment.class.getCanonicalName() + ".title_state";
    private static final String MESSAGE_STATE = AlertDialogFragment.class.getCanonicalName() + ".message_state";
    public static final String ITEMS_ARRAY_RES_STATE = AlertDialogFragment.class.getCanonicalName() + ".items_array_res_state";
    protected static final String ITEMS_ARRAY_STATE = AlertDialogFragment.class.getCanonicalName() + ".items_array_state";
    private static final String SHOW_CANCEL_STATE = AlertDialogFragment.class.getCanonicalName() + ".show_cancel_state";
    private static final String OK_LABEL_STATE = AlertDialogFragment.class.getCanonicalName() + ".ok_label_state";
    private static final String CANCEL_LABEL_STATE = AlertDialogFragment.class.getCanonicalName() + ".cancel_label_state";
    private static final String CANCELABLE_STATE = AlertDialogFragment.class.getCanonicalName() + ".cancelable_state";
    private static final String SHOW_BUTTONS_STATE = AlertDialogFragment.class.getCanonicalName() + ".show_buttons_state";
    private static final String DIALOG_THEME_RES_STATE = AlertDialogFragment.class.getCanonicalName() + ".dialog_theme_res_state";
    private static final String SHOW_NEUTRAL_STATE = AlertDialogFragment.class.getCanonicalName() + ".show_neutral_state";
    private static final String NEUTRAL_LABEL_STATE = AlertDialogFragment.class.getCanonicalName() + ".neutral_label_state";

    public interface YesNoListener extends ItemClickListener {

        void onYes(int dialogCode);

        void onNo(int dialogCode);
    }

    public interface YesNoNeutralListener extends YesNoListener {

        void onNeutral(int dialogCode);
    }

    protected int mDialogCode;
    private int mItemsResourceArray;
    private ArrayList<String> mItems;
    private boolean mShowCancel;
    private int mOkLabelResource;
    private int mCancelLabelResource;
    private int mNeutralLabelResource;
    private boolean mCancelable;
    private boolean mShowNeutralButton;
    private boolean mShowButtons = true;

    @SuppressWarnings("deprecation")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mDialogCode = arguments.getInt(DIALOG_CODE_STATE);
        String title = arguments.getString(TITLE_STATE);
        CharSequence message = arguments.getCharSequence(MESSAGE_STATE);
        mItemsResourceArray = arguments.getInt(ITEMS_ARRAY_RES_STATE);
        mItems = arguments.getStringArrayList(ITEMS_ARRAY_STATE);
        mShowCancel = arguments.getBoolean(SHOW_CANCEL_STATE);
        mOkLabelResource = arguments.getInt(OK_LABEL_STATE);
        mCancelLabelResource = arguments.getInt(CANCEL_LABEL_STATE);
        mCancelable = arguments.getBoolean(CANCELABLE_STATE);
        mShowButtons = arguments.getBoolean(SHOW_BUTTONS_STATE);
        mShowNeutralButton = arguments.getBoolean(SHOW_NEUTRAL_STATE);
        mNeutralLabelResource = arguments.getInt(NEUTRAL_LABEL_STATE);
        int dialogTheme = arguments.getInt(DIALOG_THEME_RES_STATE);

        final Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, dialogTheme > 0 ? dialogTheme : R.style.SbisAlertDialogTheme);
        if (title != null) {
            builder.setTitle(TextFormatUtils.getCustomFontSpannableString(title, ru.tensor.sbis.design.R.font.roboto_regular, context));
        }
        if (message != null) {
            if (message instanceof Spannable) {
                builder.setMessage(message);
            } else {
                //resolving custom font problems
                builder.setMessage(TextFormatUtils.getCustomFontSpannableString(message.toString(), ru.tensor.sbis.design.R.font.roboto_regular, context));
            }
        }
        if (mItemsResourceArray <= 0 && mItems == null || mShowButtons) {
            builder.setPositiveButton(mOkLabelResource, (dialog, which) -> {
                // Do nothing here because we override this callback in onStart()
                // to prevent dialog dismissing when positive button is clicked
            });
        }
        if (mShowCancel) {
            builder.setNegativeButton(mCancelLabelResource, (dialog, which) -> {
                dismiss();
                if (getYesNoListener() != null) {
                    getYesNoListener().onNo(mDialogCode);
                }
            });
        }
        if (mShowNeutralButton) {
            builder.setNeutralButton(mNeutralLabelResource, (dialog, which) -> {
                dismiss();
                if (getYesNoListener() != null) {
                    ((YesNoNeutralListener) getYesNoListener()).onNeutral(mDialogCode);
                }
            });
        }
        if (mItems != null) {
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            builder.setItems(mItems.toArray(new String[mItems.size()]), (dialogInterface, which) -> {
                if (getYesNoListener() != null) {
                    getYesNoListener().onItem(mDialogCode, which);
                }
            });
        } else if (mItemsResourceArray > 0) {
            builder.setItems(mItemsResourceArray, (dialogInterface, which) -> {
                if (getYesNoListener() != null) {
                    getYesNoListener().onItem(mDialogCode, which);
                }
            });
        }

        Dialog alertDialog = builder.create();
        setCancelable(mCancelable);
        alertDialog.setCanceledOnTouchOutside(mCancelable);

        return alertDialog;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (mShowNeutralButton) {
            checkYesNoNeutralListener(context);
        } else {
            checkYesNoListener(context);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            TextView message = dialog.findViewById(android.R.id.message);
            if (message != null) {
                message.setMovementMethod(LinkMovementMethod.getInstance());
            }
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if (mCancelable) {
                    dismiss();
                }

                if (getYesNoListener() != null) {
                    getYesNoListener().onYes(mDialogCode);
                }
            });
        }
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getParentFragment() instanceof AlertDismissListener) {
            ((AlertDismissListener) getParentFragment()).onDialogDissmissed();
        }
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, boolean showCancel) {
        return newInstance(dialogCode, title, null, showCancel);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, String message, boolean showCancel) {
        return newInstance(dialogCode, title, message, showCancel, 0, null, android.R.string.ok, android.R.string.cancel, true, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, boolean showCancel, int okLabelResource, int cancelLabelResource) {
        return newInstance(dialogCode, title, null, showCancel, 0, null, okLabelResource, cancelLabelResource, true, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, String message, boolean showCancel, int okLabelResource,
                                                  int cancelLabelResource) {
        return newInstance(dialogCode, title, message, showCancel, 0, null, okLabelResource, cancelLabelResource, true, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, CharSequence message, boolean showCancel, int okLabelResource,
                                                  int cancelLabelResource, @StyleRes int dialogStyle) {
        return newInstance(dialogCode, title, message, showCancel, 0, null, okLabelResource, cancelLabelResource, true, false, dialogStyle, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode,
                                                  String message,
                                                  int okLabelResource,
                                                  boolean cancelable) {
        return newInstance(dialogCode, null, message, false, 0, null, okLabelResource, 0, cancelable, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, int array) {
        return newInstance(dialogCode, title, null, false, array, null, android.R.string.ok, android.R.string.cancel, true, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, ArrayList<String> array) {
        return newInstance(dialogCode, title, null, false, 0, array, android.R.string.ok, android.R.string.cancel, true, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, ArrayList<String> array, int labelCloseResource) {
        return newInstance(dialogCode, title, null, false, 0, array, labelCloseResource, android.R.string.cancel, true, false, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, ArrayList<String> array,
                                                  int okLabelResource, int cancelLabelResource, boolean showButtons) {
        return newInstance(dialogCode, title, null, true, 0, array, okLabelResource, cancelLabelResource, true, showButtons, 0, false, 0);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title, String message, boolean showCancel, boolean showNeutral, int okLabelResource,
                                                  int cancelLabelResource, int neutralLabelResource) {
        return newInstance(dialogCode, title, message, showCancel, 0, null, okLabelResource, cancelLabelResource, true, false, 0, showNeutral, neutralLabelResource);
    }

    public static AlertDialogFragment newInstance(int dialogCode, String title) {
        return newInstance(dialogCode, title, null, false, 0, null, android.R.string.ok, 0, false, false, 0, false, 0);
    }

    private static AlertDialogFragment newInstance(int dialogCode,
                                                   String title,
                                                   CharSequence message,
                                                   boolean showCancel,
                                                   int arrayRes,
                                                   ArrayList<String> array,
                                                   int okLabel,
                                                   int cancelLabel,
                                                   boolean cancelable,
                                                   boolean showButtons,
                                                   @StyleRes int dialogStyle,
                                                   boolean showNeutral,
                                                   int neutralLabel) {

        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(DIALOG_CODE_STATE, dialogCode);
        arguments.putString(TITLE_STATE, title);
        arguments.putCharSequence(MESSAGE_STATE, message);
        arguments.putBoolean(SHOW_CANCEL_STATE, showCancel);
        arguments.putInt(ITEMS_ARRAY_RES_STATE, arrayRes);
        arguments.putStringArrayList(ITEMS_ARRAY_STATE, array);
        arguments.putInt(OK_LABEL_STATE, okLabel);
        arguments.putInt(CANCEL_LABEL_STATE, cancelLabel);
        arguments.putBoolean(CANCELABLE_STATE, cancelable);
        arguments.putBoolean(SHOW_BUTTONS_STATE, showButtons);
        arguments.putInt(DIALOG_THEME_RES_STATE, dialogStyle);
        arguments.putBoolean(SHOW_NEUTRAL_STATE, showNeutral);
        arguments.putInt(NEUTRAL_LABEL_STATE, neutralLabel);
        alertDialogFragment.setArguments(arguments);
        return alertDialogFragment;
    }

    protected YesNoListener getYesNoListener() {
        if (getActivity() == null
                && getParentFragment() == null
                && getTargetFragment() == null) {
            return null;
        }
        if (getParentFragment() instanceof YesNoListener) {
            return (YesNoListener) getParentFragment();
        }
        if (getTargetFragment() instanceof YesNoListener) {
            return (YesNoListener) getTargetFragment();
        }
        if (getActivity() instanceof YesNoListener) {
            return (YesNoListener) getActivity();
        }
        throw new ClassCastException(getActivity().toString() + " must implement " +
                (mShowNeutralButton ? YesNoNeutralListener.class.getSimpleName() : YesNoListener.class.getSimpleName()));
    }

    private void checkYesNoListener(Context context) {
        if (!(context instanceof YesNoListener) &&
                (getParentFragment() == null || !(getParentFragment() instanceof YesNoListener))
                && (getTargetFragment() == null || !(getTargetFragment() instanceof YesNoListener))) {
            if (getTargetFragment() != null) {
                throw new ClassCastException(getTargetFragment().toString() + " must implement " + YesNoListener.class.getSimpleName());
            } else if (getParentFragment() != null) {
                throw new ClassCastException(getParentFragment().toString() + " must implement " + YesNoListener.class.getSimpleName());
            } else {
                throw new ClassCastException(context.toString() + " must implement " + YesNoListener.class.getSimpleName());
            }
        }
    }

    private void checkYesNoNeutralListener(Context context) {
        if (!(context instanceof YesNoNeutralListener) &&
                (getParentFragment() == null || !(getParentFragment() instanceof YesNoNeutralListener))
                && (getTargetFragment() == null || !(getTargetFragment() instanceof YesNoNeutralListener))) {
            if (getTargetFragment() != null) {
                throw new ClassCastException(getTargetFragment().toString() + " must implement " + YesNoNeutralListener.class.getSimpleName());
            } else if (getParentFragment() != null) {
                throw new ClassCastException(getParentFragment().toString() + " must implement " + YesNoNeutralListener.class.getSimpleName());
            } else {
                throw new ClassCastException(context.toString() + " must implement " + YesNoNeutralListener.class.getSimpleName());
            }
        }
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public interface AlertDismissListener {

        void onDialogDissmissed();
    }
}
