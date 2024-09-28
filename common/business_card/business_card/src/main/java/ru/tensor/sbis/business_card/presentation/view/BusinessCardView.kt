package ru.tensor.sbis.business_card.presentation.view

import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isGone
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.google.zxing.BarcodeFormat
import ru.tensor.sbis.business_card.R
import ru.tensor.sbis.business_card.contract.internal.BusinessCardStore.Intent
import ru.tensor.sbis.business_card.contract.internal.BusinessCardStore.State
import ru.tensor.sbis.business_card.databinding.BusinessCardFragmentBinding
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.common.util.PreviewerUrlUtil
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.utils.extentions.getDimenFrom
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegate
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateImpl
import ru.tensor.sbis.design.utils.insets.DefaultViewInsetDelegateParams
import ru.tensor.sbis.design.utils.insets.IndentType
import ru.tensor.sbis.design.utils.insets.Position
import ru.tensor.sbis.design.utils.insets.ViewToAddInset
import ru.tensor.sbis.design.view_ext.MaskShape
import ru.tensor.sbis.design.view_ext.Source
import ru.tensor.sbis.design.view_ext.barcode.setBarcodeImage
import ru.tensor.sbis.design.view_ext.setImage

/** Представления UI экрана визитки */
internal class BusinessCardView(
    private val binding: BusinessCardFragmentBinding,
    private val data: BusinessCard
) : BaseMviView<State, Intent>(),
    DefaultViewInsetDelegate by DefaultViewInsetDelegateImpl() {

    init {
        initPersonView()
        initQRCode()
        initInsets()
    }

    private fun initPersonView() {
        with(binding) {
            if (data.personPhoto != null) {
                val photoUrl = PreviewerUrlUtil.replacePreviewerUrlPartWithCheck(
                    data.personPhoto!!,
                    PHOTO_IMAGE_SIZE_DP,
                    PHOTO_IMAGE_SIZE_DP,
                    PreviewerUrlUtil.ScaleMode.RESIZE
                )
                businessCardImage.setImage(Source.Image(photoUrl), shape = MaskShape.Circle)
            } else {
                businessCardImage.isGone = true
            }
            businessCardLinkButton.setOnClickListener { dispatch(Intent.OnLinkButtonClicked(data)) }
            businessCardName.text = data.personName
            businessCardDescription.text = data.personRole
            businessCardToolbar.apply {
                content = SbisTopNavigationContent.LargeTitle(
                    if (data.title.isNotEmpty()) PlatformSbisString.Value(data.title)
                    else PlatformSbisString.Res(R.string.business_card_toolbar_title)
                )
                showBackButton = true
                backBtn?.setOnClickListener { dispatch(Intent.OnToolbarBackClicked) }
            }
        }
    }

    private fun initQRCode() {
        val containerImageQR = LinearLayout(binding.businessCardQrContainer.context).apply {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val imageQR = AppCompatImageView(context).apply {
                layoutParams = MarginLayoutParams(dp(QR_IMAGE_SIZE_DP), dp(QR_IMAGE_SIZE_DP)).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    topMargin = getDimenFrom(R.dimen.business_card_qr_padding)
                }
                setBarcodeImage(data.links[0].url, BarcodeFormat.QR_CODE)
            }
            addView(imageQR)
        }
        binding.businessCardQrContainer.addView(containerImageQR)
    }

    private fun initInsets() {
        initInsetListener(
            DefaultViewInsetDelegateParams(
                listOf(ViewToAddInset(binding.businessCardToolbar, listOf(IndentType.PADDING to Position.TOP)))
            )
        )
    }

    companion object {
        private const val QR_IMAGE_SIZE_DP = 190
        private const val PHOTO_IMAGE_SIZE_DP = 96
    }
}