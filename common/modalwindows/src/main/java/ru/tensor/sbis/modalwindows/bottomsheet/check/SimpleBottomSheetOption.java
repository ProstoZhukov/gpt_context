package ru.tensor.sbis.modalwindows.bottomsheet.check;

import android.os.Parcel;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import ru.tensor.sbis.modalwindows.bottomsheet.binding.UniversalBottomSheetOption;

/**
 * SelfDocumented
 * @author sr.golovkin
 */
public class SimpleBottomSheetOption extends UniversalBottomSheetOption {

    /** SelfDocumented */
    public static final int VIEW_TYPE = 0;

    private final int mViewType;

    public SimpleBottomSheetOption(int viewType) {
        mViewType = viewType;
    }

    public SimpleBottomSheetOption() {
        mViewType = VIEW_TYPE;
    }

    @Override
    protected int getViewType() {
        return mViewType;
    }

    @NonNull
    @Override
    protected SparseArray<Object> createBindingVariables() {
        return new SparseArray<>(0);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mViewType);
    }

    protected SimpleBottomSheetOption(Parcel in) {
        super(in);
        mViewType = in.readInt();
    }

    /** SelfDocumented */
    public static final Creator<CheckableBottomSheetOption> CREATOR = new Creator<CheckableBottomSheetOption>() {
        @Override
        public CheckableBottomSheetOption createFromParcel(Parcel in) {
            return new CheckableBottomSheetOption(in);
        }

        @Override
        public CheckableBottomSheetOption[] newArray(int size) {
            return new CheckableBottomSheetOption[size];
        }
    };
}
