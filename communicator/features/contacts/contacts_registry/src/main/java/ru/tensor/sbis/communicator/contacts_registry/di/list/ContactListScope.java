package ru.tensor.sbis.communicator.contacts_registry.di.list;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * DI scope реестра контактов
 *
 * @author da.zhukov
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
@interface ContactListScope { }
