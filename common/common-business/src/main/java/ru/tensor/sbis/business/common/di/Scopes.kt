package ru.tensor.sbis.business.common.di

import javax.inject.Scope

/**
 * Аннтотация области действия для DI.
 * Продолжительность "времени" существования host-экрана, т.е. области действия, которую инжектор создает
 * и использует повторно сохраняя экземпляр для возможного повторного использования
 *
 * @see Scope @Scope
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerHostFragment

/**
 * Аннтотация области действия для DI.
 * Обозначает экран, который инжектор создает единожды.
 * Возможно использование повторно только в области действия одного [PerHostFragment], т.е.
 * когда "контекст" области видимости [PerHostFragment] заканчивается, любые экраны [PerFragment]
 * связанные с этой областью считаются выходящими за пределы области видимости и не могут быть повторно
 * введены в другие экземпляры.
 *
 * @see Scope @Scope
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerFragment

/**
 * Аннтотация области действия для DI.
 * Обозначает экран, который инжектор создает единожды.
 * Возможно использование повторно только в области действия одного [PerFragment], т.е.
 * когда "контекст" области видимости [PerFragment] заканчивается, любые экраны [PerSubFragment]
 * связанные с этой областью считаются выходящими за пределы области видимости и не могут быть повторно
 * введены в другие экземпляры.
 *
 * @see Scope @Scope
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerSubFragment