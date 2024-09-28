package ru.tensor.sbis.persons.util;

import androidx.annotation.NonNull;

import ru.tensor.sbis.verification_decl.account.PersonalAccount;
import ru.tensor.sbis.verification_decl.account.UserAccount;
import ru.tensor.sbis.person_decl.profile.model.PersonName;

/**
 * Created by aa.mironychev on 11.08.16.
 *
 * @deprecated используйте {@link ru.tensor.sbis.persons.util.PersonFormatExtKt}
 * или {@link ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate}
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"})
public class PersonFormatUtils {

    /** @SelfDocumented */
    public static String formatName(@NonNull PersonalAccount account, @NonNull PersonNameTemplate template) {
        return template.format(account.getSurname(), account.getName(), null);
    }

    /** @SelfDocumented */
    public static String formatName(@NonNull UserAccount account, @NonNull PersonNameTemplate template) {
        return template.format(account.getUserSurname(), account.getUserName(), account.getUserPatronymic());
    }

    /** @SelfDocumented */
    public static String formatName(@NonNull PersonName name, @NonNull PersonNameTemplate template) {
        return template.format(name.getLastName(), name.getFirstName(), name.getPatronymicName());
    }

    /** @SelfDocumented */
    public static String formatName(String surname, String name, String patronymic, @NonNull PersonNameTemplate template) {
        return template.format(surname, name, patronymic);
    }

    /** @SelfDocumented */
    public static String formatName(String surname, String name, @NonNull PersonNameTemplate template) {
        return template.format(surname, name, null);
    }
}
