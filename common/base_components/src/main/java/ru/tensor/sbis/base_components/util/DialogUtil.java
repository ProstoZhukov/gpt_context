package ru.tensor.sbis.base_components.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;
import ru.tensor.sbis.base_components.BaseProgressDialogFragment;
import ru.tensor.sbis.design.R;

/**
 * Утилитные методы для работы с диалогом
 */
@SuppressWarnings("unused")
public class DialogUtil {

    private static final String progressDialogTag = BaseProgressDialogFragment.class.getSimpleName();

    /** SelfDocumented */
    public static BaseProgressDialogFragment createProgressDialogFragment(@NonNull Context context) {
        return createProgressDialogFragment(context, null);
    }

    /** SelfDocumented */
    public static BaseProgressDialogFragment createProgressDialogFragment(@NonNull Context context, @StringRes int messageResId) {
        return createProgressDialogFragment(context, messageResId == 0 ? null : context.getString(messageResId));
    }

    /** SelfDocumented */
    public static BaseProgressDialogFragment createProgressDialogFragment(@NonNull Context context, @Nullable String message) {
        BaseProgressDialogFragment progressDialogFragment = new BaseProgressDialogFragment();
        progressDialogFragment.init(null, message == null ? context.getString(R.string.design_please_wait) : message);
        return progressDialogFragment;
    }

    /** SelfDocumented */
    public static void showProgressDialog(@NonNull Context context, @NonNull FragmentManager fragmentManager) {
        showProgressDialog(createProgressDialogFragment(context), fragmentManager);
    }

    /** SelfDocumented */
    public static void showProgressDialog(@NonNull Context context, @NonNull FragmentManager fragmentManager,
                                          @StringRes int messageResId) {
        showProgressDialog(createProgressDialogFragment(context, messageResId), fragmentManager);
    }

    /** SelfDocumented */
    public static void showProgressDialog(@NonNull Context context, @NonNull FragmentManager fragmentManager,
                                          @Nullable String message) {
        showProgressDialog(createProgressDialogFragment(context, message), fragmentManager);
    }

    /** SelfDocumented */
    public static void showProgressDialog(@NonNull BaseProgressDialogFragment progressDialogFragment,
                                          @NonNull FragmentManager fragmentManager) {
        BaseProgressDialogFragment fragment = getCurrentProgressDialog(fragmentManager);
        if (fragment == null || !fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(progressDialogFragment, progressDialogTag)
                    .commitNowAllowingStateLoss();
        }
    }

    /** SelfDocumented */
    public static void hideProgressDialog(@Nullable BaseProgressDialogFragment progressDialogFragment) {
        if (progressDialogFragment != null && progressDialogFragment.isAdded()) {
            progressDialogFragment.dismissAllowingStateLoss();
        }
    }

    /** SelfDocumented */
    @Nullable
    public static BaseProgressDialogFragment getCurrentProgressDialog(@NonNull FragmentManager fragmentManager) {
        return (BaseProgressDialogFragment) fragmentManager.findFragmentByTag(progressDialogTag);
    }

    /** SelfDocumented */
    public static void hideProgressDialog(@NonNull FragmentManager fragmentManager) {
        BaseProgressDialogFragment fragment = getCurrentProgressDialog(fragmentManager);
        if (fragment != null && fragment.isAdded()) {
            fragment.dismissAllowingStateLoss();
        }
    }
}
