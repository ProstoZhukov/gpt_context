package ru.tensor.sbis.common.util.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by ss.buvaylink on 21.01.2017.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerApp {
}
