package ru.tensor.sbis.design.retail_views.payment_view.internal

import androidx.annotation.RequiresOptIn

/**
 * Аннотация для обозначения "опасного" Api.
 *
 * В данном конкретном случае - используйте указанное API на свой страх и риск,
 * корректная работа PaymentView после вашего вмешательства не гарантирована.
 *
 * Если не уверены на 100% в корректности своих действий - используйте ViewAccessSafetyApi.,
 * */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@MustBeDocumented
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER
)
internal annotation class DangerousApi