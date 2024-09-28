package ru.tensor.sbis.recipient_selection.profile.data.group_profiles;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import kotlin.Unit;
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardIntentFactory;
import ru.tensor.sbis.design.profile.person.PersonView;
import ru.tensor.sbis.design.profile_decl.person.PersonData;
import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder;
import ru.tensor.sbis.design.utils.DoubleClickPreventerKt;
import ru.tensor.sbis.recipient_selection.R;
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionComponentProvider;

/**
 * Холдер для отображения ContactItem
 */
public class RecipientContactViewHolder extends MultiSelectionViewHolder<ContactItem> {

    protected PersonView mPersonPhotoView;
    private TextView mContactTitle;
    private TextView mContactSubtitle;

    /** @SelfDocumented */
    public RecipientContactViewHolder(View itemView, boolean isSingleChoice) {
        super(itemView, isSingleChoice);
    }

    /** @SelfDocumented */
    @Override
    protected void initViews() {
        mCheckBox = itemView.findViewById(R.id.checkbox);
        mCheckBoxContainer = itemView.findViewById(R.id.checkbox_container);
        mSeparatorView = itemView.findViewById(R.id.recipient_item_separator);
        mPersonPhotoView = itemView.findViewById(R.id.person_photo);
        mContactTitle = itemView.findViewById(R.id.contact_title);
        mContactSubtitle = itemView.findViewById(R.id.contact_subtitle);

        itemView.setOnLongClickListener(view -> onItemViewLongClick(view.getContext()));

        mPersonPhotoView.setOnClickListener(DoubleClickPreventerKt.preventViewFromDoubleClickWithDelay(1000L, view -> {
            onPersonPhotoClick(view.getContext());
            return Unit.INSTANCE;
        }));
        mPersonPhotoView.setOnLongClickListener(view -> onPersonPhotoLongClick(view.getContext()));
    }

    /** @SelfDocumented */
    @Override
    public void bind(ContactItem dataModel) {
        super.bind(dataModel);
        ContactVM contact = dataModel.getContact();
        mPersonPhotoView.setData(createViewData(contact));
        mContactTitle.setText(contact.getRenderedName());
        mContactSubtitle.setText(contact.getData1());
        mContactSubtitle.setVisibility(TextUtils.isEmpty(contact.getData1()) ? View.INVISIBLE : View.VISIBLE);
        mPersonPhotoView.setHasActivityStatus(true, false);
    }

    private PersonData createViewData(@NonNull ContactVM contact) {
        return new PersonData(
            contact.getUUID(),
            contact.getRawPhoto(),
            contact.getInitialsStubData()
        );
    }

    private boolean onItemViewLongClick(@NonNull Context context) {
        showProfile(context);
        return true;
    }

    private void onPersonPhotoClick(@NonNull Context context) {
        showProfile(context);
    }

    private boolean onPersonPhotoLongClick(@NonNull Context context) {
        showProfile(context);
        return true;
    }

    private void showProfile(@NonNull Context context) {
        @Nullable Intent intent = getProfileIntent(context);
        if (intent != null) {
            if (context == context.getApplicationContext()) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    @Nullable
    private Intent getProfileIntent(@NonNull Context context) {
        PersonCardIntentFactory intentFactory = RecipientSelectionComponentProvider
                .getRecipientSelectionSingletonComponent(context)
                .getDependency()
                .getPersonCardIntentFactory();
        if (intentFactory != null) {
            return intentFactory.createPersonCardIntent(context, mItem.getUUID());
        } else {
            return null;
        }
    }

}
