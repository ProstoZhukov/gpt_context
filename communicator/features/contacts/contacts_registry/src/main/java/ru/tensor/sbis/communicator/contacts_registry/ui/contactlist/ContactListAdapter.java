package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.tensor.sbis.base_components.adapter.AbstractViewHolder;
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter;
import ru.tensor.sbis.common.util.UUIDUtils;
import ru.tensor.sbis.communicator.common.util.layout_manager.CommunicatorLayoutManager;
import ru.tensor.sbis.communicator.common.view.SegmentDividerItemDecoration;
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.model.ContactProfileModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.diff_util.ContactListDiffCallback;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.diff_util.ContactListUpdateCallback;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactFoldersModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel;
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsStubModel;
import ru.tensor.sbis.communicator.contacts_registry.utils.FoldersViewHolderHelper;
import ru.tensor.sbis.design.custom_view_tools.styles.CanvasStylesProvider;
import ru.tensor.sbis.design.folders.FoldersView;
import ru.tensor.sbis.design.folders.support.utils.stub_integration.StubViewMediator;
import ru.tensor.sbis.design.list_header.DateTimeAdapter;
import ru.tensor.sbis.design.list_header.ListDateViewUpdater;
import ru.tensor.sbis.design.stubview.StubView;
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfileModel;
import ru.tensor.sbis.profile_service.models.person.PersonModel;
import ru.tensor.sbis.swipeablelayout.SwipeableViewBinderHelper;
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper;
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool;

/**
 * Адаптер списка контактов
 *
 * @author da.zhukov
 */
@UiThread
public class ContactListAdapter
        extends BaseTwoWayPaginationAdapter<ContactRegistryModel>
        implements DateTimeAdapter {

    private static final int MAX_CONTACTS_RECYCLED_VIEWS = 100;
    private static final String SEGMENTED_DIVIDER_POSITION_KEY = "SEGMENTED_DIVIDER_POSITION_KEY";

    public static final int HOLDER_CONTACT = 1;
    public static final int HOLDER_FOLDERS = 2;
    public static final int HOLDER_STUB = 3;

    @NonNull
    private final SwipeableViewBinderHelper<UUID> mSwipeHelper = new SwipeableViewBinderHelper<>();

    private ContactListActionsListener mContactListActionsListener;

    @NonNull
    private final SwipeMenuViewPool mSwipeMenuViewPool;

    @NonNull
    private final ListDateViewUpdater mDateUpdater;

    private final CanvasStylesProvider stylesProvider = new CanvasStylesProvider() {};

    private boolean mHeadersEnabled;
    private boolean mIsHeadersEnableChanged;
    private int segmentDividerPosition;

    private ContactHolder.ContactItemsClickHandler mContactItemsClickHandler;

    private StubViewMediator mStubViewMediator;

    private FoldersView foldersView;
    private FoldersViewHolderHelper foldersViewHolderHelper;

    public void setStubViewMediator(StubViewMediator stubMediator) {
        mStubViewMediator = stubMediator;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        stylesProvider.setResourceCacheEnabled(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(HOLDER_CONTACT, MAX_CONTACTS_RECYCLED_VIEWS);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(HOLDER_FOLDERS, 1);
        if (foldersView == null) {
            resetFoldersView();
            foldersViewHolderHelper.attachFoldersView(foldersView);
        }
    }

    public void closeFoldersView() {
        if (foldersView != null) {
            foldersView.showCompactFolders();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stylesProvider.setResourceCacheEnabled(false);
        stylesProvider.clearReferences();
    }

    public ContactListAdapter(
        @NonNull SwipeMenuViewPool swipeMenuViewPool,
        @NonNull ListDateViewUpdater dateUpdater
    ) {
        mSwipeMenuViewPool = swipeMenuViewPool;
        mDateUpdater = dateUpdater;
    }

    /** SelfDocumented */
    public void setFoldersViewHolderHelper(FoldersViewHolderHelper helper) {
        foldersViewHolderHelper = helper;
    }

    @Override
    public void onSavedInstanceState(@NonNull Bundle outState) {
        super.onSavedInstanceState(outState);
        outState.putInt(SEGMENTED_DIVIDER_POSITION_KEY, segmentDividerPosition);
        mSwipeHelper.saveStates(outState, ContactListAdapter.class);
        if (foldersView != null) {
            foldersView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        segmentDividerPosition = savedInstanceState.getInt(SEGMENTED_DIVIDER_POSITION_KEY);
        mSwipeHelper.restoreStates(savedInstanceState, ContactListAdapter.class);
        if (foldersView != null) {
            foldersView.onRestoreInstanceState(savedInstanceState);
        }
    }

    public void clear() {
        setContactListActionsListener(null);
        setFoldersViewHolderHelper(null);
        mSwipeHelper.clearEntries();
    }

    private void resetFoldersView() {
        if (mRecyclerView != null) {
            foldersView = new FoldersView(mRecyclerView.getContext());
        }
    }

    //region swipe layout

    public void closeAllOpenSwipeMenus() {
        SwipeHelper.INSTANCE.closeAll(true);
    }

    public void closeAllSwipeItems() {
        mSwipeHelper.closeAll();
    }

    public void clearSwipeMenuState() {
        mSwipeHelper.clearEntries();
    }

    private boolean isSwipeableViewHolder(int holderViewType) {
        return holderViewType == HOLDER_CONTACT;
    }

    public void removeSavedSwipeState(@NonNull UUID uuid) {
        mSwipeHelper.removeEntry(uuid);
    }
    //endregion

    @Override
    public Date getItemDateTime(int position) {
        ContactsModel item = null;
        if (getItem(position) instanceof ContactsModel) {
            item = (ContactsModel) getItem(position);
        }
        return mHeadersEnabled && item != null ? item.getLastMessageDate() : null;
    }

    public void enableHeaders(boolean enable) {
        if (mHeadersEnabled != enable) mIsHeadersEnableChanged = true;
        mHeadersEnabled = enable;
    }

    public void setContactListActionsListener(@Nullable ContactListActionsListener listener) {
        mContactListActionsListener = listener;
    }

    @Override
    protected int getItemType(@Nullable ContactRegistryModel dataModel) {
        if (dataModel == null) {
            return HOLDER_EMPTY;
        } else if (dataModel instanceof ContactFoldersModel) {
            return HOLDER_FOLDERS;
        } else if (dataModel instanceof ContactsStubModel) {
            return HOLDER_STUB;
        } else {
            return HOLDER_CONTACT;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == HOLDER_CONTACT) {
            return createContactHolder(parent);
        } else if (viewType == HOLDER_FOLDERS) {
            // Избегаем краша когда foldersView еще в recyclerView и recyclerView пытается создать новый FoldersViewHolder.
            // Костыльно, но другого варианта пока не нашли.
            if ((foldersView == null || foldersView.getParent() != null) && foldersViewHolderHelper != null) {
                // вызов этого метода создаст новый foldersView
                resetFoldersView();
            }
            if (foldersView == null) {
                throw new RuntimeException("FoldersView is null!");
            }
            return new ContactFoldersHolder(foldersView);
        } else if (viewType == HOLDER_STUB) {
            StubView stubView = new StubView(parent.getContext());
            stubView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            return new ContactStubViewHolder(stubView);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    private ContactHolder createContactHolder(@NonNull ViewGroup parent) {
        return new ContactHolder(
            new ContactRegistryItemView(
                parent.getContext(),
                null,
                stylesProvider
            ),
            mSwipeMenuViewPool,
            mContactListActionsListener,
            mContactItemsClickHandler
        );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ContactsModel contactsModel = null;
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            CommunicatorLayoutManager communicatorLayoutManager = (CommunicatorLayoutManager) mRecyclerView.getLayoutManager();
            if (getItem(position) instanceof ContactsModel) {
                contactsModel = (ContactsModel) getItem(position);
                communicatorLayoutManager.setVerticalScrollEnabled(true);
            }
            if (HOLDER_STUB == holder.getItemViewType()) {
                ContactsStubModel stub = (ContactsStubModel) getItem(position);
                ContactStubViewHolder stubViewHolder = (ContactStubViewHolder) holder;
                mStubViewMediator.setStubView(stubViewHolder.getStubView());
                if (stub != null && stub.getContent() != null) {
                    stubViewHolder.getStubView().setContent(stub.getContent());
                }
                communicatorLayoutManager.setVerticalScrollEnabled(false);
            }
        }

        if (isSwipeableViewHolder(holder.getItemViewType())) {
            ContactRegistryModel previousItem = position - 1 >= 0 ? getItem(position - 1) : null;
            ContactHolder contactHolder = null;
            if (holder instanceof ContactHolder) {
                contactHolder = (ContactHolder) holder;
            }

            if (contactHolder != null) {
                if (contactsModel != null) {
                    boolean isSegmentedDividerVisible = contactsModel.isSegmentDividerVisible();
                    contactHolder.configureSegmentDivider(isSegmentedDividerVisible);
                    contactHolder.configureSeparator(mHeadersEnabled || (!isSegmentedDividerVisible && previousItem != null && !(previousItem instanceof ContactFoldersModel)));
                    contactHolder.setCheckModeEnabled(getCheckHelper() != null && getCheckHelper().isCheckModeEnabled() && !contactsModel.getContact().isMyAccountManager());
                    contactsModel.setFormatterDateTime(mDateUpdater.getFormattedDate(position));
                    contactHolder.bind(contactsModel);
                }
                mSwipeHelper.bind(contactHolder.getSwipeableLayout(), contactsModel == null ? null : contactsModel.getContact().getUuid());
            }
        } else if (holder.getItemViewType() == HOLDER_FOLDERS) {
            FoldersView itemView = (FoldersView) holder.itemView;
            if (itemView != foldersView || !isFoldersMeasuredCorrectly(itemView)) {
                foldersViewHolderHelper.detachFoldersView(foldersView);
                foldersViewHolderHelper.attachFoldersView(itemView);
                foldersView = itemView;
            }
        } else {
            holder.bind(contactsModel);
        }
    }

    /**
     * Проверить, что папки измерились корректно.
     * Очередной откровенный костль для android sdk, когда в списке есть ячейка со своим
     * дата-сурсом, и почему-то в процессе отрисовки корневого ресайклера - внутренний не может
     * корректно посчитаться, после чего он становится инициализированным, видимым, но с высотой 0.
     * Данная проверка позвляет понять, что необходимо перепривязать компонент папок к его
     * вью-модели, чтобы вызвать перестроение внутреннего ресайклера.
     */
    private boolean isFoldersMeasuredCorrectly(FoldersView view) {
        boolean isFoldersMeasuredCorrectly;
        try {
            View dividerView = view.getChildAt(view.getChildCount() - 1);
            int dividerHeight = dividerView.getMeasuredHeight();
            isFoldersMeasuredCorrectly = view.getMeasuredHeight() - dividerHeight > 0;
        } catch (Exception ex) {
            isFoldersMeasuredCorrectly = false;
        }
        return isFoldersMeasuredCorrectly;
    }

    @Override
    protected boolean isMatching(@NonNull ContactRegistryModel src, @NonNull ContactRegistryModel item) {
        if (src instanceof ContactsModel && item instanceof ContactsModel) {
            return UUIDUtils.equals(((ContactsModel) src).getContact().getUuid(), ((ContactsModel) item).getContact().getUuid());
        } else return false;
    }

    public void setContactItemsClickHandler(ContactHolder.ContactItemsClickHandler helper) {
        mContactItemsClickHandler = helper;
    }

    public int getSegmentDividerPosition() {
        return segmentDividerPosition;
    }

    /**
     * Поиск позиции item'а-разделителя "Найдено ещё в сотрудниках"
     *
     * @return позицию item'а-разделителя
     * Если item'а-разделителя в списке нет, то возвращаем Integer.MAX_VALUE
     * Вариант с -1 здесь не годится из-за особенностей реализации {@link SegmentDividerItemDecoration}
     * Мы отображаем sticky item-разделитель, если позиция item'а будет больше или равна позиции item'а-разделителя
     */
    private int findSegmentDividerPosition() {
        List<ContactRegistryModel> contacts = getContent();
        for (int i = 0; i < contacts.size(); i++) {
            ContactsModel model = null;
            if (contacts.get(i) instanceof ContactsModel) {
                model = (ContactsModel) contacts.get(i);
            }
            if (model != null && model.isSegmentDividerVisible()) return i;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public void setData(@Nullable List<ContactRegistryModel> dataList, int offset) {
        mOffset = offset;
        DiffUtil.DiffResult diffResult = null;

        boolean notifyDataSetChanged = dataList == null || mIsHeadersEnableChanged;
        if (!notifyDataSetChanged) {
            diffResult = DiffUtil.calculateDiff(new ContactListDiffCallback(getContent(), dataList));
        }

        setContent(dataList, notifyDataSetChanged);

        if (diffResult != null) {
            diffResult.dispatchUpdatesTo(new ContactListUpdateCallback(this, offset));
        }

        if (dataList != null && !dataList.isEmpty()) {
            segmentDividerPosition = findSegmentDividerPosition();
        }
    }

    @Override
    protected int getHolderBottomPaddingHeight(@NonNull Context context) {
        // Используем размер контейнера для Fab
        return context.getResources().getDimensionPixelSize(ru.tensor.sbis.communicator.design.R.dimen.communicator_fab_container_height);
    }

    //region AbstractSelectableListAdapter override methods
    @Override
    protected boolean needSaveStateInAdapter() {
        return false;
    }

    @Override
    @NonNull
    public ContactRegistryModel provideStubItem() {
        return new ContactsModel(new ContactProfileModel(new EmployeeProfileModel(new PersonModel(UUIDUtils.NIL_UUID))));
    }

    @Override
    public void onViewRecycled(@NonNull AbstractViewHolder<ContactRegistryModel> holder) {
        super.onViewRecycled(holder);
        if (holder.getItemViewType() == HOLDER_STUB) {
            // если заглушка не видна, можно отвязать её
            mStubViewMediator.setStubView(null);
        }
    }
    //endregion

    public interface ContactListActionsListener {

        //region swipe actions
        void onSwipeRemoveClicked(@NonNull ContactsModel contact);

        void onSwipeMoveToFolderClicked(@NonNull ContactsModel contact);

        void onSwipeSendMessageClicked(@NonNull ContactsModel contact);

        void onDismissed(@NonNull UUID uuid);

        void onDismissedWithoutMessage(@Nullable String uuid);
    }
}