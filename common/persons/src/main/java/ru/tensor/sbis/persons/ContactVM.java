package ru.tensor.sbis.persons;

import android.os.Parcel;

import org.jetbrains.annotations.NotNull;

import ru.tensor.sbis.design.profile_decl.person.InitialsStubData;
import ru.tensor.sbis.person_decl.profile.model.Gender;
import ru.tensor.sbis.persons.recipientselection.RecipientSelectionItem;

/**
 * Класс для мапинга вью-моделей контактов.
 * @author aa.mironychev
 */
public class ContactVM extends PersonModel implements IContactVM, RecipientSelectionItem, ConversationRegistryItem {

    private String mData1;
    private String mData2;
    private boolean mWithSubAttribute;
    private Gender mGender;
    private boolean mHasAccess;
    private InitialsStubData mInitialsStubData;

    // region getters
    public String getData1() {
        return mData1;
    }

    @NotNull
    @Override
    public String toString() {
        return "ContactVM{" +
                "mData1='" + mData1 + '\'' +
                ", mData2='" + mData2 + '\'' +
                ", mWithSubAttribute=" + mWithSubAttribute +
                ", mGender=" + mGender +
                ", mHasAccess=" + mHasAccess +
                ", mUuid=" + mUuid +
                ", mRawPhoto='" + mRawPhoto + '\'' +
                ", mName=" + mName +
                ", mNameHighlight=" + mNameHighlight +
                ", mActivityStatus=" + mActivityStatus +
                '}';
    }

    public String getData2() {
        return mData2;
    }

    public boolean isWithSubAttribute() {
        return mWithSubAttribute;
    }

    public Gender getGender() {
        return mGender;
    }

    public boolean isHasAccess() {
        return mHasAccess;
    }

    @Override
    public InitialsStubData getInitialsStubData() {
        return mInitialsStubData;
    }

    // endregion getters

    // region setters
    public void setData1(String data1) {
        mData1 = data1;
    }

    public void setData2(String data2) {
        mData2 = data2;
    }

    public void setWithSubAttribute(boolean withSubAttribute) {
        mWithSubAttribute = withSubAttribute;
    }

    public void setGender(Gender gender) {  mGender = gender; }

    public void setHasAccess(boolean hasAccess) {
        this.mHasAccess = hasAccess;
    }

    @Override
    public void setInitialsStubData(InitialsStubData initialsStubData) {
        mInitialsStubData = initialsStubData;
    }

    // endregion setters

    public ContactVM() {
    }

    // region Parcel
    public ContactVM(Parcel in) {
        super(in);
        mData1 = in.readString();
        mData2 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mData1);
        dest.writeString(mData2);
    }

    public static final Creator<ContactVM> CREATOR = new Creator<ContactVM>() {
        @Override
        public ContactVM createFromParcel(Parcel in) {
            return new ContactVM(in);
        }

        @Override
        public ContactVM[] newArray(int size) {
            return new ContactVM[size];
        }
    };
    // endregion

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ContactVM contactVM = (ContactVM) o;

        return mUuid.equals(contactVM.getUUID());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mUuid != null ? mUuid.hashCode() : 0);
        return result;
    }
}
