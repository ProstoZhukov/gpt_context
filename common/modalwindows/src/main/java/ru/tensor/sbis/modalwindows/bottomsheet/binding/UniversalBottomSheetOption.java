package ru.tensor.sbis.modalwindows.bottomsheet.binding;

import android.os.Parcel;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.modalwindows.bottomsheet.BottomSheetOption;

/**
 * Базовая модель опции с использованием биндинга.
 *
 * @author sr.golovkin
 */
public abstract class UniversalBottomSheetOption extends BottomSheetOption {

    /**
     * Пемеренные для биндинга.
     */
    @Nullable
    private SparseArray<Object> mBindingVariables;

    /**
     * Возвращает список (id перменной - переменная) связываемых с помощью data binding переменных.
     * Прокидывается в разметку через view holder
     *
     * @return SparseArray, где ключ - идентификатор BR переменной, а значение - объект переменной
     */
    @NonNull
    public SparseArray<Object> getBindingVariables() {
        if (mBindingVariables == null) {
            mBindingVariables = createBindingVariables();
        }
        return mBindingVariables;
    }

    /**
     * Получить уникальный для данного типа элементов идентификатор view type.
     * @return view type
     */
    protected abstract int getViewType();

    /** SelfDocumented */
    @NonNull
    protected abstract SparseArray<Object> createBindingVariables();

    public UniversalBottomSheetOption() {

    }

    public UniversalBottomSheetOption(Parcel in) {
        super(in);
    }
}
