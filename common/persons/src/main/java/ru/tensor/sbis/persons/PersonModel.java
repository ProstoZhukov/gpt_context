package ru.tensor.sbis.persons;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.UUID;

import ru.tensor.sbis.common.util.UrlUtils;
import ru.tensor.sbis.persons.util.PersonFormatUtils;
import ru.tensor.sbis.persons.util.PersonNameTemplate;
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus;

/**
 * Модель данных, общих для работы с персоной на любом экране.
 * @author aa.mironychev
 */
public class PersonModel implements IPersonModel, Parcelable {

    /**
     * Идентификатор пользователя.
     */
    protected UUID mUuid;

    /**
     * "Сырая" ссылка на аватарку пользователя.
     */
    protected String mRawPhoto;

    /**
     * Модель имени пользователя.
     */
    protected PersonName mName;

    /**
     * Позиции для выделения имени при поиске.
     */
    protected List<Integer> mNameHighlight;

    /**
     * Статус активности пользователя.
     */
    protected ActivityStatus mActivityStatus;

    public PersonModel() {}

    // region getters

    /**
     * Возвращает идентификатор пользователя.
     * @return идентификатор пользователя
     */
    public UUID getUUID() {
        return mUuid;
    }

    /**
     * Возвращает "сырую" ссылку на аватарку пользователя.
     * @return "сырая" ссылка
     */
    public String getRawPhoto() {
        return mRawPhoto;
    }

    /**
     * Возвращает подготовленную ссылку на аватарку пользователя.
     * @param width     - ширина аватарки
     * @param height    - высота аватарки
     * @return подготовленная ссылка на аватарку пользователя
     */
    @SuppressWarnings("deprecation")
    public String getPreparedPhoto(int width, int height) {
        if (mRawPhoto != null) {
            return UrlUtils.insertSizeInImageUrlPlaceholders(mRawPhoto, width, height);
        }
        return null;
    }

    /**
     * Возвращает модель имени пользователя.
     * @return модель имени пользователя
     */
    public PersonName getName() {
        return mName;
    }

    /**
     * Возвращает "склеенное" по указанному шаблону имя пользователя.
     * @param template - шаблон имени
     * @return имя пользователя
     */
    @SuppressWarnings("deprecation")
    public String getRenderedName(@NonNull PersonNameTemplate template) {
        if (mName != null) {
            return PersonFormatUtils.formatName(mName, template);
        }
        return null;
    }

    /**
     * Возвращает "склеенное" имя пользователя.
     * @return имя пользователя
     */
    @SuppressWarnings("deprecation")
    public String getRenderedName() {
        return getRenderedName(PersonNameTemplate.SURNAME_NAME);
    }

    /**
     * Возвращает статус активности пользователя.
     * @return статус активности пользователя
     */
    public ActivityStatus getActivityStatus() {
        return mActivityStatus;
    }

    /**
     * @return список позиций для выделения имени при поиске.
     */
    public List<Integer> getNameHighlight() {
        return mNameHighlight;
    }
    // endregion getters

    // region setters

    /**
     * Задает идентификатор пользователя.
     * @param uuid - идентификатор
     */
    public void setUUID(UUID uuid) {
        this.mUuid = uuid;
    }

    /**
     * Задает "сырую" ссылку на аватарку пользователя.
     * @param photoUrl - "сырая" ссылка на аватарку пользователя
     */
    public void setRawPhoto(String photoUrl) {
        this.mRawPhoto = photoUrl;
    }

    /**
     * Задает модель имени пользователя.
     * @param name - модель имени пользователя
     */
    public void setName(PersonName name) {
        this.mName = name;
    }

    /**
     * Задает статус активности пользователя.
     * @param activityStatus - статус активности
     */
    public void setActivityStatus(ActivityStatus activityStatus) {
        this.mActivityStatus = activityStatus;
    }

    @Override
    public void setNameHighlight(List<Integer> highlights) {
        this.mNameHighlight = highlights;
    }

    // endregion setters

    // region Parcelable
    protected PersonModel(Parcel in) {
        mUuid = (UUID) in.readSerializable();
        mRawPhoto = in.readString();
        mName = in.readParcelable(PersonName.class.getClassLoader());
        mActivityStatus = readActivityStatus(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mUuid);
        dest.writeString(mRawPhoto);
        dest.writeParcelable(mName, 0);
        dest.writeString(mActivityStatus != null ? mActivityStatus.name() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PersonModel> CREATOR = new Creator<PersonModel>() {
        @Override
        public PersonModel createFromParcel(Parcel in) {
            return new PersonModel(in);
        }

        @Override
        public PersonModel[] newArray(int size) {
            return new PersonModel[size];
        }
    };

    // region Parcel-Utils
    protected ActivityStatus readActivityStatus(Parcel in) {
        String status = in.readString();
        return status != null ? ActivityStatus.valueOf(status) : null;
    }
    // endregion
    // endregion

    // region Comparing
    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && o.getClass() == getClass() && mUuid != null && mUuid.equals(((PersonModel) o).mUuid));

    }

    @Override
    public int hashCode() {
        return mUuid != null ? mUuid.hashCode() : 0;
    }
    // endregion
}
