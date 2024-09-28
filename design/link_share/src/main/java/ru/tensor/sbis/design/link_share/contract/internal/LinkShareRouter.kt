package ru.tensor.sbis.design.link_share.contract.internal

import androidx.fragment.app.Fragment
import ru.tensor.sbis.mvi_extension.router.Router

/**@SelfDocumented*/
internal interface LinkShareRouter : Router<Fragment> {

    /**@SelfDocumented */
    fun showCopyLinkAlertDialog(message: String, linkUrl: String)

    /**@SelfDocumented */
    fun openLinkInBrowser(linkUrl: String)

    /**@SelfDocumented */
    fun shareLink(linkUrl: String)

    /**@SelfDocumented */
    fun showQRCodeDialogFragment(linkUrl: String)
}