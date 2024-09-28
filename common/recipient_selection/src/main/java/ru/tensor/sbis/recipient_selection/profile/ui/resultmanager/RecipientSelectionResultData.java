package ru.tensor.sbis.recipient_selection.profile.ui.resultmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import ru.tensor.sbis.communication_decl.employeeselection.result.RecipientSelectionForRepostItemType;
import ru.tensor.sbis.communication_decl.model.FolderType;
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionResultDataContract;
import ru.tensor.sbis.mvp.multiselection.data.BaseSelectionResultData;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem;
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem;
import ru.tensor.sbis.recipient_selection.profile.data.repost.RecipientSelectionResultDataForRepost;

/**
 * Класс, описывающий результат совершения выбора получателей сообщения
 */
public class RecipientSelectionResultData extends BaseSelectionResultData implements RecipientSelectionResultDataContract {

    /** @SelfDocumented */
    public RecipientSelectionResultData() {
    }

    /** @SelfDocumented */
    public RecipientSelectionResultData(@Nullable List<MultiSelectionItem> items) {
        super(items);
    }

    /** @SelfDocumented */
    public RecipientSelectionResultData(int resultCode, @Nullable List<MultiSelectionItem> items) {
        super(resultCode, items);
    }

    /** @SelfDocumented */
    public RecipientSelectionResultData(int resultCode, int requestCode, @Nullable List<MultiSelectionItem> items) {
        super(resultCode, requestCode, items);
    }

    /**
     * @return list of all contact uuds: single contact uuids + extracted contact uuids from folders
     */
    @NonNull
    public List<UUID> getAllContactUuids() {
        Collection<UUID> uuids = new LinkedHashSet<>();
        if (mItems != null) {
            for (MultiSelectionItem item : mItems) {
                if (ContactItem.CONTACT_TYPE.equals(item.getItemType())) {
                    uuids.add(item.getUUID());
                } else if (GroupItem.GROUP_TYPE.equals(item.getItemType())) {
                    List<UUID> personUuids = ((GroupItem) item).getGroup().getPersonUuids();
                    if (personUuids != null) {
                        uuids.addAll(personUuids);
                    }
                }
            }
        }
        return new ArrayList<>(uuids);
    }

    /**
     * @return list of all contact models: single contacts + extracted contacts from folders
     */
    @NonNull
    public List<ContactVM> getAllContacts() {
        Collection<ContactVM> contacts = new LinkedHashSet<>();
        if (mItems != null) {
            for (MultiSelectionItem item : mItems) {
                if (ContactItem.CONTACT_TYPE.equals(item.getItemType())) {
                    contacts.add(((ContactItem) item).getContact());
                } else if (GroupItem.GROUP_TYPE.equals(item.getItemType())) {
                    List<ContactVM> groupPersons = ((GroupItem) item).getGroup().getPersons();
                    if (groupPersons != null) {
                        contacts.addAll(groupPersons);
                    }
                }
            }
        }
        return new ArrayList<>(contacts);
    }

    /**
     * @return список моделей выбранных контактов для репоста
     */
    @NonNull
    public List<RecipientSelectionResultDataForRepost> getContactsDataForRepost() {
        List<RecipientSelectionResultDataForRepost> result = new ArrayList<>();

        if (mItems != null) {
            for (MultiSelectionItem recipientItem : mItems) {
                RecipientSelectionResultDataForRepost itemModel = null;
                if (ContactItem.CONTACT_TYPE.equals(recipientItem.getItemType())) {
                    ContactItem contactItem = (ContactItem) recipientItem;
                    itemModel = new RecipientSelectionResultDataForRepost(
                            contactItem.getUUID(),
                            contactItem.getContact().getRenderedName(),
                            contactItem.getContact().getData1(),
                            contactItem.getContact().getRawPhoto(),
                            false,
                            contactItem.getItemCount(),
                            RecipientSelectionForRepostItemType.PERSON
                    );
                } else if (GroupItem.GROUP_TYPE.equals(recipientItem.getItemType())) {
                    GroupItem groupItem = (GroupItem) recipientItem;
                    itemModel = new RecipientSelectionResultDataForRepost(
                            groupItem.getUUID(),
                            groupItem.getGroup().getGroupName(),
                            groupItem.getGroup().getGroupChiefName(),
                            null,
                            true,
                            groupItem.getItemCount(),
                            RecipientSelectionForRepostItemType.FOLDER
                    );
                }

                if (itemModel != null) result.add(itemModel);
            }
        }

        return result;
    }

    /**
     * @return only single contact uuids list
     */
    @Override
    @NonNull
    public List<UUID> getContactUuids() {
        List<UUID> contactUuids = new ArrayList<>();
        if (mItems != null) {
            for (MultiSelectionItem item : mItems) {
                if (ContactItem.CONTACT_TYPE.equals(item.getItemType())) {
                    contactUuids.add(item.getUUID());
                }
            }
        }
        return contactUuids;
    }

    /**
     * @return only group uuids list
     */
    public List<UUID> getGroupUuids() {
        List<UUID> groupUuids = new ArrayList<>();
        if (mItems != null) {
            for (MultiSelectionItem item : mItems) {
                if (GroupItem.GROUP_TYPE.equals(item.getItemType())) {
                    groupUuids.add(item.getUUID());
                }
            }
        }
        return groupUuids;
    }

    /**
     * @param excludedFolderType folder type which should be excluded
     * @return list of single contacts and filtered groups excluding the specified folder type
     */

    @NonNull
    public List<MultiSelectionItem> getRecipientsWithFilteredGroups(@NonNull FolderType excludedFolderType) {
        List<MultiSelectionItem> result = mItems == null ? new ArrayList<>() : mItems;
        for (MultiSelectionItem item : mItems) {
            if (GroupItem.GROUP_TYPE.equals(item.getItemType())) {
                if (((GroupItem) item).getGroup().getFolderType() == excludedFolderType) {
                    result.remove(item);
                }
            }
        }
        return result;
    }

    /**
     * Заполнение списка - результата выбора, установление флага успешного выполнения.
     */
    public void setContactResultList(@Nullable List<ContactVM> contactResultList) {
        mResultCode = RESULT_SUCCESS;
        if (contactResultList != null) {
            for (ContactVM contact : contactResultList) {
                if (mItems == null) {
                    mItems = new ArrayList<>();
                }
                mItems.add(new ContactItem(contact));
            }
        }
    }

}
