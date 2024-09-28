package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
 * Активность добавления сотрудников внутри компании в реестр контактов
 *
 * @author da.zhukov
 */
@UiThread
public class AddInternalEmployeesActivity extends AdjustResizeActivity
    implements ResultListener<AddContactResult> {

    public static final int REQUEST_CODE = 105;
    public static final String SELECTION_RESULT_EXTRA_KEY = AddInternalEmployeesActivity.class.getCanonicalName() + ".SELECTION_RESULT_EXTRA_KEY";
    private static final String FOLDER_UUID_ARG = AddInternalEmployeesActivity.class.getCanonicalName() + ".FOLDER_UUID_ARG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(ru.tensor.sbis.design.R.anim.right_in, ru.tensor.sbis.design.R.anim.nothing);

        setContentView(R.layout.communicator_activity_add_internal_employees);

        initToolbar();

        if (savedInstanceState == null) {
            createFragment();
        }
    }

    private void initToolbar() {
        SbisTopNavigationView sbisToolbar = findViewById(R.id.communicator_add_internal_employees_sbis_toolbar);
        sbisToolbar.setContent(
                new SbisTopNavigationContent.SmallTitle(
                        new PlatformSbisString.Res(
                                ru.tensor.sbis.communicator.design.R.string.communicator_adding_internal_employee_label
                        ),
                        null,
                        null,
                        null,
                        null,
                        () -> null
                )
        );
        if (sbisToolbar.getBackBtn() != null) {
            sbisToolbar.getBackBtn().setOnClickListener(view -> finish());
        }
    }

    private void createFragment() {
        Intent intent = getIntent();
        UUID folderUuid = intent != null ? (UUID) intent.getSerializableExtra(FOLDER_UUID_ARG) : null;

        AddInternalEmployeesFragment fragment =
                AddInternalEmployeesFragment.newInstance(folderUuid);

        getSupportFragmentManager().beginTransaction().
                replace(R.id.communicator_add_internal_employees_frame_layout, fragment)
                .commit();
    }

    @Override
    public int getContentViewId() {
        return R.id.communicator_add_internal_employees_frame_layout;
    }

    @Override
    protected boolean swipeBackEnabled() {
        return true;
    }

    @NonNull
    public static Intent provideStartingIntent(@NonNull Context context, @Nullable UUID folderUuid) {
        Intent intent = new Intent(context, AddInternalEmployeesActivity.class);
        intent.putExtra(FOLDER_UUID_ARG, folderUuid);
        return intent;
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
    public void finish() {
        KeyboardUtils.hideKeyboard(getContentView());
        super.finish();
        overridePendingTransition(ru.tensor.sbis.design.R.anim.nothing, ru.tensor.sbis.design.R.anim.right_out);
    }
}
