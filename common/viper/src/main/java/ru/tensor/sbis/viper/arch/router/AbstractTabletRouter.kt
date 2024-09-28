package ru.tensor.sbis.viper.arch.router

import ru.tensor.sbis.android_ext_decl.AndroidComponent

/**
 * Базовый класс для Router для планшетов.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class AbstractTabletRouter(androidComponent: AndroidComponent,
                                    protected val hostContainerId: Int = 0,
                                    protected val containerId: Int,
                                    protected val subContainerId: Int) : AbstractRouter(androidComponent)