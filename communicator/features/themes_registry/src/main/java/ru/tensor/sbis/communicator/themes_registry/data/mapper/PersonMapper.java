package ru.tensor.sbis.communicator.themes_registry.data.mapper;

import android.content.Context;
import androidx.annotation.NonNull;

import ru.tensor.sbis.persons.PersonModel;
import ru.tensor.sbis.persons.PersonName;
import ru.tensor.sbis.common.modelmapper.BaseModelMapper;
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile;

/**
 * Created by aa.mironychev on 02.10.17.
 *
 * Абстрактный класс для мапинга наследников {@link PersonModel}.
 * @param <T> - расширение {@link PersonModel}
 */
public abstract class PersonMapper<T extends PersonModel> extends BaseModelMapper<EmployeeProfile, T> {

    PersonMapper(@NonNull Context context) {
        super(context);
    }

    /**
     * Создает "болванку" типа {@link T} для заполнения ее данными из объекта {@link EmployeeProfile}.
     * @return пустая модель типа {@link T}
     */
    @NonNull
    protected abstract T createBlank();

    /**
     * Конвертирует объект типа {@link EmployeeProfile} в модель типа {@link T}.
     * @param profile - исходная модель
     * @return результирующая модель
     */
    @Override
    public T apply(@NonNull EmployeeProfile profile) {
        T instance = createBlank();
        fill(instance, profile);
        return instance;
    }

    /**
     * Заполняет "болванку" типа {@link T} данными на основе объекта {@link EmployeeProfile}.
     * @param dest  - источник данных
     * @param src   - результирующая модель
     */
    void fill(@NonNull T dest, @NonNull EmployeeProfile src) {
        dest.setUUID(src.getUuid());
        dest.setRawPhoto(src.getPhotoUrl());
        dest.setName(new PersonName(src.getName().getFirstName(), src.getName().getLastName(), src.getName().getPatronymicName()));
    }

}
