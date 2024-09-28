package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import ru.tensor.sbis.base_components.AdjustResizeActivity;
import ru.tensor.sbis.common.listener.ResultListener;
import ru.tensor.sbis.communicator.contacts_registry.R;
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactResult;
import ru.tensor.sbis.design.theme.res.PlatformSbisString;
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent;
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView;
import ru.tensor.sbis.design.utils.KeyboardUtils;

/**
 * Активность экрана добавления контакта в реестр контактов
 *
 * @author da.zhukov
 */
@UiThread
public class AddNewContactsActivity extends AdjustResizeActivity
        implements ResultListener<AddContactResult> {

    public static final int REQUEST_CODE = 106;
    public static final String SELECTION_RESULT_EXTRA_KEY = AddNewContactsActivity.class.getCanonicalName() + ".SELECTION_RESULT_EXTRA_KEY";
    private static final String FOLDER_UUID_ARG = AddNewContactsActivity.class.getCanonicalName() + ".FOLDER_UUID_ARG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.nothing);

        setContentView(R.layout.communicator_activity_add_new_contacts);

        initToolbar();

        if (savedInstanceState == null) {
            createFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createFragment() {
        Intent intent = getIntent();
        UUID folderUuid = intent != null ? (UUID) intent.getSerializableExtra(FOLDER_UUID_ARG) : null;

        AddNewContactsFragment fragment = AddNewContactsFragment.newInstance(folderUuid);

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.communicator_add_new_contacts_frame_layout, fragment)
            .commit();
    }

    private void initToolbar() {
        SbisTopNavigationView sbisToolbar = findViewById(R.id.communicator_add_new_contacts_sbis_toolbar);
        sbisToolbar.setContent(
                new SbisTopNavigationContent.SmallTitle(
                        new PlatformSbisString.Res(
                                ru.tensor.sbis.communicator.design.R.string.communicator_activity_add_new_contacts_label
                        ),
                        null,
                        null,
                        null,
                        null,
                        () -> null
                )
        );
        if (sbisToolbar.getBackBtn() != null) {
            sbisToolbar.getBackBtn().setOnClickListener(view -> {
                KeyboardUtils.hideKeyboard(view);
                onBackPressed();
            });
        }
    }

    @NonNull
    public static Intent provideStartingIntent(@NonNull Context context, @Nullable UUID folderUuid) {
        Intent intent = new Intent(context, AddNewContactsActivity.class);
        intent.putExtra(FOLDER_UUID_ARG, folderUuid);
        return intent;
    }

    @Override
    protected int getContentViewId() {
        return R.id.communicator_add_new_contacts_frame_layout;
    }

    @Override
    protected boolean swipeBackEnabled() {
        return true;
    }

    //region ResultListener interface implementation
    @Override
    public void onResultOk(@NonNull AddContactResult result) {
        Bundle data = new Bundle();
        data.putParcelable(SELECTION_RESULT_EXTRA_KEY, result);
        Intent intent = new Intent();
        intent.putExtras(data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onResultCancel() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    public void onViewGoneBySwipe() {
        super.onViewGoneBySwipe();
        KeyboardUtils.hideKeyboard(getContentView());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(ru.tensor.sbis.design.R.anim.nothing, ru.tensor.sbis.design.R.anim.right_out);
    }
}
