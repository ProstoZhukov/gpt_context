package ru.tensor.sbis.viper.arch.router

import ru.tensor.sbis.android_ext_decl.AndroidComponent

/**
 * Базовый класс для Router для телефонов.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class AbstractPhoneRouter(androidComponent: AndroidComponent,
                                   protected val containerId: Int) : AbstractRouter(androidComponent)