package ru.tensor.sbis.design_dialogs.dialogs.container

import io.reactivex.Observable

/**
 * Поставщик параметров верхнего и нижнего инсета контейнера.
 *
 * Верхний инсет - это системный status bar и тулбар.
 * Нижний инсет - это системный navigation bar и иные вью снизу экрана.
 */
interface ContainerInsetsProvider {
    val topInset: Observable<ContainerInsetParams>
    val bottomInset: Observable<ContainerInsetParams>
}