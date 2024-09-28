package ru.tensor.sbis.base_components.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import ru.tensor.sbis.base_components.R;
import ru.tensor.sbis.base_components.databinding.BaseComponentsAlertDialogEditTextBinding;
import ru.tensor.sbis.common.util.CommonUtils;
import ru.tensor.sbis.design.text_span.SbisEditText;
import ru.tensor.sbis.design.utils.KeyboardUtils;

/**
 * Legacy-код
 *
 * @author sa.nikitin
 */
@SuppressWarnings({"Convert2Lambda", "unused"})
public class CommonDialogFragment extends BaseDialogFragment {

    public static final String COMMON_TAG = CommonDialogFragment.class.getSimpleName();

    //Тэги аргументов
    private static final String PARAMETERS = "parameters";
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String POSITIVE_BUTTON_TEXT_COLOR = "positive_button_text_color";
    private static final String NEUTRAL_BUTTON_TEXT = "neutral_button_text";
    private static final String NEGATIVE_BUTTON_TEXT = "negative_button_text";
    private static final String ENABLE_POSITIVE_BUTTON_CLICK_LISTENER = "enable_positive_button_click_listener";
    private static final String ENABLE_NEUTRAL_BUTTON_CLICK_LISTENER = "enable_neutral_button_click_listener";
    private static final String ENABLE_NEGATIVE_BUTTON_CLICK_LISTENER = "enable_negative_button_click_listener";
    private static final String ITEMS = "items";
    private static final String ITEM_LAYOUT = "item_layout";
    private static final String ITEM_TEXT_VIEW_RESOURCE_ID = "item_text_view_resource_id";
    private static final String ENABLE_ITEM_CLICK_LISTENER = "enable_item_click_listener";
    private static final String CHECKED_ITEM_POSITION = "checked_item_position";
    private static final String CANCEL_ON_POSITIVE_BUTTON_CLICK = "cancel_on_positive_button_click";
    private static final String CANCELABLE = "cancelable";
    private static final String CANCELED_ON_TOUCH_OUTSIDE = "canceled_on_touch_outside";
    private static final String INPUT_FIELD_ENABLE = "input_field_enable";
    private static final String INPUT_FIELD_INITIAL_VALUE = "input_field_initial_value";
    private static final String INPUT_FIELD_COMPLETE_ONLY_AFTER_CHANGE = "input_field_complete_only_after_change";
    private static final String INPUT_FIELD_MAX_LENGTH = "input_field_max_length";
    private static final String INPUT_FIELD_FOCUSED = "input_field_focused";
    private static final String INPUT_FIELD_ERROR = "input_field_error";
    private static final String INPUT_FIELD_SELECTION_START = "input_field_selection_start";
    private static final String INPUT_FIELD_SELECTION_END = "input_field_selection_end";
    private static final String POSITIVE_BUTTON_ENABLED = "positive_button_enabled";
    private static final String SHOWING_PROGRESS_BAR_ON_INPUT = "showing_progress_bar_on_input";
    private static final String ENABLE_INVALID_SYMBOLS_FILTER = "enable_invalid_symbols_filter";

    private static final int DEFAULT_DELAY = 500;

    private OnPositiveButtonClickListener onPositiveButtonClickListener;
    private OnNeutralButtonClickListener onNeutralButtonClickListener;
    private OnNegativeButtonClickListener onNegativeButtonClickListener;

    private boolean inputFieldEnable;
    private CharSequence inputFieldInitialValue;
    private int mInputMaxLength;
    private boolean inputFieldCompleteOnlyAfterChange;
    @Nullable
    private BaseComponentsAlertDialogEditTextBinding inputBinding;
    private OnInputEndListener onInputEndListener;
    private boolean inputFieldFocusSavedState;
    private int inputFieldSelectionStartSavedState = -1;
    private int inputFieldSelectionEndSavedState = 1;
    private Runnable delayedShowingProgressBar;
    private boolean isRunningDelayedShowingProgressBar;

    private OnItemClickListener onItemClickListener;
    private int checkedItemPosition;

    public interface OnPositiveButtonClickListener {
        void onClickDialogPositiveButton(int dialogCode, @Nullable Bundle parameters);
    }

    public interface OnNeutralButtonClickListener {
        void onClickDialogNeutralButton(int dialogCode, @Nullable Bundle parameters);
    }

    public interface OnNegativeButtonClickListener {
        void onClickDialogNegativeButton(int dialogCode, @Nullable Bundle parameters);
    }

    public interface OnInputEndListener {
        void onDialogInputEnd(int dialogCode, @Nullable Bundle parameters, String enteredText);
    }

    public interface OnItemClickListener {
        void onDialogItemClick(int dialogCode, @Nullable Bundle parameters, int which, CharSequence name);
    }

    private static class InvalidSymbolsFilter implements InputFilter {

        @SuppressWarnings("FieldCanBeLocal")
        @NonNull
        private final String mInvalidSymbols = "[<>:\"|?*/\\\\]";

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            String filtered = source != null ? source.toString().replaceAll(mInvalidSymbols, "") : null;
            return !CommonUtils.equals(source, filtered) ? filtered : null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return new AlertDialog.Builder(requireActivity()).create();
        }

        final int dialogCode = arguments.getInt(BaseDialogFragment.DIALOG_CODE);
        final Bundle parameters = arguments.getBundle(PARAMETERS);
        final CharSequence title = arguments.getCharSequence(TITLE);
        final CharSequence message = arguments.getCharSequence(MESSAGE);
        final boolean enablePositiveButtonClickListener = arguments.getBoolean(ENABLE_POSITIVE_BUTTON_CLICK_LISTENER, false);
        CharSequence positiveButtonText = arguments.getCharSequence(POSITIVE_BUTTON_TEXT);
        final boolean enableNegativeButtonClickListener = arguments.getBoolean(ENABLE_NEGATIVE_BUTTON_CLICK_LISTENER, false);
        CharSequence negativeButtonText = arguments.getCharSequence(NEGATIVE_BUTTON_TEXT);
        final boolean enableNeutralButtonClickListener = arguments.getBoolean(ENABLE_NEUTRAL_BUTTON_CLICK_LISTENER, false);
        CharSequence neutralButtonText = arguments.getCharSequence(NEUTRAL_BUTTON_TEXT);
        final ArrayList<CharSequence> items = arguments.getCharSequenceArrayList(ITEMS);
        final int itemLayoutResource = arguments.getInt(ITEM_LAYOUT, R.layout.base_components_alert_dialog_list_item);
        final int itemTextViewResourceId = arguments.getInt(ITEM_TEXT_VIEW_RESOURCE_ID, R.id.base_components_title);
        final boolean enableItemClickListener = arguments.getBoolean(ENABLE_ITEM_CLICK_LISTENER, false);
        checkedItemPosition = arguments.getInt(CHECKED_ITEM_POSITION, -1);
        final boolean cancelOnPositiveButtonClick = arguments.getBoolean(CANCEL_ON_POSITIVE_BUTTON_CLICK, true);
        final boolean cancelable = arguments.getBoolean(CANCELABLE, true);
        final boolean cancelableOnTouchOutside = arguments.getBoolean(CANCELED_ON_TOUCH_OUTSIDE, true);
        inputFieldEnable = arguments.getBoolean(INPUT_FIELD_ENABLE, false);
        inputFieldInitialValue = arguments.getCharSequence(INPUT_FIELD_INITIAL_VALUE);
        mInputMaxLength = arguments.getInt(INPUT_FIELD_MAX_LENGTH, -1);
        inputFieldCompleteOnlyAfterChange = arguments.getBoolean(INPUT_FIELD_COMPLETE_ONLY_AFTER_CHANGE, false);

        if (inputFieldEnable && onInputEndListener == null) {
            onInputEndListener = getListener(OnInputEndListener.class);
        }
        if (enablePositiveButtonClickListener && onPositiveButtonClickListener == null) {
            onPositiveButtonClickListener = getListener(OnPositiveButtonClickListener.class);
        }
        if (enableNegativeButtonClickListener && onNegativeButtonClickListener == null) {
            onNegativeButtonClickListener = getListener(OnNegativeButtonClickListener.class);
        }
        if (enableNeutralButtonClickListener && onNeutralButtonClickListener == null) {
            onNeutralButtonClickListener = getListener(OnNeutralButtonClickListener.class);
        }
        if (enableItemClickListener && onItemClickListener == null) {
            onItemClickListener = getListener(OnItemClickListener.class);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), ru.tensor.sbis.design.design_dialogs.R.style.AlertDialog);

        if (title != null) {
            builder.setTitle(title);
        }

        if (message != null) {
            builder.setMessage(message);
        }

        if (items != null) {
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(requireActivity(),
                    itemLayoutResource, itemTextViewResourceId, items) {
                @Override
                public boolean isEnabled(int position) {
                    return onItemClickListener != null;
                }
            };
            //noinspection Convert2Lambda
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onItemClickListener != null && !enablePositiveButtonClickListener) {
                        onItemClickListener.onDialogItemClick(
                                dialogCode, parameters, which,
                                items.get(which));
                        dialog.dismiss();
                    }
                }
            });
        }

        if (inputFieldEnable) {
            delayedShowingProgressBar = new Runnable() {
                @Override
                public void run() {
                    if (isRunningDelayedShowingProgressBar) {
                        if (inputBinding != null) {
                            inputBinding.baseComponentsProgressBarContainer.setVisibility(View.VISIBLE);
                        }
                        isRunningDelayedShowingProgressBar = false;
                    }
                }
            };
            inputBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()),
                    R.layout.base_components_alert_dialog_edit_text, null, false);
            inputBinding.baseComponentsEditText.setOnKeyPreImeListener(new SbisEditText.OnKeyPreImeListener() {
                @Override
                public void onKeyPreImeEvent(int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        inputBinding.baseComponentsEditText.clearFocus();
                    }
                }
            });
            if (savedInstanceState == null) {
                if (inputFieldInitialValue != null) {
                    inputBinding.baseComponentsEditText.setText(inputFieldInitialValue);
                }
                inputBinding.baseComponentsEditText.selectAll();
            }

            if (arguments.getBoolean(ENABLE_INVALID_SYMBOLS_FILTER, false)) {
                inputBinding.baseComponentsEditText.setFilters(new InputFilter[]{new InvalidSymbolsFilter()});
            }

            inputBinding.baseComponentsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setEnabledPositiveButton(defineEnableStatusPositiveButton());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mInputMaxLength > 0 && s.length() > mInputMaxLength) {
                        inputBinding.baseComponentsTextInputLayout.setError(getString(R.string.base_components_input_max_length_error));
                    } else if (!TextUtils.isEmpty(inputBinding.baseComponentsTextInputLayout.getError())) {
                        hideInputError();
                    }
                }
            });
            builder.setView(inputBinding.getRoot());
            showInputKeyboardPost();
        }

        if (!cancelable) {
            builder.setCancelable(false);
        }

        if (positiveButtonText != null || enablePositiveButtonClickListener || inputFieldEnable) {
            if (positiveButtonText == null) {
                positiveButtonText = requireActivity().getText(R.string.base_components_dialog_button_ok);
            }
            builder.setPositiveButton(positiveButtonText, null);
        }

        if (negativeButtonText != null || enableNegativeButtonClickListener) {
            if (negativeButtonText == null) {
                negativeButtonText = requireActivity().getText(R.string.base_components_dialog_button_cancel);
            }
            builder.setNegativeButton(negativeButtonText, null);
        }

        if (neutralButtonText != null || enableNeutralButtonClickListener) {
            builder.setNeutralButton(neutralButtonText, null);
        }

        AlertDialog alertDialog = builder.create();
        if (!cancelableOnTouchOutside) {
            alertDialog.setCanceledOnTouchOutside(false);
        }
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                int positiveBtnTextColor = arguments.getInt(POSITIVE_BUTTON_TEXT_COLOR, -1);
                if (positiveBtnTextColor != -1) {
                    positiveButton.setTextColor(ContextCompat.getColor(requireContext(), positiveBtnTextColor));
                }
                if (positiveButton.getVisibility() == View.VISIBLE) {
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onPositiveButtonClickListener != null) {
                                onPositiveButtonClickListener.onClickDialogPositiveButton(dialogCode, parameters);
                            }
                            hideInputKeyboardPost();
                            if (inputFieldEnable && onInputEndListener != null) {
                                setEnabledPositiveButton(false);
                                startShowingProgressBar();
                                onInputEndListener.onDialogInputEnd(dialogCode, parameters, getTrimmedEnteredText());
                            }
                            if (cancelOnPositiveButtonClick) {
                                dialog.dismiss();
                            }
                        }
                    });
                    setEnabledPositiveButton(defineEnableStatusPositiveButton());
                }
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (negativeButton.getVisibility() == View.VISIBLE) {
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onNegativeButtonClickListener != null) {
                                onNegativeButtonClickListener.onClickDialogNegativeButton(dialogCode, parameters);
                            }
                            hideInputKeyboard();
                            dialog.dismiss();
                        }
                    });
                }
                Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                if (neutralButton.getVisibility() == View.VISIBLE) {
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onNeutralButtonClickListener != null) {
                                onNeutralButtonClickListener.onClickDialogNeutralButton(dialogCode, parameters);
                            }
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return alertDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (inputFieldEnable && inputBinding != null) {
                if (savedInstanceState.getBoolean(SHOWING_PROGRESS_BAR_ON_INPUT)) {
                    inputBinding.baseComponentsProgressBarContainer.setVisibility(View.VISIBLE);
                } else {
                    inputBinding.baseComponentsEditText.clearFocus();
                    boolean inputFieldFocused = savedInstanceState.getBoolean(INPUT_FIELD_FOCUSED);
                    if (inputFieldFocused) {
                        showInputKeyboard();
                    }
                    final CharSequence errorMessage = savedInstanceState.getCharSequence(INPUT_FIELD_ERROR);
                    if (errorMessage != null) {
                        setInputError(errorMessage);
                    }
                    int selectionStart = savedInstanceState.getInt(INPUT_FIELD_SELECTION_START, 0);
                    int selectionEnd = savedInstanceState.getInt(INPUT_FIELD_SELECTION_END, 0);
                    inputBinding.baseComponentsEditText.setSelection(selectionStart, selectionEnd);
                }
            }
            setEnabledPositiveButton(savedInstanceState.getBoolean(POSITIVE_BUTTON_ENABLED));
        }
    }

    public void setInputEnabled(boolean enabled) {
        if (inputBinding != null) {
            inputBinding.baseComponentsEditText.setEnabled(enabled);
            if (enabled) {
                showInputKeyboard();
            } else {
                hideInputKeyboardPost();
            }
        }
    }

    public void setInputText(@NonNull String inputText) {
        if (inputBinding != null) {
            inputBinding.baseComponentsEditText.setText(inputText);
            inputBinding.baseComponentsEditText.selectAll();
            showInputKeyboard();
        }
    }

    public void setInputError(@Nullable CharSequence errorMessage) {
        if (inputFieldEnable) {
            stopShowingProgressBar();
            if (errorMessage == null) {
                errorMessage = getString(R.string.base_components_operation_error_message);
            }
            if (inputBinding != null) {
                inputBinding.baseComponentsTextInputLayout.setError(errorMessage);
            }
            setEnabledPositiveButton(false);
        }
    }

    public void hideInputError() {
        if (inputBinding != null) {
            inputBinding.baseComponentsTextInputLayout.setError(null);
            inputBinding.baseComponentsTextInputLayout.setErrorEnabled(false);
        }
    }

    public void startShowingProgressBar() {
        if (inputBinding != null) {
            isRunningDelayedShowingProgressBar = true;
            inputBinding.getRoot().postDelayed(delayedShowingProgressBar, DEFAULT_DELAY);
        }
    }

    public void stopShowingProgressBar() {
        if (inputBinding != null) {
            inputBinding.baseComponentsProgressBarContainer.setVisibility(View.GONE);
            if (isRunningDelayedShowingProgressBar) {
                isRunningDelayedShowingProgressBar = false;
                inputBinding.getRoot().removeCallbacks(delayedShowingProgressBar);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (inputBinding != null) {
            outState.putBoolean(SHOWING_PROGRESS_BAR_ON_INPUT, inputBinding.baseComponentsProgressBarContainer.getVisibility() == View.VISIBLE);
            outState.putCharSequence(INPUT_FIELD_ERROR, inputBinding.baseComponentsTextInputLayout.getError());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                outState.putBoolean(INPUT_FIELD_FOCUSED, inputFieldFocusSavedState);
                outState.putInt(INPUT_FIELD_SELECTION_START, inputFieldSelectionStartSavedState);
                outState.putInt(INPUT_FIELD_SELECTION_END, inputFieldSelectionEndSavedState);
                inputFieldFocusSavedState = false;
                inputFieldSelectionStartSavedState = -1;
                inputFieldSelectionEndSavedState = -1;
            } else {
                outState.putBoolean(INPUT_FIELD_FOCUSED, isFocusedInputField());
                outState.putInt(INPUT_FIELD_SELECTION_START, inputBinding.baseComponentsEditText.getSelectionStart());
                outState.putInt(INPUT_FIELD_SELECTION_END, inputBinding.baseComponentsEditText.getSelectionEnd());
            }
        }
        outState.putInt(CHECKED_ITEM_POSITION, checkedItemPosition);
        outState.putBoolean(POSITIVE_BUTTON_ENABLED, isEnabledPositiveButton());
    }

    @Override
    public void onStop() {
        //На Android.P onStop срабатывает раньше onSaveInstanceState
        if (inputBinding != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            inputFieldFocusSavedState = isFocusedInputField();
            inputFieldSelectionStartSavedState = inputBinding.baseComponentsEditText.getSelectionStart();
            inputFieldSelectionEndSavedState = inputBinding.baseComponentsEditText.getSelectionEnd();
        }
        hideInputKeyboard();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        stopShowingProgressBar();
        inputBinding = null;
        super.onDestroyView();
    }

    private boolean isFocusedInputField() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            View focusedView = dialog.getCurrentFocus();
            return focusedView != null && focusedView.getId() == Objects.requireNonNull(inputBinding).baseComponentsEditText.getId();
        } else {
            return false;
        }
    }

    public void setOnPositiveButtonClickListener(OnPositiveButtonClickListener onPositiveButtonClickListener) {
        this.onPositiveButtonClickListener = onPositiveButtonClickListener;
    }

    public void setOnNeutralButtonClickListener(OnNeutralButtonClickListener onNeutralButtonClickListener) {
        this.onNeutralButtonClickListener = onNeutralButtonClickListener;
    }

    public void setOnNegativeButtonClickListener(OnNegativeButtonClickListener onNegativeButtonClickListener) {
        this.onNegativeButtonClickListener = onNegativeButtonClickListener;
    }

    public void setOnInputEndListener(OnInputEndListener onInputEndListener) {
        this.onInputEndListener = onInputEndListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class Builder {

        protected final Context context;
        protected final Bundle arguments;
        protected boolean enableMessage;
        protected boolean enableInputField;
        protected boolean enableItems;

        public Builder(Context context, int dialogCode) {
            this.context = context;
            arguments = new Bundle();
            arguments.putInt(BaseDialogFragment.DIALOG_CODE, dialogCode);
        }

        /**
         * Используется, чтобы прикрепить к диалогу параметры, которые потребуются в будущем, например, в слушателях нажатия кнопок
         */
        public Builder parameters(@NonNull Bundle parameters) {
            arguments.putBundle(PARAMETERS, parameters);
            return this;
        }

        public Builder title(@StringRes int titleRes) {
            if (titleRes == 0) {
                return this;
            }
            return title(context.getText(titleRes));
        }

        public Builder title(@NonNull CharSequence title) {
            arguments.putCharSequence(TITLE, title);
            return this;
        }

        public Builder message(@StringRes int messageRes) {
            if (messageRes == 0) {
                return this;
            }
            return message(context.getText(messageRes));
        }

        public Builder message(@NonNull CharSequence message) {
            if (enableInputField) {
                throw new IllegalStateException("You cannot use message() when you're using a input field.");
            }
            if (enableItems) {
                throw new IllegalStateException("You cannot use message() when you have items set.");
            }
            arguments.putCharSequence(MESSAGE, message);
            enableMessage = true;
            return this;
        }

        public Builder positiveButtonText(@StringRes int positiveRes) {
            if (positiveRes == 0) {
                return this;
            }
            return positiveButtonText(context.getText(positiveRes));
        }

        public Builder positiveButtonText(@NonNull CharSequence positiveText) {
            arguments.putCharSequence(POSITIVE_BUTTON_TEXT, positiveText);
            return this;
        }

        public Builder positiveButtonTextColor(@ColorRes int positiveBtnTextColor) {
            arguments.putInt(POSITIVE_BUTTON_TEXT_COLOR, positiveBtnTextColor);
            return this;
        }

        /**
         * Требует реализации интерфейса OnPositiveButtonClickListener
         */
        public Builder enablePositiveButtonClickListener() {
            arguments.putBoolean(ENABLE_POSITIVE_BUTTON_CLICK_LISTENER, true);
            return this;
        }

        public Builder neutralButtonText(@StringRes int neutralRes) {
            if (neutralRes == 0) {
                return this;
            }
            return neutralButtonText(context.getText(neutralRes));
        }

        public Builder neutralButtonText(@NonNull CharSequence neutralText) {
            arguments.putCharSequence(NEUTRAL_BUTTON_TEXT, neutralText);
            return this;
        }

        /**
         * Требует реализации интерфейса OnNeutralButtonClickListener
         */
        public Builder enableNeutralButtonClickListener() {
            arguments.putBoolean(ENABLE_NEUTRAL_BUTTON_CLICK_LISTENER, true);
            return this;
        }

        public Builder negativeButtonText(@StringRes int negativeRes) {
            if (negativeRes == 0) {
                return this;
            }
            return negativeButtonText(context.getText(negativeRes));
        }

        public Builder negativeButtonText(@NonNull CharSequence negativeText) {
            arguments.putCharSequence(NEGATIVE_BUTTON_TEXT, negativeText);
            return this;
        }

        /**
         * Требует реализации интерфейса OnNegativeButtonClickListener
         */
        public Builder enableNegativeButtonClickListener() {
            arguments.putBoolean(ENABLE_NEGATIVE_BUTTON_CLICK_LISTENER, true);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder items(@NonNull ArrayList<? extends CharSequence> items) {
            if (enableInputField) {
                throw new IllegalStateException("You cannot use items() when you're using a input field.");
            }
            if (enableMessage) {
                throw new IllegalStateException("You cannot use items() when you're using a message.");
            }
            arguments.putCharSequenceArrayList(ITEMS, (ArrayList<CharSequence>) items);
            enableItems = true;
            return this;
        }

        public Builder items(@NonNull CharSequence... items) {
            ArrayList<CharSequence> itemList = new ArrayList<>();
            Collections.addAll(itemList, items);
            items(itemList);
            return this;
        }

        public Builder items(@ArrayRes int itemsRes) {
            items(context.getResources().getTextArray(itemsRes));
            return this;
        }

        public Builder itemLayout(@LayoutRes int itemLayout, @IdRes int itemTextViewId) {
            arguments.putInt(ITEM_LAYOUT, itemLayout);
            arguments.putInt(ITEM_TEXT_VIEW_RESOURCE_ID, itemTextViewId);
            return this;
        }

        /**
         * Включает режим одиночного выбора. OnItemClickListener отработает, после чего диалог закроется
         * Требует реализации интерфейса OnItemClickListener
         */
        public Builder enableItemClickListener() {
            if (enableInputField) {
                throw new IllegalStateException("You cannot use enableItemClickListener() when you're using a input field.");
            }
            if (enableMessage) {
                throw new IllegalStateException("You cannot use enableItemClickListener() when you're using a message.");
            }
            arguments.putBoolean(ENABLE_ITEM_CLICK_LISTENER, true);
            return this;
        }

        /**
         * Если включен режим одиночного выбора, то checkedItemPosition - позиция изначально выбранного элемента
         */
        public Builder checkedItemPosition(int checkedItemPosition) {
            arguments.putInt(CHECKED_ITEM_POSITION, checkedItemPosition);
            return this;
        }

        public Builder cancelOnPositiveButtonClick(boolean cancelOnPositiveButtonClick) {
            arguments.putBoolean(CANCEL_ON_POSITIVE_BUTTON_CLICK, cancelOnPositiveButtonClick);
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            arguments.putBoolean(CANCELABLE, cancelable);
            arguments.putBoolean(CANCELED_ON_TOUCH_OUTSIDE, cancelable);
            return this;
        }

        public Builder canceledOnTouchOutside(boolean canceledOnTouchOutside) {
            arguments.putBoolean(CANCELED_ON_TOUCH_OUTSIDE, canceledOnTouchOutside);
            return this;
        }

        /**
         * Требует реализации интерфейса OnInputEndListener
         *
         * @param completeOnlyAfterChange Запретить завершение ввода, если начальное значение не изменилось
         */
        public Builder inputField(@Nullable CharSequence initialValue,
                                  boolean completeOnlyAfterChange,
                                  int maxLength,
                                  boolean enableInvalidSymbolsFilter) {
            if (enableItems) {
                throw new IllegalStateException("You cannot use inputField() when you have items set.");
            }
            if (enableMessage) {
                throw new IllegalStateException("You cannot use inputField() when you're using a message.");
            }
            arguments.putCharSequence(INPUT_FIELD_INITIAL_VALUE, initialValue);
            arguments.putBoolean(INPUT_FIELD_COMPLETE_ONLY_AFTER_CHANGE, completeOnlyAfterChange);
            arguments.putInt(INPUT_FIELD_MAX_LENGTH, maxLength);
            arguments.putBoolean(ENABLE_INVALID_SYMBOLS_FILTER, enableInvalidSymbolsFilter);
            arguments.putBoolean(INPUT_FIELD_ENABLE, true);
            enableInputField = true;
            return this;
        }

        public Builder inputField(@Nullable CharSequence initialValue,
                                  boolean completeOnlyAfterChange,
                                  int maxLength) {
            return inputField(initialValue, completeOnlyAfterChange, maxLength, true);
        }

        public CommonDialogFragment build() {
            CommonDialogFragment dialogFragment = new CommonDialogFragment();
            dialogFragment.setArguments(arguments);
            return dialogFragment;
        }

        public void show(@NonNull FragmentManager fragmentManager, String tag) {
            build().show(fragmentManager, tag);
        }
    }

    @NonNull
    private String getTrimmedEnteredText() {
        if (inputBinding != null) {
            Editable text = inputBinding.baseComponentsEditText.getText();
            if (text != null) {
                return text.toString();
            }
        }
        return "";
    }

    private void showInputKeyboard() {
        if (inputFieldEnable && inputBinding != null) {
            KeyboardUtils.showKeyboard(inputBinding.baseComponentsEditText);
        }
    }

    private void showInputKeyboardPost() {
        if (inputFieldEnable && inputBinding != null) {
            KeyboardUtils.showKeyboardPost(inputBinding.baseComponentsEditText);
        }
    }

    private void hideInputKeyboardPost() {
        if (inputFieldEnable && inputBinding != null) {
            inputBinding.baseComponentsEditText.post(this::hideInputKeyboard);
        }
    }

    private void hideInputKeyboard() {
        if (inputFieldEnable && inputBinding != null) {
            KeyboardUtils.hideKeyboard(inputBinding.baseComponentsEditText);
            inputBinding.baseComponentsEditText.clearFocus();
        }
    }

    private boolean defineEnableStatusPositiveButton() {
        if (inputFieldEnable) {
            String trimmedEnteredText = getTrimmedEnteredText();
            boolean enabled = !trimmedEnteredText.isEmpty() &&
                    (mInputMaxLength <= 0 || trimmedEnteredText.length() <= mInputMaxLength) &&
                    !trimmedEnteredText.matches("^[ .]+$");
            if (enabled && inputFieldCompleteOnlyAfterChange && inputFieldInitialValue != null) {
                enabled = !trimmedEnteredText.contentEquals(inputFieldInitialValue);
            }
            return enabled;
        }
        return true;
    }

    private void setEnabledPositiveButton(boolean enabled) {
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setEnabled(enabled);
        }
    }

    private boolean isEnabledPositiveButton() {
        AlertDialog dialog = (AlertDialog) getDialog();
        return dialog != null && dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled();
    }

}
