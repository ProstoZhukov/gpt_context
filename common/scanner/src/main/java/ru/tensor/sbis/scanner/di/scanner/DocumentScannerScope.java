package ru.tensor.sbis.scanner.di.scanner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author am.boldinov
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentScannerScope {
}
