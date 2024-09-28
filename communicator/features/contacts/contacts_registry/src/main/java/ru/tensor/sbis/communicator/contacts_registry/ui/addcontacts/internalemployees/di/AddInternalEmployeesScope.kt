package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.di

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope

/**
 * DI scope компонента добавления сотрудников внутри компании в реестр контактов.
 *
 * @author da.zhukov
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
internal annotation class AddInternalEmployeesScope
