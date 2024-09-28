package ru.tensor.sbis.consignment_decl.base.util

/**
 * Компонент, помеченный данной аннотацией, переживает Config Change.
 *
 * @author kv.martyshenko
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY
)
annotation class ConfigChangeResistant