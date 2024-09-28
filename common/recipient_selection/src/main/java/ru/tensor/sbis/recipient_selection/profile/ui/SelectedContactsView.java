package ru.tensor.sbis.recipient_selection.profile.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.persons.GroupContactVM;
import ru.tensor.sbis.mvp_extensions.sbisview.SelectedItemsView;
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem;
import ru.tensor.sbis.design.TypefaceManager;
import ru.tensor.sbis.recipient_selection.R;
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem;
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem;

/**
 * Класс - View для отображения списка уже выбранных получателей, собранного из пользователей и папок
 */
//TODO возвращение реализации для обратной совместимости, удалить после выполнения https://online.sbis.ru/opendoc.html?guid=8192fa85-349f-4040-8d28-f850e33b898e
public class SelectedContactsView extends SelectedItemsView<SelectedContactsView.SelectedContactsViewDisplayInfo> {

    private List<GroupContactVM> mFolders = new ArrayList<>();
    private List<ContactVM> mPersons = new ArrayList<>();
    private List<Integer> mDisplayedFoldersIndexes = new ArrayList<>();
    private List<Integer> mDisplayedPersonsIndexes = new ArrayList<>();

    private Paint mPersonsPaint;
    private Paint mFoldersPaint;

    private int mItemPersonTextMarginLeft;
    private int mItemPersonTextMarginRight;
    private int mItemPersonTextMaxWidth;
    private int mItemFolderIconMarginLeft;
    private int mItemFolderIconMarginRight;
    private int mItemFolderTextMarginRight;
    private int mItemFolderTextMaxWidth;
    private int mItemFolderIconTextWidth;
    private int mItemPersonPhotoSize;

    /** @SelfDocumented */
    public SelectedContactsView(Context context) {
        super(context);
    }

    /** @SelfDocumented */
    public SelectedContactsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /** @SelfDocumented */
    public SelectedContactsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** @SelfDocumented */
    public void initUsingResources(@NonNull Context context, @Nullable AttributeSet attrs) {
        super.initUsingResources(context, attrs);
        Resources resources = context.getResources();
        Typeface font = TypefaceManager.getRobotoRegularFont(context);

        mFoldersPaint = new Paint();
        mFoldersPaint.setTypeface(font);
        mFoldersPaint.setTextSize(resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.size_body2_scaleOff));

        mPersonsPaint = new Paint();
        mPersonsPaint.setTypeface(font);
        mPersonsPaint.setTextSize(resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.size_caption1_scaleOff));

        mItemPersonTextMarginLeft = resources.getDimensionPixelSize(R.dimen.recipients_item_person_text_margin_left);
        mItemPersonTextMarginRight = resources.getDimensionPixelSize(R.dimen.recipients_item_person_text_margin_right);
        mItemPersonPhotoSize = resources.getDimensionPixelSize(R.dimen.recipients_item_person_image_size);
        mItemPersonTextMaxWidth = resources.getDimensionPixelSize(R.dimen.recipients_item_person_text_max_width);

        mItemFolderIconMarginLeft = resources.getDimensionPixelSize(R.dimen.recipients_item_folder_image_margin_left);
        mItemFolderIconMarginRight = resources.getDimensionPixelSize(R.dimen.recipients_item_folder_image_margin_right);
        mItemFolderTextMarginRight = resources.getDimensionPixelSize(R.dimen.recipients_item_folder_text_area_margin_right);
        mItemFolderTextMaxWidth = resources.getDimensionPixelSize(R.dimen.recipients_item_folder_text_max_width);
        mItemFolderIconTextWidth = resources.getDimensionPixelSize(R.dimen.recipients_item_folder_icon_size);
    }

    /** @SelfDocumented */
    @Override
    protected boolean isItemTypeCorrect(@NonNull MultiSelectionItem contactWrapper) {
        return GroupItem.GROUP_TYPE.equals(contactWrapper.getItemType()) ||
               ContactItem.CONTACT_TYPE.equals(contactWrapper.getItemType());
    }

    /** @SelfDocumented */
    @Override
    public boolean removeVerifiedItem(@NonNull MultiSelectionItem contactWrapper) {
        if (GroupItem.GROUP_TYPE.equals(contactWrapper.getItemType())) {
            return mFolders.remove(((GroupItem) contactWrapper).getGroup());
        } else {
            return mPersons.remove(((ContactItem) contactWrapper).getContact());
        }
    }

    /** @SelfDocumented */
    @Override
    protected boolean addVerifiedItem(@NonNull MultiSelectionItem contactWrapper) {
        if (GroupItem.GROUP_TYPE.equals(contactWrapper.getItemType())) {
            return mFolders.add(((GroupItem) contactWrapper).getGroup());
        } else {
            return mPersons.add(((ContactItem) contactWrapper).getContact());
        }
    }

    /** @SelfDocumented */
    @Override
    protected void clearData() {
        mFolders.clear();
        mPersons.clear();
    }

    /** @SelfDocumented */
    @Override
    protected void clearIndexes() {
        mDisplayedFoldersIndexes.clear();
        mDisplayedPersonsIndexes.clear();
    }

    /** @SelfDocumented */
    @Override
    protected boolean isEmptyContent() {
        return mPersons.size() == 0 && mFolders.size() == 0;
    }

    /**
     * Добавление всех View, ответственных за отображение того или иного пользователя или папки
     */
    @Override
    protected void displayItems(@NonNull SelectedContactsViewDisplayInfo displayInfo) {
        super.displayItems(displayInfo);
        mCounter.setText(formatCount(displayInfo.hasInvisibleItems(), displayInfo.getTotalItemsCount()));
        Iterator<Integer> foldersIndexIterator = mDisplayedFoldersIndexes.iterator();
        Iterator<Integer> personsIndexIterator = mDisplayedPersonsIndexes.iterator();
        while (foldersIndexIterator.hasNext()) {
            mItemsContainer.addView(generateFolderView(foldersIndexIterator.next()));
        }
        while (personsIndexIterator.hasNext()) {
            mItemsContainer.addView(generatePersonView(personsIndexIterator.next(), displayInfo.getWithText()));
        }
    }

    /**
     * Функция подсчета количества вью получателей для отображения,
     * получение информации об индексах каждого получателя.
     */
    @Override
    @NonNull
    protected SelectedContactsViewDisplayInfo setupItemsIndexes() {
        int contactsToDisplayCount = 0;
        int contactsContainerWidth = mItemsContainerMaxWidth;
        for (int i = 0; i < mFolders.size(); i++) {
            GroupContactVM folder = mFolders.get(i);
            boolean displayable = checkFolderDisplayable(i, mItemsFullSize - contactsToDisplayCount - folder.getItemCount() > 0, mItemsFullSize, contactsContainerWidth);
            if (displayable) {
                mDisplayedFoldersIndexes.add(i);
                contactsContainerWidth -= measureFolderItemWidth(i);
                contactsToDisplayCount += folder.getItemCount();
            } else {
                return new SelectedContactsViewDisplayInfo(mItemsFullSize, (mItemsFullSize - contactsToDisplayCount) > 0, false);
            }
        }

        boolean personViewWithText = true;
        int personContainerWidth = contactsContainerWidth;

        for (int i = 0; i < mPersons.size(); i++) {
            boolean displayable = checkPersonDisplayable(i, mItemsFullSize - contactsToDisplayCount - 1 > 0, mItemsFullSize, personContainerWidth, personViewWithText);
            if (displayable) {
                mDisplayedPersonsIndexes.add(i);
                personContainerWidth -= measurePersonItemWidth(i, personViewWithText);
                contactsToDisplayCount += 1;
            } else if (personViewWithText) {
                personViewWithText = false;
                personContainerWidth = contactsContainerWidth - (i * measurePersonItemWidth("", "", personViewWithText));
                if (!checkPersonDisplayable(i, mItemsFullSize - contactsToDisplayCount - 1 > 0, mItemsFullSize, personContainerWidth, personViewWithText)) {
                    personViewWithText = true;
                    break;
                } else {
                    mDisplayedPersonsIndexes.add(i);
                    personContainerWidth -= measurePersonItemWidth("", "", personViewWithText);
                    contactsToDisplayCount += 1;
                }
            } else {
                break;
            }
        }

        return new SelectedContactsViewDisplayInfo(mItemsFullSize, (mItemsFullSize - contactsToDisplayCount) > 0, personViewWithText);
    }

    private boolean checkFolderDisplayable(int folderIndex, boolean hasInvisibleContacts, int totalItemCount, int containerWidth) {
        int foldersContainerWidth = 0;
        GroupContactVM folder = mFolders.get(folderIndex);
        foldersContainerWidth += measureFolderItemWidth(folder.getGroupName());
        foldersContainerWidth += measureCounterWidth(hasInvisibleContacts, totalItemCount);
        return foldersContainerWidth <= containerWidth;
    }

    private boolean checkPersonDisplayable(int personIndex, boolean hasInvisibleContacts, int totalContactsSize, int containerWidth, boolean withText) {
        int personsContainerWidth = 0;
        ContactVM person = mPersons.get(personIndex);
        personsContainerWidth += measurePersonItemWidth(person.getName().getFirstName(), person.getName().getLastName(), withText);
        personsContainerWidth += measureCounterWidth(hasInvisibleContacts, totalContactsSize);
        return personsContainerWidth <= containerWidth;
    }

    private int measurePersonItemWidth(int personIndex, boolean withText) {
        ContactVM person = mPersons.get(personIndex);
        return measurePersonItemWidth(person.getName().getFirstName(), person.getName().getLastName(), withText);
    }

    private int measurePersonItemWidth(@Nullable String firstName, @Nullable String lastName, boolean withText) {
        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }

        if (withText) {
            return measurePersonTextWidth(firstName, lastName) + mItemPersonTextMarginLeft + mItemPersonTextMarginRight + mItemPersonPhotoSize + mItemMarginRight;
        } else {
            return mItemMarginRight + mItemPersonPhotoSize;
        }
    }

    private int measurePersonTextWidth(@NonNull String firstName, @NonNull String lastName) {
        int firstNameTextWidth = (int) mPersonsPaint.measureText(firstName);
        firstNameTextWidth = firstNameTextWidth <= mItemPersonTextMaxWidth ? firstNameTextWidth : mItemPersonTextMaxWidth;
        int secondNameTextWidth = (int) mPersonsPaint.measureText(lastName);
        secondNameTextWidth = secondNameTextWidth <= mItemPersonTextMaxWidth ? secondNameTextWidth : mItemPersonTextMaxWidth;
        return firstNameTextWidth > secondNameTextWidth ? firstNameTextWidth : secondNameTextWidth;
    }

    private int measureFolderTextWidth(@NonNull String folderText) {
        int folderTextWidth = (int) mFoldersPaint.measureText(folderText);
        return folderTextWidth <= mItemFolderTextMaxWidth ? folderTextWidth : mItemFolderTextMaxWidth;
    }

    private int measureFolderItemWidth(@NonNull String folderText) {
        return measureFolderTextWidth(folderText) + mItemFolderIconMarginLeft + mItemFolderIconMarginRight + mItemFolderTextMarginRight + mItemMarginRight + mItemFolderIconTextWidth;
    }

    private int measureFolderItemWidth(int folderIndex) {
        GroupContactVM folder = mFolders.get(folderIndex);
        return measureFolderItemWidth(folder.getGroupName());
    }

    private void configurePersonView(@NonNull PersonView personView, @NonNull ContactVM person, boolean witText) {
        int photoSize = getContext().getResources().getDimensionPixelSize(R.dimen.recipients_selection_person_photo_size);
        personView.setPersonData(person.getPreparedPhoto(photoSize, photoSize), person.getName().getFirstName(), person.getName().getLastName(), witText);
    }

    private PersonView generatePersonView(int personIndex, boolean withText) {
        PersonView personView = new PersonView(getContext());
        configurePersonView(personView, mPersons.get(personIndex), withText);
        return personView;
    }

    private void configureFolderView(@NonNull FolderView personView, @NonNull GroupContactVM folder) {
        personView.setFolderData(folder.getGroupName());
    }

    private FolderView generateFolderView(int folderPosition) {
        FolderView folderView = new FolderView(getContext());
        configureFolderView(folderView, mFolders.get(folderPosition));
        return folderView;
    }


    private static class FolderView extends FrameLayout {

        private TextView mFolderText;

        /** @SelfDocumented */
        public FolderView(@NonNull Context context) {
            super(context);
            initViews(context);
        }

        /** @SelfDocumented */
        public FolderView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initViews(context);
        }

        /** @SelfDocumented */
        public FolderView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initViews(context);
        }

        /** @SelfDocumented */
        void initViews(@NonNull Context context) {
            LayoutInflater.from(context).inflate(R.layout.recipient_selection_selected_panel_folder_item, this, true);
            mFolderText = findViewById(R.id.contact_selected_folder_text);
        }

        /** @SelfDocumented */
        public void setFolderData(@Nullable String folderText) {
            mFolderText.setText(folderText);
        }

    }


    private static class PersonView extends FrameLayout {

        private SimpleDraweeView mPicture;
        private TextView mFirstName;
        private TextView mLastName;

        /** @SelfDocumented */
        public PersonView(@NonNull Context context) {
            super(context);
            initViews(context);
        }

        /** @SelfDocumented */
        public PersonView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            initViews(context);
        }

        /** @SelfDocumented */
        public PersonView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initViews(context);
        }

        /** @SelfDocumented */
        void initViews(@NonNull Context context) {
            LayoutInflater.from(context).inflate(R.layout.recipient_selection_selected_panel_person_item, this, true);
            mPicture = findViewById(R.id.contact_selected_person_photo);
            mFirstName = findViewById(R.id.contact_selected_person_first_name);
            mLastName = findViewById(R.id.contact_selected_person_last_name);
        }

        /** @SelfDocumented */
        public void setPersonData(@Nullable String uriPhoto, @Nullable String firstName, @Nullable String lastName, boolean withText) {
            if (uriPhoto != null) {
                mPicture.setImageURI(Uri.parse(uriPhoto));
            }
            mFirstName.setText(firstName);
            mLastName.setText(lastName);
            if (withText) {
                setPersonTextVisibility(VISIBLE);
            } else {
                setPersonTextVisibility(GONE);
            }
        }

        private void setPersonTextVisibility(int visibility) {
            mLastName.setVisibility(visibility);
            mFirstName.setVisibility(visibility);
        }

    }

    /**
     * Класс для отображения View с общей информацией о выбранных получателях
     */
    static class SelectedContactsViewDisplayInfo extends SelectedItemsView.SelectedItemsViewDisplayInfo {
        private boolean mWithText;

        /** @SelfDocumented */
        SelectedContactsViewDisplayInfo(int totalContactsCount, boolean hasInvisibleContacts, boolean withText) {
            super(totalContactsCount, hasInvisibleContacts);
            mWithText = withText;
        }

        /** @SelfDocumented */
        boolean getWithText() {
            return mWithText;
        }
    }
}
