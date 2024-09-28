package ru.tensor.sbis.modalwindows.bottomsheet.check;

import android.os.Parcel;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import ru.tensor.sbis.modalwindows.BR;
import ru.tensor.sbis.modalwindows.bottomsheet.binding.UniversalBottomSheetOption;

/**
 * SelfDocumented
 * @author sr.golovkin
 */
@SuppressWarnings("unused")
public class CheckableBottomSheetOption extends UniversalBottomSheetOption implements OptionCheckProvider {

    /** SelfDocumented */
    public static final int VIEW_TYPE = 0;

    private final int mViewType;
    private boolean mChecked;

    public CheckableBottomSheetOption(int viewType) {
        mViewType = viewType;
    }

    public CheckableBottomSheetOption() {
        mViewType = VIEW_TYPE;
    }

    @Override
    protected int getViewType() {
        return mViewType;
    }

    @NonNull
    @Override
    protected SparseArray<Object> createBindingVariables() {
        final SparseArray<Object> variables = new SparseArray<>(1);
        variables.put(BR.CheckableOption, this);
        return variables;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mChecked ? 1 : 0);
        dest.writeInt(mViewType);
    }

    protected CheckableBottomSheetOption(Parcel in) {
        super(in);
        mChecked = in.readInt() == 1;
        mViewType = in.readInt();
    }

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
