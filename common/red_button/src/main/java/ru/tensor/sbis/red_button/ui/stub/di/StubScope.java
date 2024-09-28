package ru.tensor.sbis.red_button.ui.stub.di;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

/**
 * Dagger-скоп для [RedButtonStubActivity]
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface StubScope {
}
