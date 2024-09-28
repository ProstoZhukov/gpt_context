package ru.tensor.sbis.base_components.fragment.dialog;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import ru.tensor.sbis.base_components.R;

/** Legacy-код */
public enum FolderContextMenuOperations implements Parcelable {
    MOVE_UP(1, R.string.base_components_common_folder_context_menu_move_up),
    MOVE_DOWN(2, R.string.base_components_common_folder_context_menu_move_down),
    EDIT(3, R.string.base_components_common_folder_context_menu_move_edit),
    REMOVE(4, R.string.base_components_common_folder_context_menu_move_remove),
    MOVE(5, R.string.base_components_common_folder_context_menu_move);

    private final int mId;
    private final int mLabelRes;

    FolderContextMenuOperations(int id, int label) {
        mId = id;
        mLabelRes = label;
    }

    public static final Creator<FolderContextMenuOperations> CREATOR =
            new Creator<FolderContextMenuOperations>() {
                public FolderContextMenuOperations createFromParcel(@NonNull Parcel in) {
                    return FolderContextMenuOperations.values()[in.readInt()];
                }

                @NonNull
                public FolderContextMenuOperations[] newArray(int size) {
                    return new FolderContextMenuOperations[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mLabelRes);
    }

    @SuppressWarnings("unused")
    public int getId() {
        return mId;
    }

    @SuppressWarnings("unused")
    public int getLabelRes() {
        return mLabelRes;
    }
}
