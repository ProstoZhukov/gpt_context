package ru.tensor.sbis.recipient_selection.profile.data.group_profiles;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.tensor.sbis.persons.GroupContactVM;
import ru.tensor.sbis.communication_decl.model.FolderType;
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.recipient_selection.R;

/**
 * Класс - элемент списка выбора получателей - группа получателей
 */
public class GroupItem implements MultiSelectionItem {

    public static final String GROUP_TYPE = "multi_selection_item.mGroup";

    private GroupContactVM mGroup;

    private boolean mIsChecked;

    /** @SelfDocumented */
    public GroupItem(GroupContactVM group) {
        this.mGroup = group;
    }

    /** @SelfDocumented */
    public GroupContactVM getGroup() {
        return mGroup;
    }

    /** @SelfDocumented */
    public boolean isTaskType() {
        return mGroup.getFolderType() == FolderType.TASK_EXECUTOR;
    }

    /** @SelfDocumented */
    @Override
    public int getItemCount() {
        return mGroup.getItemCount();
    }

    /** @SelfDocumented */
    @Override
    public UUID getUUID() {
        return mGroup.getUUID();
    }

    /** @SelfDocumented */
    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    /** @SelfDocumented */
    @Override
    public void setIsChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    /**
     * Получение класса ViewHolder-a, к которому привязана данная группа.
     */
    @NonNull
    @Override
    public Class<? extends MultiSelectionViewHolder> getViewHolderClass() {
        return RecipientGroupViewHolder.class;
    }

    /** @SelfDocumented */
    @Override
    public int getHolderLayoutResId() {
        return R.layout.recipient_selection_item_list_group_contact_recipient;
    }

    /** @SelfDocumented */
    @Nullable
    @Override
    public String getItemType() {
        return GROUP_TYPE;
    }

    /** @SelfDocumented */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupItem groupItem = (GroupItem) o;

        return mGroup.equals(groupItem.mGroup);
    }

    /** @SelfDocumented */
    @Override
    public int hashCode() {
        return mGroup.hashCode();
    }

}
