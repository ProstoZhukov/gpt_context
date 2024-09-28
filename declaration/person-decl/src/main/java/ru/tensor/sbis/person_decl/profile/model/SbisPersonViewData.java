package ru.tensor.sbis.person_decl.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Subbotenko Dmitry
 * @deprecated должен быть удален вместе с устаревшим SbisTitleView
 */
@Deprecated
public final class SbisPersonViewData implements Parcelable {

    @Deprecated
    public static final int WITHOUT_ACTIVITY_STATUS = -1;
    @Deprecated
    public static final int ONLINE_WORK = 0;
    @Deprecated
    public static final int OFFLINE_WORK = 1;
    @Deprecated
    public static final int ONLINE_HOME = 2;
    @Deprecated
    public static final int OFFLINE_HOME = 3;

    public static final int UNUSED_OLD_ACTIVITY_STATUS_VALUE = -2;

    public static final Creator<SbisPersonViewData> CREATOR = new Creator<SbisPersonViewData>() {
        @NonNull
        @Override
        public SbisPersonViewData createFromParcel(@NonNull Parcel in) {
            return new SbisPersonViewData(in);
        }

        @NonNull
        @Override
        public SbisPersonViewData[] newArray(int size) {
            return new SbisPersonViewData[size];
        }
    };

    public UUID personUuid;
    @Nullable
    public String photoUrl;
    /**
     * Используется для вывода drawable в случае отсутствия {@link #photoUrl}
     */
    @DrawableRes
    public int photoResId;
    @Deprecated
    public int oldActivityStatus = UNUSED_OLD_ACTIVITY_STATUS_VALUE;
    @Nullable
    public ActivityStatus activityStatus;
    @Nullable
    public SbisPersonViewInitialsStubData initialsStubData;
    @NonNull
    public SbisPersonViewPhotoDataType photoDataType = SbisPersonViewPhotoDataType.PERSON;

    public SbisPersonViewData() {

    }

    public SbisPersonViewData(@Nullable UUID personUuid,
                              @Nullable String photoUrl,
                              int oldActivityStatus) {
        this.personUuid = personUuid;
        this.photoUrl = photoUrl;
        this.oldActivityStatus = oldActivityStatus;
    }

    public SbisPersonViewData(@Nullable UUID personUuid,
                              @Nullable String photoUrl,
                              @Nullable ActivityStatus activityStatus,
                              @Nullable SbisPersonViewInitialsStubData initialsStubData) {
        this.personUuid = personUuid;
        this.photoUrl = photoUrl;
        this.activityStatus = activityStatus;
        this.oldActivityStatus = UNUSED_OLD_ACTIVITY_STATUS_VALUE;
        this.initialsStubData = initialsStubData;
    }

    public SbisPersonViewData(@Nullable UUID personUuid,
                              @Nullable String photoUrl,
                              @Nullable ActivityStatus activityStatus) {
        this(personUuid, photoUrl, activityStatus, null);
    }

    public SbisPersonViewData(String photoUrl) {
        this(null, photoUrl, 0);
    }

    public SbisPersonViewData(@Nullable UUID personUuid,
                              @Nullable String photoUrl) {
        this(personUuid, photoUrl, null);
    }

    @NonNull
    public static SbisPersonViewData createCompanyData(@Nullable UUID companyUuid,
                                                       @Nullable String photoUrl) {
        SbisPersonViewData data = new SbisPersonViewData(companyUuid, photoUrl, null);
        data.photoDataType = SbisPersonViewPhotoDataType.COMPANY;
        return data;
    }

    @NonNull
    public static SbisPersonViewData createDepartmentData(@Nullable UUID departmentUuid,
                                                          @Nullable String photoUrl) {
        SbisPersonViewData data = new SbisPersonViewData(departmentUuid, photoUrl, null);
        data.photoDataType = SbisPersonViewPhotoDataType.DEPARTMENT;
        return data;
    }

    @NonNull
    public static SbisPersonViewData createGroupData(@Nullable UUID groupUuid,
                                                     @Nullable String photoUrl) {
        SbisPersonViewData data = new SbisPersonViewData(groupUuid, photoUrl, null);
        data.photoDataType = SbisPersonViewPhotoDataType.GROUP;
        return data;
    }

    protected SbisPersonViewData(@NonNull Parcel in) {
        this((UUID) in.readSerializable(), in.readString(), in.readInt());
        final String activityStatusFromParcel = in.readString();
        activityStatus = activityStatusFromParcel == null ? null : ActivityStatus.valueOf(activityStatusFromParcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest,
                              int flags) {
        dest.writeSerializable(personUuid);
        dest.writeString(photoUrl);
        dest.writeInt(oldActivityStatus);
        dest.writeString(activityStatus != null ? activityStatus.name() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SbisPersonViewData that = (SbisPersonViewData) o;
        return photoResId == that.photoResId &&
                oldActivityStatus == that.oldActivityStatus &&
                Objects.equals(personUuid, that.personUuid) &&
                Objects.equals(photoUrl, that.photoUrl) &&
                activityStatus == that.activityStatus &&
                Objects.equals(initialsStubData, that.initialsStubData) &&
                photoDataType == that.photoDataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personUuid, photoUrl, photoResId, oldActivityStatus, activityStatus,
                initialsStubData, photoDataType);
    }

    @Deprecated
    @IntDef({WITHOUT_ACTIVITY_STATUS, ONLINE_WORK, OFFLINE_WORK, ONLINE_HOME, OFFLINE_HOME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PersonActivityStatus {
    }

}
