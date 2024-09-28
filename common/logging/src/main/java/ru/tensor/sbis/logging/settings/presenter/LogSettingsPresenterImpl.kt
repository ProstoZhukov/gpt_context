package ru.tensor.sbis.logging.settings.presenter

/**
 * Реализация презентера экрана настроек логирования
 *
 * @author da.zolotarev
 */
internal class LogSettingsPresenterImpl : LogSettingsContract.Presenter {
    private var fragView: LogSettingsContract.View? = null

    override fun attachView(view: LogSettingsContract.View) {
        fragView = view
    }

    override fun onCloseClick() {
        fragView?.closeWindow()
    }

    override fun onBackPressed() = true

    override fun detachView() {
        fragView = null
    }

    override fun onDestroy() = Unit
}