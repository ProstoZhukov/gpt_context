package ru.tensor.sbis.viper.arch.router

/**
 * Интерфейс для взаимодействия Router и Presenter.
 *
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface RouterProxyPresenter<T> {

    fun getRouterProxy(): RouterProxy<T>
}