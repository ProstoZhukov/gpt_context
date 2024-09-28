package ru.tensor.sbis.viper.crud

import ru.tensor.sbis.plugin_struct.feature.Feature

/**@SelfDocumented*/
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface BaseCommandWrapperProvider<WRAPPER : Any> : Feature {

    /**@SelfDocumented*/
    fun getWrapper(): WRAPPER
}


/**@SelfDocumented*/
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface FilterCommandWrapperProvider<WRAPPER : Any, FILTER : Any> : BaseCommandWrapperProvider<WRAPPER> {

    /**@SelfDocumented*/
    fun getFilter(): FILTER
}