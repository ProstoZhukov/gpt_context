package ru.tensor.sbis.recipient_selection.profile.data.group_profiles;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import ru.tensor.sbis.persons.GroupContactVM;
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder;
import ru.tensor.sbis.recipient_selection.R;

/**
 * Холдер для отображения GroupItem
 */
public class RecipientGroupViewHolder extends MultiSelectionViewHolder<GroupItem> {

    private TextView mTitle;
    private TextView mSubtitle;

    /** @SelfDocumented */
    public RecipientGroupViewHolder(View itemView) {
        super(itemView);
    }

    /** @SelfDocumented */
    public RecipientGroupViewHolder(View itemView, boolean isSingleChoice) {
        super(itemView, isSingleChoice);
    }

    /** @SelfDocumented */
    @Override
    protected void initViews() {
        mCheckBox = itemView.findViewById(R.id.checkbox);
        mCheckBoxContainer = itemView.findViewById(R.id.checkbox_container);
        mSeparatorView = itemView.findViewById(R.id.recipient_item_separator);
        mTitle = itemView.findViewById(R.id.group_contact_title);
        mSubtitle = itemView.findViewById(R.id.group_contact_subtitle);
    }

    /** @SelfDocumented */
    @Override
    public void bind(GroupItem item) {
        super.bind(item);
        GroupContactVM group = item.getGroup();
        mTitle.setText(group.getGroupName());
        String subtitleText = mTitle.getContext()
                .getString(R.string.recipient_selection_contact_group_chief_and_count_format, group.getGroupChiefName(), item.getItemCount());
        mSubtitle.setText(subtitleText);
        mSubtitle.setVisibility(item.getItemCount() > 0 || !TextUtils.isEmpty(group.getGroupChiefName()) ?
                View.VISIBLE : View.GONE);

    }
}
