package ru.tensor.sbis.design.confirmation_dialog

import android.content.Context
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContainerViewModel
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.container.createParcelableViewContainer
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenVerticalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import java.util.UUID

/**
 * Диалог подтверждения
 *
 * @author ma.kolpakov
 */
@Parcelize
class ConfirmationDialog<BUTTON_ID : Any>(
    internal val contentProvider: ContentProvider,
    internal val buttons: () -> List<ButtonModel<BUTTON_ID>>,
    internal val style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
    internal var tag: String? = null,
    internal val buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
    private val isCancellable: Boolean = true,
    internal val onDialogViewCreated: ((Fragment) -> Unit)? = null,
    internal val showMarker: Boolean = true,
    internal val isContainerScrolled: Boolean = false,
    internal val buttonCallback: ((SbisContainerImpl, BUTTON_ID) -> Unit)? = null
) : Parcelable {
    constructor(
        message: CharSequence?,
        comment: CharSequence?,
        buttons: () -> List<ButtonModel<BUTTON_ID>>,
        customContentProvider: ((Context) -> View)? = null,
        style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
        tag: String? = null,
        buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
        isCancellable: Boolean = true,
        isContainerScrolled: Boolean = false,
        onDialogViewCreated: ((Fragment) -> Unit)? = null,
        callback: ((ContainerViewModel, BUTTON_ID) -> Unit)? = null
    ) : this(
        BaseContentProvider(
            message,
            comment,
            if (customContentProvider != null) { context, _ -> customContentProvider.invoke(context) } else null
        ),
        buttons,
        style,
        tag,
        buttonOrientation,
        isCancellable,
        onDialogViewCreated,
        true,
        isContainerScrolled = isContainerScrolled,
        buttonCallback = { sbisContainerImpl, buttonId ->
            callback?.invoke(sbisContainerImpl.getViewModel(), buttonId)
        }
    )

    constructor(
        contentProvider: ContentProvider,
        buttons: () -> List<ButtonModel<BUTTON_ID>>,
        style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
        tag: String? = null,
        buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
        isCancellable: Boolean = true,
        isContainerScrolled: Boolean = false,
        onDialogViewCreated: ((Fragment) -> Unit)? = null,
        callback: ((ContainerViewModel, BUTTON_ID) -> Unit)? = null
    ) : this(
        contentProvider,
        buttons,
        style,
        tag,
        buttonOrientation,
        isCancellable,
        onDialogViewCreated,
        true,
        isContainerScrolled = isContainerScrolled,
        buttonCallback = { sbisContainerImpl, buttonId ->
            callback?.invoke(sbisContainerImpl.getViewModel(), buttonId)
        }
    )

    @IgnoredOnParcel
    private var onDismissListener: (() -> Unit)? = null

    @IgnoredOnParcel
    var containerParameters = ContainerParameters()

    fun show(fragmentManager: FragmentManager, dimType: DimType = DimType.SOLID, isSync: Boolean = false) {
        containerParameters.dimType = dimType
        show(fragmentManager = fragmentManager, isSync = isSync)
    }

    fun show(fragmentManager: FragmentManager, isSync: Boolean = false) {
        @Suppress("UNCHECKED_CAST" /* Безопасность типов обеспечена в конструкторе */)
        val creator = ConfirmationDialogContentCreator(this as ConfirmationDialog<Any>, containerParameters.customWidth)
        createParcelableViewContainer(
            contentCreator = creator,
            tag = getOrGenerateTag()
        ).apply {
            isAnimated = true
            this.dimType = containerParameters.dimType
            setOnDismissListener(onDismissListener)
            onDismissListener = null
            isCloseOnTouchOutside = containerParameters.shouldDismissByTapOutside
            isDialogCancelable = isCancellable
            show(
                fragmentManager = fragmentManager,
                horizontalLocator = ScreenHorizontalLocator(HorizontalAlignment.CENTER),
                verticalLocator = ScreenVerticalLocator(VerticalAlignment.CENTER),
                isSync = isSync
            )
        }
    }

    fun close(fragmentManager: FragmentManager) {
        val container = fragmentManager.findFragmentByTag(tag)
        if (container is SbisContainer && container.isAdded) {
            container.getViewModel().closeContainer()
        }
    }

    fun setOnDismissListener(listener: (() -> Unit)?) {
        onDismissListener = listener
    }

    private fun getOrGenerateTag(): String {
        val result = tag ?: UUID.randomUUID().toString()
        tag = result
        return result
    }

    @Suppress("FunctionName")

    companion object UseCase {

        const val DEFAULT_BUTTON_CONTENT_DESCRIPTION = "confirmation_dialog_button"

        fun OkDialog(
            message: CharSequence?,
            comment: CharSequence?,
            style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
            buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
            tag: String? = null,
            callback: (ContainerViewModel, ConfirmationButtonId) -> Unit
        ) = ConfirmationDialog(
            message = message,
            comment = comment,
            buttons = { OK },
            customContentProvider = null,
            style = style,
            tag = tag,
            buttonOrientation = buttonOrientation,
            callback = callback
        )

        fun OkCancelDialog(
            message: CharSequence?,
            comment: CharSequence?,
            style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
            tag: String? = null,
            callback: ((ContainerViewModel, ConfirmationButtonId) -> Unit)? = null
        ) = ConfirmationDialog(
            message = message,
            comment = comment,
            buttons = { OK_CANCEL },
            customContentProvider = null,
            style = style,
            tag = tag,
            callback = callback
        )

        fun YesDialog(
            message: CharSequence?,
            comment: CharSequence?,
            style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
            buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
            tag: String? = null,
            callback: ((ContainerViewModel, ConfirmationButtonId) -> Unit)? = null
        ) = ConfirmationDialog(
            message = message,
            comment = comment,
            buttons = { YES },
            customContentProvider = null,
            style = style,
            tag = tag,
            buttonOrientation = buttonOrientation,
            callback = callback
        )

        fun YesNoDialog(
            message: CharSequence?,
            comment: CharSequence?,
            style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
            buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
            tag: String? = null,
            callback: ((ContainerViewModel, ConfirmationButtonId) -> Unit)? = null
        ) = ConfirmationDialog(
            message = message,
            comment = comment,
            buttons = { YES_NO },
            customContentProvider = null,
            style = style,
            tag = tag,
            buttonOrientation = buttonOrientation,
            callback = callback
        )

        fun YesNoCancelDialog(
            message: CharSequence?,
            comment: CharSequence?,
            style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
            buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
            tag: String? = null,
            callback: ((ContainerViewModel, ConfirmationButtonId) -> Unit)? = null
        ) = ConfirmationDialog(
            message = message,
            comment = comment,
            buttons = { YES_NO_CANCEL },
            customContentProvider = null,
            style = style,
            tag = tag,
            buttonOrientation = buttonOrientation,
            callback = callback
        )

        fun YesNoDialogCustom(
            message: CharSequence? = null,
            contentProvider: ((Context) -> View)? = null,
            buttons: List<ButtonModel<ConfirmationButtonId>> = YES_NO,
            style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY,
            buttonOrientation: ConfirmationButtonOrientation = ConfirmationButtonOrientation.AUTO,
            tag: String? = null,
            callback: ((ContainerViewModel, ConfirmationButtonId) -> Unit)? = null
        ) = ConfirmationDialog(
            message = message,
            comment = null,
            buttons = { buttons },
            customContentProvider = contentProvider,
            style = style,
            tag = tag,
            buttonOrientation = buttonOrientation,
            callback = callback
        )

        /**
         * Поиск открытого диалога по тэгу [tag] и обновление его контента.
         *
         * @param fragmentManager fragmentManager в котором отображается диалог.
         * @param tag тэг диалога, по которому будет происходить его поиск.
         * @param dialog новая модель диалога, на которую будет заменяться старая модель.
         * @param needUpdateSize нужно ли обновлять размер контейнера под новый контент или оставлять прежним.
         */
        fun <T : Any> findAndUpdateDialog(
            fragmentManager: FragmentManager,
            tag: String,
            dialog: ConfirmationDialog<T>,
            needUpdateSize: Boolean = false,
        ) {
            val container = fragmentManager.findFragmentByTag(tag)
            if (container is SbisContainerImpl && container.isAdded && container.content is ConfirmationDialogContent) {

                // Обновление ui текущего отрытого диалога.
                val content = container.content as ConfirmationDialogContent
                content.updateDialog(dialog, needUpdateSize)

                // Обновление криэйтора диалога для восстановления в случае пересоздания контейнера (поворота экрана).
                // Не используется [ContainerViewModel.showNewContent(...)], т.к. происходит промаргивание при обновлении.
                val newCreator = ConfirmationDialogContentCreator(dialog, dialog.containerParameters.customWidth)
                container.getViewModel().setNewContent(newCreator)
            }

        }
    }
}
