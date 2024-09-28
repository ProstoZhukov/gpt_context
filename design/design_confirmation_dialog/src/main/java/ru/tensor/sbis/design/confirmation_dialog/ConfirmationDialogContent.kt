package ru.tensor.sbis.design.confirmation_dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ScrollView
import androidx.annotation.DimenRes
import androidx.core.graphics.toColorInt
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.style.*
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.container.ViewContent
import ru.tensor.sbis.design.design_confirmation.R
import ru.tensor.sbis.design.design_confirmation.databinding.ConfirmationDialogBinding
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Контент для диалога подтверждения
 *
 * @author ma.kolpakov
 */
internal class ConfirmationDialogContent(
    private val confirmation: ConfirmationDialog<Any>,
    @DimenRes private val customWidth: Int = R.dimen.design_confirmation_dialog_width
) : ViewContent {

    override fun theme() = R.style.ConfirmationDialog
    override fun customWidth() = customWidth

    private lateinit var binding: ConfirmationDialogBinding
    private lateinit var containerFragment: SbisContainerImpl

    override fun getView(containerFragment: SbisContainerImpl, container: ViewGroup): View {
        this.containerFragment = containerFragment
        val context = containerFragment.requireContext()
        context.theme.applyStyle(R.style.ConfirmationDialog, false)

        binding = ConfirmationDialogBinding.inflate(LayoutInflater.from(context))

        updateDialog(dialog = confirmation, needUpdateSize = true)

        return if (confirmation.contentProvider is TextInputContentProvider || confirmation.isContainerScrolled) {
            ScrollView(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                addView(binding.root)
            }
        } else {
            binding.root
        }
    }

    /**
     * Обновить контент диалога.
     *
     * @param dialog новая модель диалога для отображения. При пересоздании будет использована старая модель из
     * конструктора. Для восстановления новой модели используй новый [ConfirmationDialogContentCreator], как это
     * сделано в методе [ConfirmationDialog.findAndUpdateDialog].
     * @param needUpdateSize нужно ли обновлять размер контейнера под новый контент.
     */
    internal fun updateDialog(dialog: ConfirmationDialog<*>, needUpdateSize: Boolean) {
        updateMarker(dialog.showMarker, dialog.style)
        updateDialogSize(needUpdateSize)
        updateDialogContent(dialog.contentProvider)
        updateButtonOrientation(dialog.buttonOrientation)
        updateButtonsGroup(dialog.buttons())
    }

    private fun updateMarker(showMarker: Boolean, style: ConfirmationDialogStyle) =
        with(binding.designConfirmationMarker) {
            background.setTint(getMarkerColor(binding.root.context, style))
            visibility = if (showMarker) VISIBLE else INVISIBLE
        }

    private fun updateDialogSize(needUpdateSize: Boolean) = with(binding.root) {
        if (layoutParams == null) return
        updateLayoutParams(if (needUpdateSize) ::setMatchParentSize else ::setOldSize)
    }

    private fun updateDialogContent(contentProvider: ContentProvider?) =
        binding.designConfirmationContentContainer.run {
            if (contentProvider == null) return
            removeAllViews()
            addView(contentProvider.getContent(context, containerFragment))
        }

    private fun updateButtonOrientation(buttonOrientation: ConfirmationButtonOrientation?) =
        binding.designConfirmationButtonsContainer.run {
            if (buttonOrientation != null) this.orientation = buttonOrientation
        }

    private fun updateButtonsGroup(buttons: List<ButtonModel<Any>>?) = binding.designConfirmationButtonsContainer.run {
        if (buttons == null) return@run
        val minButtonWidth = context.getDimenPx(R.attr.confirmationDialogMinButtonWidth)
        removeAllViews()
        buttons.forEach { buttonModel ->
            val newButton = createButton(context, buttonModel, minButtonWidth, containerFragment)
            addView(newButton)
        }
    }

    override fun onViewCreated(containerFragment: SbisContainerImpl) {
        confirmation.onDialogViewCreated?.invoke(containerFragment)
        val gradient = createGradient(containerFragment.requireContext(), binding.root.measuredWidth)
        val backgroundColor = createBackgroundColor(containerFragment.requireContext())
        binding.root.background = LayerDrawable(arrayOf(backgroundColor, gradient))
    }

    override fun useDefaultHorizontalOffset() = false

    private fun createButton(
        context: Context,
        buttonModel: ButtonModel<Any>,
        minButtonWidth: Int,
        containerFragment: SbisContainerImpl
    ): SbisButton {
        return SbisButton(context).apply {
            val mStyle = if (buttonModel.isPrimary) getButtonStyleColor() else buttonModel.style
            style = getStyleWithChangedBackgroundRes(mStyle)
            val titleString = buttonModel.labelString ?: buttonModel.labelRes?.let { context.getString(it) }
            model = SbisButtonModel(
                title = SbisButtonTitle(titleString),
                clickListener = {
                    val buttonListener = getButtonListener(containerFragment)
                    if (buttonListener != null) {
                        buttonListener.onButtonClick(confirmation.tag, buttonModel.id.toString(), containerFragment)
                    } else {
                        confirmation.buttonCallback?.invoke(containerFragment, buttonModel.id)
                    }
                }
            )
            id = buttonModel.viewId ?: View.generateViewId()
            size = SbisButtonSize.XS
            minimumWidth = minButtonWidth
            contentDescription = ConfirmationDialog.DEFAULT_BUTTON_CONTENT_DESCRIPTION
        }
    }

    private fun getMarkerColor(context: Context, style: ConfirmationDialogStyle) =
        when (style) {
            ConfirmationDialogStyle.PRIMARY ->
                context.getThemeColorInt(R.attr.confirmationDialogMarkerPrimaryColor)

            ConfirmationDialogStyle.ERROR ->
                context.getThemeColorInt(RDesign.attr.dangerColor)

            ConfirmationDialogStyle.SUCCESS ->
                context.getThemeColorInt(RDesign.attr.successColor)

            ConfirmationDialogStyle.WARNING ->
                context.getThemeColorInt(RDesign.attr.warningColor)
        }

    private fun getBackgroundSecondColor(context: Context) =
        when (confirmation.style) {
            ConfirmationDialogStyle.PRIMARY ->
                context.getThemeColorInt(RDesign.attr.secondaryBackgroundColorConfirmation)

            ConfirmationDialogStyle.ERROR ->
                context.getThemeColorInt(RDesign.attr.dangerBackgroundColorConfirmation)

            ConfirmationDialogStyle.SUCCESS ->
                context.getThemeColorInt(RDesign.attr.successBackgroundColorConfirmation)

            ConfirmationDialogStyle.WARNING ->
                context.getThemeColorInt(RDesign.attr.warningBackgroundColorConfirmation)
        }

    private fun getButtonStyleColor() = when (confirmation.style) {
        ConfirmationDialogStyle.PRIMARY -> PrimaryButtonStyle
        ConfirmationDialogStyle.SUCCESS -> SuccessButtonStyle
        ConfirmationDialogStyle.ERROR -> DangerButtonStyle
        ConfirmationDialogStyle.WARNING -> WarningButtonStyle
    }

    private fun getButtonListener(containerFragment: SbisContainerImpl): ConfirmationButtonHandler? =
        (containerFragment.parentFragment ?: containerFragment.requireActivity()) as? ConfirmationButtonHandler

    private fun getStyleWithChangedBackgroundRes(style: SbisButtonStyle): SbisButtonStyle {
        if (style !is SbisButtonResourceStyle) return style
        val dialogButtonStyle = when (style) {
            PrimaryButtonStyle -> R.style.ConfirmationDialogPrimaryButton
            SecondaryButtonStyle -> R.style.ConfirmationDialogSecondaryButton
            SuccessButtonStyle -> R.style.ConfirmationDialogSuccessButton
            UnaccentedButtonStyle -> R.style.ConfirmationDialogUnaccentedButton
            BonusButtonStyle -> R.style.ConfirmationDialogBonusButton
            DangerButtonStyle -> R.style.ConfirmationDialogDangerButton
            WarningButtonStyle -> R.style.ConfirmationDialogWarningButton
            InfoButtonStyle -> R.style.ConfirmationDialogInfoButton
            DefaultButtonStyle -> R.style.ConfirmationDialogDefaultButton
            NavigationButtonStyle -> R.style.ConfirmationDialogNavigationButton
            PaleButtonStyle -> R.style.ConfirmationDialogPaleButton
            BrandButtonStyle -> R.style.ConfirmationDialogBrandButton
            else -> return style
        }
        return SbisButtonResourceStyle(
            style.buttonStyle,
            dialogButtonStyle,
            style.roundButtonStyle,
            style.defaultRoundButtonStyle,
            style.linkButtonStyle,
            style.defaultLinkButtonStyle
        )
    }

    private fun createGradient(context: Context, width: Int) = GradientDrawable().apply {
        gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientRadius = calculateRadius(width)
        colors = intArrayOf(
            "#00FFFFFF".toColorInt(),
            getBackgroundSecondColor(context)
        )
        setGradientCenter(.25f, -.15f)
    }

    private fun createBackgroundColor(context: Context) =
        ColorDrawable(context.getThemeColorInt(RDesign.attr.backgroundColorDialog))

    /**
     * Значение радиуса градиента. Подобрано эмпирически, так как на всех платформах параметры градиентов
     * интерпритируются по разному.
     */
    private fun calculateRadius(width: Int) = width * 1.5f

    private fun setMatchParentSize(params: ViewGroup.LayoutParams) = with(params) {
        width = MATCH_PARENT
        height = MATCH_PARENT
    }

    private fun setOldSize(params: ViewGroup.LayoutParams) = with(params) {
        width = binding.root.width
        height = binding.root.height
    }

}
