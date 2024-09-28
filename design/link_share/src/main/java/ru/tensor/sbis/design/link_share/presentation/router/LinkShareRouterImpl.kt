package ru.tensor.sbis.design.link_share.presentation.router

import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.google.zxing.BarcodeFormat
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.openInBrowserApp
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.confirmation_dialog.BaseContentProvider
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonOrientation
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.link_share.R
import ru.tensor.sbis.design.link_share.contract.internal.LinkShareRouter
import ru.tensor.sbis.design.view_ext.barcode.setBarcodeImage
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter

/**@SelfDocumented*/
internal class LinkShareRouterImpl : LinkShareRouter, FragmentRouter() {

    override fun showCopyLinkAlertDialog(message: String, linkUrl: String) = execute {
        ClipboardManager.copyToClipboard(requireContext(), linkUrl)
        SbisPopupNotification.push(SbisPopupNotificationStyle.SUCCESS, message)
    }

    /**@SelfDocumented */
    override fun openLinkInBrowser(linkUrl: String) = execute {
        openInBrowserApp(requireContext(), linkUrl)
    }

    /**@SelfDocumented */
    override fun shareLink(linkUrl: String) = execute {
        val sendIntent = android.content.Intent()
        sendIntent.action = android.content.Intent.ACTION_SEND
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, linkUrl)
        sendIntent.type = "text/plain"

        this.activity?.startActivity(android.content.Intent.createChooser(sendIntent, null))
    }

    /**@SelfDocumented */
    override fun showQRCodeDialogFragment(linkUrl: String) = execute {
        val imageQR = AppCompatImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(dp(QR_IMAGE_SIZE_DP), dp(QR_IMAGE_SIZE_DP)).apply {
                gravity = Gravity.CENTER
            }
            setBarcodeImage(linkUrl, BarcodeFormat.QR_CODE)
        }

        ConfirmationDialog(
            contentProvider = BaseContentProvider(null, null, provider = { _, _ -> imageQR }),
            buttons = {
                listOf(
                    ButtonModel(
                        ConfirmationButtonId.OK,
                        R.string.link_share_qr_confirmation_dialog_button_ok,
                        PrimaryButtonStyle,
                        true
                    )
                )
            },
            style = ConfirmationDialogStyle.PRIMARY,
            buttonOrientation = ConfirmationButtonOrientation.HORIZONTAL,
            showMarker = true
        ).show(childFragmentManager)
    }

    companion object {
        private const val QR_IMAGE_SIZE_DP = 190
    }
}