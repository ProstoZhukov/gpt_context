package ru.tensor.sbis.mvp.multiselection.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.tensor.sbis.mvp.multiselection.MultiSelectionResultManager;

/**
 * Часть общего компонента для выбора элементов MultiSelection.
 * Базовый класс, описывающий результат совершения выбора, который передается через BehaviorSubject, хранящийся в {@link MultiSelectionResultManager}.
 * Этот тип результата представляет из себя пару:
 * состояние выбора (RESULT_CANCELED - выбор отменен, RESULT_SUCCESS - выбор совершен, RESULT_CLEARED - выбор пуст)
 * и список выбранных элементов.
 *
 * @deprecated Устаревший подход, переходим на mvi
 */
@Deprecated
@SuppressWarnings("JavaDoc")
public class BaseSelectionResultData {

    /**
     * @SelfDocumented
     */
    public static final int RESULT_CANCELED = 0;
    /**
     * @SelfDocumented
     */
    public static final int RESULT_SUCCESS = 1;
    /**
     * @SelfDocumented
     */
    public static final int RESULT_CLEARED = 2;
    /**
     * @SelfDocumented
     */
    public static final int DEFAULT_REQUEST_CODE = -1;

    @SuppressWarnings("UnusedAssignment")
    protected int mResultCode = RESULT_CLEARED;

    protected int mRequestCode = DEFAULT_REQUEST_CODE;
    @SuppressWarnings("UnusedAssignment")
    @Nullable
    protected List<MultiSelectionItem> mItems = new ArrayList<>();

    public BaseSelectionResultData() {
        this(RESULT_CLEARED, new ArrayList<>());
    }

    public BaseSelectionResultData(@Nullable List<MultiSelectionItem> items) {
        this(RESULT_SUCCESS, items);
    }

    public BaseSelectionResultData(int resultCode, @Nullable List<MultiSelectionItem> items) {
        mResultCode = resultCode;
        mItems = items;
    }

    public BaseSelectionResultData(int resultCode, int requestCode, @Nullable List<MultiSelectionItem> items) {
        mResultCode = resultCode;
        mRequestCode = requestCode;
        mItems = items;
    }

    /**
     * @SelfDocumented
     */
    public int getResultCode() {
        return mResultCode;
    }

    /**
     * @SelfDocumented
     */
    public int getRequestCode() {
        return mRequestCode;
    }

    /**
     * @SelfDocumented
     */
    public boolean isCanceled() {
        return mResultCode == RESULT_CANCELED;
    }

    /**
     * @SelfDocumented
     */
    public boolean isCleared() {
        return mResultCode == RESULT_CLEARED;
    }

    /**
     * @SelfDocumented
     */
    @SuppressWarnings("SpellCheckingInspection")
    public boolean isSuccess() {
        return mResultCode == RESULT_SUCCESS;
    }

    /**
     * @return full selected items list
     */
    @NonNull
    public List<MultiSelectionItem> getFullList() {
        return mItems == null ? new ArrayList<>() : mItems;
    }

    /**
     * @SelfDocumented
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public void setResultList(@Nullable List<MultiSelectionItem> resultItems) {
        mItems = resultItems;
    }

}
