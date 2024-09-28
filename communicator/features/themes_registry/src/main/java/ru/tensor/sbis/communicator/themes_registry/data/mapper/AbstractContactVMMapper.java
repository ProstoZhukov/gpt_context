package ru.tensor.sbis.communicator.themes_registry.data.mapper;

import android.content.Context;
import androidx.annotation.NonNull;

import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile;

/**
 * Created by aa.mironychev on 02.10.17.
 * <p>
 * Класс для маппинга вью-моделей контактов с информацией о работе/должности.
 */
public abstract class AbstractContactVMMapper<T extends ContactVM> extends PersonMapper<T> {

    public AbstractContactVMMapper(@NonNull Context context) {
        super(context);
    }

    @Override
    void fill(@NonNull T dest, @NonNull EmployeeProfile src) {
        super.fill(dest, src);
        fillAdditionalData(dest, src);
    }

    protected void fillAdditionalData(@NonNull T dest, @NonNull EmployeeProfile src) {
            String companyOrDepartmentData = src.getCompanyOrDepartment();
            dest.setInitialsStubData(src.getInitialsStubData());

            if (src.getPosition() == null || src.getPosition().trim().isEmpty()) {
                dest.setData1(companyOrDepartmentData);
            } else {
                dest.setData1(src.getPosition());
                dest.setData2(companyOrDepartmentData);
            }
    }

}
