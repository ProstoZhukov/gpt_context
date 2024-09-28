package ru.tensor.sbis.person_decl.employee.my_profile

import ru.tensor.sbis.person_decl.employee.my_profile.factory.MyProfileFragmentFactory
import ru.tensor.sbis.person_decl.employee.my_profile.factory.MyProfileIntentFactory

/**
 * Поставщик моего профиля
 * @see MyProfileFragmentFactory
 * @see MyProfileIntentFactory
 *
 * @author ra.temnikov
 */
interface MyProfileProvider :
    MyProfileFragmentFactory,
    MyProfileIntentFactory