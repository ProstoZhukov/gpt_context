package ru.tensor.sbis.persons;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** @SelfDocumented */
public final class PersonName implements ru.tensor.sbis.person_decl.profile.model.PersonName {

    @NonNull
    private final String mFirstName;
    @NonNull
    private final String mLastLame;
    @NonNull
    private final String mPatronymicName;

    public PersonName(@NonNull String firstName, @NonNull String lastLame, @NonNull String patronymicName) {
        mFirstName = firstName.trim();
        mLastLame = lastLame.trim();
        mPatronymicName = patronymicName.trim();
    }

    public PersonName() {
        mFirstName = "";
        mLastLame = "";
        mPatronymicName = "";
    }

    @Override
    public boolean isEmpty() {
        return TextUtils.isEmpty(mFirstName) && TextUtils.isEmpty(mLastLame) && TextUtils.isEmpty(mPatronymicName);
    }

    @NonNull
    @Override
    public String getFirstName() {
        return mFirstName;
    }

    @NonNull
    @Override
    public String getLastName() {
        return mLastLame;
    }

    @NonNull
    @Override
    public String getPatronymicName() {
        return mPatronymicName;
    }

    @NonNull
    @Override
    public String getFullName() {
        return (mLastLame +
                " " +
                mFirstName +
                " " +
                mPatronymicName)
                .trim();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mFirstName)
                .append(mLastLame)
                .append(mPatronymicName)
                .toHashCode();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass())  {
            return false;
        }

        PersonName taskFolderModel = (PersonName) o;
        return new EqualsBuilder()
                .append(mFirstName, taskFolderModel.mFirstName)
                .append(mLastLame, taskFolderModel.mLastLame)
                .append(mPatronymicName, taskFolderModel.mPatronymicName)
                .isEquals();
    }

    public PersonName(@NonNull Parcel in) {
        mFirstName = in.readString();
        mLastLame = in.readString();
        mPatronymicName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastLame);
        dest.writeString(mPatronymicName);
    }

    @Override
    public String toString() {
        return "PersonName{" +
                "mFirstName='" + mFirstName + '\'' +
                ", mLastLame='" + mLastLame + '\'' +
                ", mPatronymicName='" + mPatronymicName + '\'' +
                '}';
    }

    public static final Creator<PersonName> CREATOR = new Creator<PersonName>() {
        @NonNull
        @Override
        public PersonName createFromParcel(@NonNull Parcel in) {
            return new PersonName(in);
        }

        @NonNull
        @Override
        public PersonName[] newArray(int size) {
            return new PersonName[size];
        }
    };
}
