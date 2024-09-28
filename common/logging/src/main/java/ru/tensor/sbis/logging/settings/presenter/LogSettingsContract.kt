package ru.tensor.sbis.logging.settings.presenter

import ru.tensor.sbis.mvp.fragment.selection.SelectionWindowContract

/**
 * Контракт экрана настроек логирования.
 *
 * @author da.zolotarev
 */
internal interface LogSettingsContract {

    /**@SelfDocumented**/
    interface View : SelectionWindowContract.View {
        fun onCloseClick()
    }

    /**@SelfDocumented**/
    interface Presenter : SelectionWindowContract.Presenter<View>
}