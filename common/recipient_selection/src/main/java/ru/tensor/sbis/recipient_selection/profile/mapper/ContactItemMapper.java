package ru.tensor.sbis.recipient_selection.profile.mapper;

import android.content.Context;
import androidx.annotation.NonNull;

import ru.tensor.sbis.communicator.generated.DialogDocument;
import ru.tensor.sbis.persons.ContactVM;
import ru.tensor.sbis.common.modelmapper.BaseModelMapper;
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile;
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem;

/**
 * Маппер для получения объекта класса {@link ContactItem} из объекта с интерфейсом {@link EmployeeProfile}
 */
public class ContactItemMapper extends BaseModelMapper<EmployeeProfile, ContactItem> {

    /** @SelfDocumented */
    public ContactItemMapper(@NonNull Context context) {
        super(context);
    }

    /**
     * Маппиг данных из EmployeeProfile в ContactItem
     */
    @Override
    public ContactItem apply(@NonNull EmployeeProfile employeeModel) throws Exception {
        return new ContactItem(createContactVM(employeeModel));
    }

    private ContactVM createContactVM(@NonNull EmployeeProfile employeeModel) {
        ContactVM contact = new ContactVM();
        contact.setUUID(employeeModel.getUuid());
        contact.setRawPhoto(employeeModel.getPhotoUrl());
        contact.setName(employeeModel.getName());
        if (!employeeModel.isPhysic()) {
            contact.setData1(employeeModel.getCompanyOrDepartment());
        } else {
            // Для физ.лица не выводим данных о работе
            contact.setData1(null);
            contact.setData2(null);
        }
        return contact;
    }

}

