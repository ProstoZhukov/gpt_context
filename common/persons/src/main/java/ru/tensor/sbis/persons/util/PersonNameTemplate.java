package ru.tensor.sbis.persons.util;

import androidx.annotation.Nullable;

import android.text.TextUtils;

/**
 * Created by aa.mironychev on 12.08.16.
 *
 * @deprecated используйте {@link ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate}
 */

@SuppressWarnings("DeprecatedIsStillUsed")
public enum PersonNameTemplate implements NameFormat {
    SURNAME_NAME {
        @Nullable
        @Override
        public String format(String surname, String name, String patronymic) {
            boolean emptyName = TextUtils.isEmpty(name);
            boolean emptySurname = TextUtils.isEmpty(surname);

            if (emptyName) {
                if (emptySurname) {
                    return null;
                }

                return surname;
            }

            if (emptySurname) {
                return name;
            }

            return String.format("%s %s", surname, name);
        }
    },
    SURNAME_N {
        @Nullable
        @Override
        public String format(String surname, String name, String patronymic) {
            boolean emptyName = TextUtils.isEmpty(name);
            boolean emptySurname = TextUtils.isEmpty(surname);

            if (emptyName) {
                if (emptySurname) {
                    return null;
                }

                return surname;
            }

            if (emptySurname) {
                return name;
            }

            return String.format("%s %.1s.", surname, name);
        }
    },
    SURNAME_NAME_PATRONYMIC {
        @Nullable
        @Override
        public String format(String surname, String name, String patronymic) {
            boolean emptyName = TextUtils.isEmpty(name);
            boolean emptySurname = TextUtils.isEmpty(surname);
            boolean emptyPatronymic = TextUtils.isEmpty(patronymic);

            if (emptyName) {
                if (emptySurname) {
                    return null;
                }

                return surname;
            }

            if (emptySurname) {
                return emptyPatronymic
                        ? name
                        : String.format("%s %s", name, patronymic);
            }

            return emptyPatronymic
                    ? String.format("%s %s", surname, name)
                    : String.format("%s %s %s", surname, name, patronymic);
        }
    },
    SURNAME_N_P {
        @Nullable
        @Override
        public String format(String surname, String name, String patronymic) {
            boolean emptyName = TextUtils.isEmpty(name);
            boolean emptySurname = TextUtils.isEmpty(surname);
            boolean emptyPatronymic = TextUtils.isEmpty(patronymic);

            if (emptyName) {
                if (emptySurname) {
                    return null;
                }

                return surname;
            }

            if (emptySurname) {
                return emptyPatronymic
                        ? name
                        : String.format("%s %.1s.", name, patronymic);
            }

            return emptyPatronymic
                    ? String.format("%s %.1s.", surname, name)
                    : String.format("%s %.1s. %.1s.", surname, name, patronymic);
        }
    }
}
