package ru.tensor.sbis.modalwindows.bottomsheet;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Базовая модель опции для нижней панели.
 *
 * @author sr.golovkin
 */
public class BottomSheetOption implements Parcelable {

    public static final int COLOR_UNDEFINED = -1;

    /**
     * Отображаемое название опции.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    @Nullable
    private String mName;

    /**
     * Целочисленное значение (идентификатор) опции.
     */
    private int mOptionValue;

    /**
     * Иконка опции.
     */
    @Nullable
    private String mIcon;
    /**
     * Цвет иконки.
     */
    private int mIconColor = COLOR_UNDEFINED;

    public BottomSheetOption() {

    }

    protected BottomSheetOption(Parcel in) {
        mName = in.readString();
        mOptionValue = in.readInt();
        mIcon = in.readString();
        mIconColor = in.readInt();
    }

    public static final Creator<BottomSheetOption> CREATOR = new Creator<BottomSheetOption>() {
        @Override
        public BottomSheetOption createFromParcel(Parcel in) {
            return new BottomSheetOption(in);
        }

        @Override
        public BottomSheetOption[] newArray(int size) {
            return new BottomSheetOption[size];
        }
    };

    @SuppressWarnings("unused")
    public static BottomSheetOption createSimpleOption(@NonNull Context context, @StringRes int stringResource, int optionType) {
        BottomSheetOption option = new BottomSheetOption();
        option.mName = context.getString(stringResource);
        option.mOptionValue = optionType;
        return option;
    }

    public static BottomSheetOption createSimpleOption(@NonNull String name, int optionType) {
        BottomSheetOption option = new BottomSheetOption();
        option.mName = name;
        option.mOptionValue = optionType;
        return option;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public void setName(@Nullable String name) {
        mName = name;
    }

    public int getOptionValue() {
        return mOptionValue;
    }

    public void setOptionValue(int optionValue) {
        mOptionValue = optionValue;
    }

    @Nullable
    public String getIcon() {
        return mIcon;
    }

    public void setIcon(@Nullable String icon) {
        mIcon = icon;
    }

    public int getIconColor() {
        return mIconColor;
    }

    public void setIconColor(int color) {
        mIconColor = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mOptionValue);
        dest.writeString(mIcon);
        dest.writeInt(mIconColor);
    }
}
