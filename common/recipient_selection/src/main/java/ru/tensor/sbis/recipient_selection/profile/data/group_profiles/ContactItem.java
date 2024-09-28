package ru.tensor.sbis.recipient_selection.profile.data.group_profiles;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.recipient_selection.R;

/**
 * Класс - элемент списка выбора получателей - один контакт-получатель
 */
public class ContactItem implements MultiSelectionItem {

    public static final String CONTACT_TYPE = "multi_selection_item.contact";

    private ContactVM contact;

    private boolean mIsChecked;

    /** @SelfDocumented */
    public ContactItem(ContactVM contact) {
        this.contact = contact;
    }

    /** @SelfDocumented */
    public ContactVM getContact() {
        return contact;
    }

    /** @SelfDocumented */
    @Override
    public int getItemCount() {
        return 1;
    }

    /** @SelfDocumented */
    @Override
    public UUID getUUID() {
        return contact.getUUID();
    }

    /** @SelfDocumented */
    @Override
    public void setIsChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    /** @SelfDocumented */
    public boolean isChecked() {
        return mIsChecked;
    }

    /**
     * Получение класса ViewHolder-a, к которому привязан данный контакт.
     * */
    @NonNull
    @Override
    public Class<? extends MultiSelectionViewHolder> getViewHolderClass() {
        return RecipientContactViewHolder.class;
    }

    /** @SelfDocumented */
    @Override
    public int getHolderLayoutResId() {
        return R.layout.recipient_selection_item_list_contact_recipient;
    }

    /** @SelfDocumented */
    @Nullable
    @Override
    public String getItemType() {
        return ContactItem.CONTACT_TYPE;
    }

    /** @SelfDocumented */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactItem that = (ContactItem) o;

        return contact.equals(that.contact);
    }

    /** @SelfDocumented */
    @Override
    public int hashCode() {
        return contact.hashCode();
    }

}
